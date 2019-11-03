package unknow.scene.builder;

import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

import unknow.scene.builder.LoadListener.LoadEvent;
import unknow.scene.builder.builders.BuilderActor;
import unknow.scene.builder.builders.BuilderButton;
import unknow.scene.builder.builders.BuilderGroup;
import unknow.scene.builder.builders.BuilderInput;
import unknow.scene.builder.builders.BuilderLabel;
import unknow.scene.builder.builders.BuilderScroll;
import unknow.scene.builder.builders.BuilderTable;
import unknow.scene.builder.builders.BuilderTableCell;
import unknow.scene.builder.builders.BuilderTableRow;
import unknow.scene.builder.builders.BuilderWidgetGroup;

public class DynLayout extends WidgetGroup
	{
	public ScriptEngine js=new ScriptEngineManager().getEngineByName("javascript");
	private Compilable c=(Compilable)js;

	private Map<Object,CompiledScript> values=new HashMap<>();

	private static Map<String,BuilderActor> builders=new HashMap<>();
	static
		{
		builders.put("actor", new BuilderActor());
		builders.put("button", new BuilderButton());
		builders.put("group", new BuilderGroup());
		builders.put("input", new BuilderInput());
		builders.put("label", new BuilderLabel());
		builders.put("scroll", new BuilderScroll());
		builders.put("table", new BuilderTable());
		builders.put("cell", new BuilderTableCell());
		builders.put("row", new BuilderTableRow());
		builders.put("widget-group", new BuilderWidgetGroup());
		}
	private static final Schema schema;
	static
		{
		SchemaFactory factory=SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		try
			{
			schema=factory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("dyn-layout.xsd")));
			}
		catch (SAXException e)
			{
			throw new RuntimeException(e);
			}
		}

	public void load(InputSource source) throws ParserConfigurationException, SAXException, IOException, ScriptException
		{
		js.eval("for(var k in this) { if(k[0]=='$') { delete this[k];}}");
		values.clear();
		clear();

		SAXParserFactory factory=SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		SAXParser parser=factory.newSAXParser();
		Handler h=new Handler();
		parser.parse(source, h);

		LoadEvent obtain=Pools.obtain(LoadEvent.class);
		obtain.setListenerActor(this);
		fire(obtain);
		Pools.free(obtain);
		}

	public void put(String id, Object o)
		{
		js.put(id, o);
		}

	public Object get(String id)
		{
		return js.get("$"+id);
		}

	@Override
	public void layout()
		{
		try
			{
			for(Entry<Object,CompiledScript> e:values.entrySet())
				{
				js.put("a", e.getKey());
				e.getValue().eval();
				}
			}
		catch (ScriptException e)
			{
			throw new RuntimeException(e);
			}
		}

	private class Handler extends DefaultHandler
		{
		/** stack of parsed tag */
		private Deque<Object> stack=new LinkedList<>();
		private Deque<BuilderActor> build=new LinkedList<>();

		private StringBuilder sb=new StringBuilder();

		private BuilderWidgetGroup layout=new BuilderWidgetGroup()
			{
			@Override
			public Object build(Object parent, Attributes attributes) throws SAXException
				{
				return DynLayout.this;
				}
			};

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
			{
			BuilderActor b="layout".equals(localName)?layout:builders.get(localName);
			if(b==null)
				throw new SAXException("no builder for '"+localName+"'");
			build.push(b);

			Object o=b.build(stack.peek(), attributes);
			stack.push(o);

			String id=attributes.getValue("id");
			if(id!=null&&id.length()>0)
				js.put("$"+id, o);

			try
				{
				b.values(sb, attributes);
				CompiledScript compile=c.compile(sb.toString());
				sb.setLength(0);
				values.put(o, compile);

				js.put("a", o);
				compile.eval();
				String l=attributes.getValue("onload");
				if(l!=null)
					js.eval("var L=Java.type('"+LoadListener.class.getName()+"'); a.addCaptureListener(new L(function(e) {"+l+"}))");
				l=attributes.getValue("onchange");
				if(l!=null)
					js.eval("var L=Java.type('"+ChangeListener.class.getName()+"'); a.addCaptureListener(new L(function(e) {"+l+"}))");
				l=attributes.getValue("onclick");
				if(l!=null)
					js.eval("var L=Java.type('"+ClickListener.class.getName()+"'); a.addCaptureListener(new L(function(e) {"+l+"}))");

				StringBuilder sb=new StringBuilder("var L=Java.extend(Java.type('");
				sb.append(InputListener.class.getName()).append("'),{");
				int i=sb.length();
				l=attributes.getValue("onkeydown");
				if(l!=null)
					sb.append("keyDown: function(e) {"+l+"},");
				l=attributes.getValue("onkeyup");
				if(l!=null)
					sb.append("keyUp: function(e) {"+l+"},");
				l=attributes.getValue("onkeypress");
				if(l!=null)
					sb.append("keyTyped: function(e) {"+l+"},");
				if(sb.length()>i)
					{
					sb.setLength(sb.length()-1);
					sb.append("});a.addCaptureListener(new L())");
					js.eval(sb.toString());
					}
				}
			catch (ScriptException e)
				{
				throw new SAXException(e);
				}
			}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
			{
			Object pop=stack.pop();
			build.pop();
			if(!build.isEmpty())
				build.peek().child(stack.peek(), pop);
			}
		}
	}
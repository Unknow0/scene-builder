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

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import unknow.scene.builder.builders.BuilderActor;
import unknow.scene.builder.builders.BuilderButton;
import unknow.scene.builder.builders.BuilderInput;
import unknow.scene.builder.builders.BuilderLabel;
import unknow.scene.builder.builders.BuilderScroll;
import unknow.scene.builder.builders.BuilderTable;
import unknow.scene.builder.builders.BuilderTableCell;
import unknow.scene.builder.builders.BuilderTableRow;
import unknow.scene.builder.builders.BuilderWidgetGroup;

public class DynLayout extends WidgetGroup
	{
	private ScriptEngine js=new ScriptEngineManager().getEngineByName("javascript");
	private Compilable c=(Compilable)js;

	private Map<Object,CompiledScript> values=new HashMap<>();

	private static Map<String,BuilderActor> builders=new HashMap<>();
	static
		{
		builders.put("actor", new BuilderActor());
		builders.put("scroll", new BuilderScroll());
		builders.put("table", new BuilderTable());
		builders.put("row", new BuilderTableRow());
		builders.put("cell", new BuilderTableCell());
		builders.put("label", new BuilderLabel());
		builders.put("input", new BuilderInput());
		builders.put("button", new BuilderButton());
		}
	private static final Schema schema;
	static
		{
		SchemaFactory factory=SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		try
			{
			schema=factory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("scene-builder.xsd")));
			}
		catch (SAXException e)
			{
			throw new RuntimeException(e);
			}
		}

	public void load(InputSource source) throws ParserConfigurationException, SAXException, IOException
		{
		SAXParserFactory factory=SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		SAXParser parser=factory.newSAXParser();
		Handler h=new Handler();
		parser.parse(source, h);
		}

	public void put(String id, Object o)
		{
		js.put(id, o);
		}

	public Object get(String id)
		{
		return js.get("#"+id);
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
			js.put("$"+id, o);

			try
				{
				b.values(sb, attributes);
				CompiledScript compile=c.compile(sb.toString());
				sb.setLength(0);
				values.put(o, compile);

				js.put("a", o);
				compile.eval();

				String l=attributes.getValue("listener");
				if(l!=null)
					js.eval("a.addListener("+l+");");
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
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
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

import unknow.scene.builder.DynLayoutContext.Attr;
import unknow.scene.builder.LoadListener.LoadEvent;
import unknow.scene.builder.builders.BuilderActor;

/**
 * A dynamic layout from an xml source
 * 
 * @author unknow
 */
public class DynLayout extends WidgetGroup {
	private final ScriptEngine js = DynLayoutContext.MANAGER.getEngineByName("javascript");
	private final Compilable c = (Compilable) js;
	private final DynLayoutContext ctx;

	private Map<Object, CompiledScript> values = new HashMap<>();

	private float prefHeight;
	private float prefWidth;

	/**
	 * create new DynLayout
	 * 
	 * @param ctx the context to use
	 */
	public DynLayout(DynLayoutContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * load the xml layout
	 * 
	 * @param source the xml source
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ScriptException
	 */
	public void load(InputSource source) throws ParserConfigurationException, SAXException, IOException, ScriptException {
		js.eval("for(var k in this) { if(k[0]=='$') { delete this[k];}}");
		values.clear();
		clear();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser parser = factory.newSAXParser();
		Handler h = new Handler();
		parser.parse(source, h);

		LoadEvent obtain = Pools.obtain(LoadEvent.class);
		obtain.setListenerActor(this);
		fire(obtain);
		Pools.free(obtain);
	}

	/**
	 * get an actor by it's id
	 * 
	 * @param id actor id
	 * @return the actor
	 */
	public Object get(String id) {
		return js.get("$" + id);
	}

	@Override
	public void layout() {
		try {
			for (Entry<Object, CompiledScript> e : values.entrySet()) {
				js.put("a", e.getKey());
				e.getValue().eval();
			}
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}

		SnapshotArray<Actor> children = getChildren();
		int size = children.size;
		prefHeight = prefWidth = 0;
		for (int i = 0; i < size; i++) {
			Actor actor = children.get(i);
			float f = Value.prefHeight.get(actor);
			if (f > prefHeight)
				prefHeight = f;
			f = Value.prefWidth.get(actor);
			if (f > prefWidth)
				prefWidth = f;
		}
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	private class Handler extends DefaultHandler {
		/** stack of parsed tag */
		private Deque<Object> stack = new LinkedList<>();
		private Deque<BuilderActor> build = new LinkedList<>();

		private StringBuilder sb = new StringBuilder();

		private BuilderActor layout = new BuilderActor(null) {
			@Override
			public Object build(Object parent, Attributes attributes) throws SAXException {
				return DynLayout.this;
			}

			@Override
			public void child(Object self, Object child) throws SAXException {
				((DynLayout) self).addActor((Actor) child);
			};
		};

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			BuilderActor b = "layout".equals(localName) ? layout : ctx.getBuilder(localName);
			if (b == null)
				throw new SAXException("no builder for '" + localName + "'");
			build.push(b);

			Object o = b.build(stack.peek(), attributes);
			stack.push(o);

			String id = attributes.getValue("id");
			if (id != null && id.length() > 0)
				js.put("$" + id, o);

			try {
				String value = attributes.getValue("init");
				if (value != null) {
					js.put("a", o);
					js.eval(value);
				}
				buildValues(o.getClass(), attributes);

				CompiledScript compile = c.compile(sb.toString());
				sb.setLength(0);
				values.put(o, compile);

				js.put("a", o);
				// compile.eval();
				String l = attributes.getValue("onload");
				if (l != null)
					js.eval("var L=Java.type('" + LoadListener.class.getName() + "'); a.addListener(new L(function(e) {" + l + "}))");
				l = attributes.getValue("onchange");
				if (l != null)
					js.eval("var L=Java.type('" + ChangeListener.class.getName() + "'); a.addListener(new L(function(e) {" + l + "}))");
				l = attributes.getValue("onclick");
				if (l != null)
					js.eval("var L=Java.type('" + ClickListener.class.getName() + "'); a.addListener(new L(function(e) {" + l + "}))");

				StringBuilder sb = new StringBuilder("var L=Java.extend(Java.type('");
				sb.append(InputListener.class.getName()).append("'),{");
				int i = sb.length();
				l = attributes.getValue("onkeydown");
				if (l != null)
					sb.append("keyDown: function(e) {" + l + "},");
				l = attributes.getValue("onkeyup");
				if (l != null)
					sb.append("keyUp: function(e) {" + l + "},");
				l = attributes.getValue("onkeypress");
				if (l != null)
					sb.append("keyTyped: function(e) {" + l + "},");
				if (sb.length() > i) {
					sb.setLength(sb.length() - 1);
					sb.append("});a.addListener(new L())");
					js.eval(sb.toString());
				}
			} catch (ScriptException e) {
				throw new SAXException(e);
			}
		}

		private void buildValues(Class<?> clazz, Attributes attributes) {
			if (clazz == null || clazz == Object.class)
				return;
			Attr[] attrs = ctx.getValues(clazz);
			if (attrs != null) {
				for (int i = 0; i < attrs.length; i++)
					attrs[i].append(sb, attributes);
			}
			for (Class<?> i : clazz.getInterfaces())
				buildValues(i, attributes);
			buildValues(clazz.getSuperclass(), attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			Object pop = stack.pop();
			build.pop();
			if (!build.isEmpty())
				build.peek().child(stack.peek(), pop);
		}
	}
}
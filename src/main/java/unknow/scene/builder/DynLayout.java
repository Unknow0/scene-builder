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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import unknow.scene.builder.LoadListener.LoadEvent;
import unknow.scene.builder.builders.BuilderActor;
import unknow.scene.builder.builders.BuilderTableCell;
import unknow.scene.builder.builders.BuilderTableRow;

public class DynLayout extends WidgetGroup {
	private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
	public final ScriptEngine js = MANAGER.getEngineByName("javascript");
	private final Compilable c = (Compilable) js;

	private Map<Object, CompiledScript> values = new HashMap<>();

	private final static Map<String, BuilderActor> builders = new HashMap<>();
	private final Map<Class<?>, Attr[]> valuesBuilder = new HashMap<>();
	static {
		builders.put("actor", new BuilderActor(Actor.class));
		builders.put("button", new BuilderActor(Button.class, () -> new VisImageTextButton("", (Drawable) null)));
		builders.put("group", new BuilderActor(Group.class, (self, child) -> ((Group) self).addActor((Actor) child)));
		builders.put("input", new BuilderActor(VisTextField.class, () -> new VisTextField()));
		builders.put("label", new BuilderActor(Label.class, () -> new VisLabel()));
		builders.put("scroll", new BuilderActor(ScrollPane.class, () -> new VisScrollPane(null), (self, child) -> {
			ScrollPane p = (ScrollPane) self;
			if (p.getWidget() != null)
				throw new SAXException("scoll can't have more than one child");
			p.setWidget((Actor) child);
		}));
		builders.put("table", new BuilderActor(Table.class, () -> new VisTable()));
		builders.put("cell", new BuilderTableCell());
		builders.put("row", new BuilderTableRow());
	}

	public DynLayout() {
		valuesBuilder.put(Actor.class, new Attr[] { new Attr("width", "setWidth"), new Attr("height", "setHeight"), new Attr("x", "setX"), new Attr("y", "setY"), new Attr("debug", "setDebug") });
		valuesBuilder.put(Disableable.class, new Attr[] { new Attr("disabled", "setDisabled") });
		valuesBuilder.put(Button.class, new Attr[] { new Attr("text", "setText") });
		valuesBuilder.put(VisTextField.class, new Attr[] { new Attr("password", "setPasswordMode"), new Attr("length", "setMaxLength"), new Attr("placeholder", "setMessageText"), new Attr("text", "setText") });
		valuesBuilder.put(Label.class, new Attr[] { new Attr("text", "setText") });
		valuesBuilder.put(ScrollPane.class, new Attr[] { new Attr("fade", "setFadeScrollBars") });
		valuesBuilder.put(Cell.class, new Attr[] { new Attr("width"), new Attr("height"), new Attr("align"), new Attr("colspan"), new Attr("rowspan"), new Attr("expand", false), new Attr("expandY", false), new Attr("expandX", false), new Attr("fill", false), new Attr("fillX", false), new Attr("fillY", false), new Attr("pad"), new Attr("padTop"), new Attr("padBottom"), new Attr("padLeft"), new Attr("padRight") });
		valuesBuilder.put(Layout.class, new Attr[] { new Attr("fillParent", "setFillParent") });
	}

	private static final Schema schema;
	static {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		try {
			schema = factory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("dyn-layout.xsd")));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public void addValueBuilder(Class<?> clazz, Attr[] attr) {
		valuesBuilder.put(clazz, attr);
	}

	public void load(InputSource source) throws ParserConfigurationException, SAXException, IOException, ScriptException {
		js.eval("for(var k in this) { if(k[0]=='$') { delete this[k];}}");
		values.clear();
		clear();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		SAXParser parser = factory.newSAXParser();
		Handler h = new Handler();
		parser.parse(source, h);

		LoadEvent obtain = Pools.obtain(LoadEvent.class);
		obtain.setListenerActor(this);
		fire(obtain);
		Pools.free(obtain);
	}

	public void put(String id, Object o) {
		js.put(id, o);
	}

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

			public void child(Object self, Object child) throws SAXException {
				((DynLayout) self).addActor((Actor) child);
			};
		};

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			BuilderActor b = "layout".equals(localName) ? layout : builders.get(localName);
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
			Attr[] attrs = valuesBuilder.get(clazz);
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

	public static class Attr {
		private final String attribute;
		private final String method;
		private final boolean value;

		public Attr(String attribute) {
			this(attribute, attribute, true);
		}

		public Attr(String attribute, boolean value) {
			this(attribute, attribute, value);
		}

		public Attr(String attribute, String method) {
			this(attribute, method, true);
		}

		public Attr(String attribute, String method, boolean value) {
			this.attribute = attribute;
			this.method = method;
			this.value = value;
		}

		public void append(StringBuilder sb, Attributes attrs) {
			String v = attrs.getValue(attribute);
			if (v == null)
				return;
			sb.append("a.").append(method).append('(');
			if (value) {
				// if (v.length() > 0 && v.startsWith("#"))
				// sb.append('"').append(v, 1, v.length()).append('"');
				// else
				sb.append(v);
			}
			sb.append(");");
		}
	}

	public static interface ValuesBuilder {
		public void values(StringBuilder sb, Attributes attributes);

	}
}
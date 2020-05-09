package unknow.scene.builder;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import unknow.scene.builder.builders.BuilderActor;
import unknow.scene.builder.builders.BuilderTableCell;
import unknow.scene.builder.builders.BuilderTableRow;

/**
 * global context to share data between DynLayout instance
 * 
 * @author unknow
 */
public class DynLayoutContext {
	/** the ScriptEngineManger */
	public static final ScriptEngineManager MANAGER = new ScriptEngineManager();

	final ScriptEngine js = MANAGER.getEngineByName("javascript");

	private final Map<String, BuilderActor> builders = new HashMap<>();
	private final Map<Class<?>, Attr[]> values = new HashMap<>();

	/**
	 * create new DynLayoutContext with builder for:
	 * <ul>
	 * <li>actor</li>
	 * <li>button</li>
	 * <li>group</li>
	 * <li>label</li>
	 * <li>scroll</li>
	 * <li>table</li>
	 * <li>cell</li>
	 * <li>row</li>
	 * </ul>
	 */
	public DynLayoutContext() {
		builders.put("actor", new BuilderActor(Actor.class));
		builders.put("button", new BuilderActor(Button.class, () -> new VisImageTextButton("", (Drawable) null)));
		builders.put("group", new BuilderActor(Group.class, () -> new Group(), (self, child) -> ((Group) self).addActor((Actor) child)));
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

		values.put(Actor.class, new Attr[] { new Attr("width", "setWidth"), new Attr("height", "setHeight"), new Attr("x", "setX"), new Attr("y", "setY"), new Attr("debug", "setDebug") });
		values.put(Disableable.class, new Attr[] { new Attr("disabled", "setDisabled") });
		values.put(Button.class, new Attr[] { new Attr("text", "setText") });
		values.put(VisTextField.class, new Attr[] { new Attr("password", "setPasswordMode"), new Attr("length", "setMaxLength"), new Attr("placeholder", "setMessageText"), new Attr("text", "setText") });
		values.put(Label.class, new Attr[] { new Attr("text", "setText") });
		values.put(ScrollPane.class, new Attr[] { new Attr("fade", "setFadeScrollBars") });
		values.put(Cell.class, new Attr[] { new Attr("width"), new Attr("height"), new Attr("align"), new Attr("colspan"), new Attr("rowspan"), new Attr("expand", false), new Attr("expandY", false), new Attr("expandX", false), new Attr("fill", false), new Attr("fillX", false), new Attr("fillY", false), new Attr("pad"), new Attr("padTop"), new Attr("padBottom"), new Attr("padLeft"), new Attr("padRight") });
		values.put(Layout.class, new Attr[] { new Attr("fillParent", "setFillParent") });
	}

	/**
	 * add builder for a new tag
	 * 
	 * @param name    tag to add
	 * @param builder how to handle that tag
	 */
	public void addBuilder(String name, BuilderActor builder) {
		builders.put(name, builder);
	}

	/**
	 * retrieve a builder
	 * 
	 * @param name the builder to get
	 * @return the builder
	 */
	public BuilderActor getBuilder(String name) {
		return builders.get(name);
	}

	/**
	 * add value handling for a class
	 * 
	 * @param clazz the class to handle
	 * @param attr  the list off allowed attribute
	 */
	public void addValue(Class<?> clazz, Attr[] attr) {
		values.put(clazz, attr);
	}

	/**
	 * get allowed values for a class
	 * 
	 * @param clazz the class
	 * @return the allowed values
	 */
	public Attr[] getValues(Class<?> clazz) {
		return values.get(clazz);
	}

	/**
	 * put a class in the global context
	 * 
	 * @param cl the class to declare
	 * @throws ScriptException
	 */
	public void putClass(Class<?> cl) throws ScriptException {
		js.getBindings(ScriptContext.GLOBAL_SCOPE).put(cl.getSimpleName(), js.eval("Java.type('" + cl.getName() + "')"));
	}

	/**
	 * put a value in the global context
	 * 
	 * @param id the id of the value
	 * @param o  the value
	 */
	public void put(String id, Object o) {
		js.getBindings(ScriptContext.GLOBAL_SCOPE).put(id, o);
	}

	/**
	 * the handle of an xml attribute
	 * 
	 * @author unknow
	 */
	public static class Attr {
		/** the xml attribute */
		private final String attribute;
		/** the method to call */
		private final String method;
		/** true if this attribute as value */
		private final boolean value;

		/**
		 * create new Attr <br>
		 * same as <code>new Attr(attribute, attribute, true);</code>
		 * 
		 * @param attribute the attribute name
		 */
		public Attr(String attribute) {
			this(attribute, attribute, true);
		}

		/**
		 * create new Attr <br>
		 * same as <code>new Attr(attribute, attribute, value);</code>
		 * 
		 * @param attribute the attribute name
		 * @param value     if this attribute as a value
		 */
		public Attr(String attribute, boolean value) {
			this(attribute, attribute, value);
		}

		/**
		 * create new Attr <br>
		 * same as <code>new Attr(attribute, method, true);</code>
		 * 
		 * @param attribute the attribute name
		 * @param method    the method to call
		 */
		public Attr(String attribute, String method) {
			this(attribute, method, true);
		}

		/**
		 * create new Attr
		 * 
		 * @param attribute the attribute name
		 * @param method    the method to call
		 * @param value     if this attribute as a value
		 */
		public Attr(String attribute, String method, boolean value) {
			this.attribute = attribute;
			this.method = method;
			this.value = value;
		}

		/**
		 * append the js script to the StringBuilder if this attribute is found
		 * 
		 * @param sb    where to append the call
		 * @param attrs the xml attributes on the tag
		 */
		public void append(StringBuilder sb, Attributes attrs) {
			String v = attrs.getValue(attribute);
			if (v == null)
				return;
			sb.append("a.").append(method).append('(');
			if (value)
				sb.append(v);
			sb.append(");");
		}
	}
}
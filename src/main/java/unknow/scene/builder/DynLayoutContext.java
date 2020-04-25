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

public class DynLayoutContext {
	public static final ScriptEngineManager MANAGER = new ScriptEngineManager();

	final ScriptEngine js = MANAGER.getEngineByName("javascript");

	private final Map<String, BuilderActor> builders = new HashMap<>();
	private final Map<Class<?>, Attr[]> values = new HashMap<>();

	public DynLayoutContext() {
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

		values.put(Actor.class, new Attr[] { new Attr("width", "setWidth"), new Attr("height", "setHeight"), new Attr("x", "setX"), new Attr("y", "setY"), new Attr("debug", "setDebug") });
		values.put(Disableable.class, new Attr[] { new Attr("disabled", "setDisabled") });
		values.put(Button.class, new Attr[] { new Attr("text", "setText") });
		values.put(VisTextField.class, new Attr[] { new Attr("password", "setPasswordMode"), new Attr("length", "setMaxLength"), new Attr("placeholder", "setMessageText"), new Attr("text", "setText") });
		values.put(Label.class, new Attr[] { new Attr("text", "setText") });
		values.put(ScrollPane.class, new Attr[] { new Attr("fade", "setFadeScrollBars") });
		values.put(Cell.class, new Attr[] { new Attr("width"), new Attr("height"), new Attr("align"), new Attr("colspan"), new Attr("rowspan"), new Attr("expand", false), new Attr("expandY", false), new Attr("expandX", false), new Attr("fill", false), new Attr("fillX", false), new Attr("fillY", false), new Attr("pad"), new Attr("padTop"), new Attr("padBottom"), new Attr("padLeft"), new Attr("padRight") });
		values.put(Layout.class, new Attr[] { new Attr("fillParent", "setFillParent") });
	}

	public void addBuilder(String name, BuilderActor builder) {
		builders.put(name, builder);
	}

	public BuilderActor getBuilder(String name) {
		return builders.get(name);
	}

	public void addValue(Class<?> clazz, Attr[] attr) {
		values.put(clazz, attr);
	}

	public Attr[] getValues(Class<?> clazz) {
		return values.get(clazz);
	}

	public void putClass(Class<?> cl) throws ScriptException {
		js.getBindings(ScriptContext.GLOBAL_SCOPE).put(cl.getSimpleName(), js.eval("Java.type('" + cl.getName() + "')"));
	}

	public void put(String id, Object o) {
		js.getBindings(ScriptContext.GLOBAL_SCOPE).put(id, o);
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
			if (value)
				sb.append(v);
			sb.append(");");
		}
	}

	public static interface ValuesBuilder {
		public void values(StringBuilder sb, Attributes attributes);

	}
}
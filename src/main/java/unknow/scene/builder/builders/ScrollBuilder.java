package unknow.scene.builder.builders;

import java.lang.reflect.*;
import java.lang.reflect.Constructor;

import org.xml.sax.*;

import unknow.scene.builder.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.kotcrab.vis.ui.widget.*;

/**
 * Build generic actor.
 * <dl>
 * <dt>required param:</dt>
 * <dd>class</dd>
 * </dl>
 */
public class ScrollBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		try
			{
			String value=attributes.getValue("", "class");

			Class<?> clazz=VisScrollPane.class;
			if(value!=null)
				{
				clazz=Class.forName(value);

				if(!ScrollPane.class.isAssignableFrom(clazz))
					throw new SAXException("class '"+value+"' isn't an Actor");
				}
			return new ScrollWrapper(clazz.getConstructor(Actor.class), parent, new StaticAttr(attributes));
			}
		catch (NoSuchMethodException|SecurityException|ClassNotFoundException e)
			{
			throw new SAXException(e);
			}
		}

	public static class ScrollWrapper extends Wrapper<ScrollPane>
		{
		private Wrapper<?> parent;
		private Constructor<?> c;
		private Attributes attributes;

		public ScrollWrapper(Constructor<?> c, Wrapper<?> parent, Attributes attributes)
			{
			super(null);
			this.parent=parent;
			this.c=c;
			this.attributes=attributes;
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			try
				{
				object=(ScrollPane)c.newInstance(o);
				setValues(object, attributes);
				parent.add(object);
				}
			catch (InstantiationException|IllegalAccessException
					|IllegalArgumentException|InvocationTargetException e)
				{
				throw new SAXException(e);
				}
			}

		@Override
		public Actor actor()
			{
			return object;
			}
		}
	}
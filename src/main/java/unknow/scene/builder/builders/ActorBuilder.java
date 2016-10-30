package unknow.scene.builder.builders;

import org.xml.sax.*;

import unknow.scene.builder.*;

import com.badlogic.gdx.scenes.scene2d.*;

/**
 * Build generic actor.
 * <dl>
 * <dt>required param:</dt>
 * <dd>class</dd>
 * </dl>
 */
public class ActorBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		try
			{
			String value=attributes.getValue("", "class");
			if(value==null)
				throw new SAXException("missing class attribute");

			Class<?> clazz=Class.forName(value);

			if(!Actor.class.isAssignableFrom(clazz))
				throw new SAXException("class '"+value+"' isn't an Actor");

			Actor a=(Actor)clazz.newInstance();
			if(parent!=null)
				parent.add(a);
			setValues(a, attributes);
			return new ActorWrapper(a);
			}
		catch (IllegalAccessException e)
			{
			throw new SAXException(e);
			}
		catch (SecurityException e)
			{
			throw new SAXException(e);
			}
		catch (InstantiationException e)
			{
			throw new SAXException(e);
			}
		catch (ClassNotFoundException e)
			{
			throw new SAXException(e);
			}
		}

	public static class ActorWrapper extends Wrapper<Actor>
		{
		protected ActorWrapper(Actor actor)
			{
			super(actor);
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			throw new SAXException("Adding to non group actor "+object);
			}
		}
	}
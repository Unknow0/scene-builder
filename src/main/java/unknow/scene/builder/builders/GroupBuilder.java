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
public class GroupBuilder extends Builder
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

			if(!Group.class.isAssignableFrom(clazz))
				throw new SAXException("class '"+value+"' isn't an Actor");

			Group a=(Group)sceneBuilder.construct(clazz, attributes);
			setValues(a, attributes);
			if(parent!=null)
				parent.add(a);
			return new GroupWrapper(a);
			}
		catch (ClassNotFoundException e)
			{
			throw new SAXException(e);
			}
		}

	public static class GroupWrapper extends Wrapper<Group>
		{
		public GroupWrapper(Group actor)
			{
			super(actor);
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			object.addActor(o);
			}

		@Override
		public Actor actor()
			{
			return object;
			}
		}
	}
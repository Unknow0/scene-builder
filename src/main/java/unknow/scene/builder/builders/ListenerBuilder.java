package unknow.scene.builder.builders;

import org.xml.sax.*;

import com.badlogic.gdx.scenes.scene2d.*;

import unknow.scene.builder.*;

/**
 * Add a listener.
 * <dl>
 * <dt>required param:</dt>
 * <dd>class</dd>
 * <dt>or</dt>
 * <dd>ref-id</dd>
 * </dl>
 */
public class ListenerBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		String value=attributes.getValue("", "class");
		EventListener l;
		if(value!=null)
			{
			try
				{
				Class<?> clazz=Class.forName(value);
				if(!EventListener.class.isAssignableFrom(clazz))
					throw new SAXException("class '"+value+"' isn't an com.badlogic.gdx.scenes.scene2d.EventListener");
				l=(EventListener)sceneBuilder.construct(clazz, attributes);
				}
			catch (ClassNotFoundException e)
				{
				throw new SAXException(e);
				}
			}
		else
			{
			value=attributes.getValue("", "ref-id");
			if(value==null)
				throw new SAXException("listener must have a class or ref-id");
			l=sceneBuilder.getListener(value);
			if(l==null)
				throw new SAXException("failed to found listener '"+value+"'");
			}
		((Actor)parent.object()).addListener(l);
		return null;
		}
	}

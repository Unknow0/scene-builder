package unknow.scene.builder.builders;

import org.xml.sax.*;

import unknow.scene.builder.*;

/**
 * Add a listener.
 * <dl>
 * <dt>required param:</dt>
 * <dd>name</dd>
 * <dd>ref-id</dd>
 * </dl>
 */
public class InjectBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		String name=attributes.getValue("", "name");
		String id=attributes.getValue("", "ref-id");
		if(id!=null)
			{
			Object o=sceneBuilder.getActor(id);
			if(o==null)
				throw new SAXException("failed to bean with id '"+id+"'");
			Setter s=Setter.getSetter(parent.object().getClass());
			s.set(parent.object(), name, o);
			}
		return null;
		}
	}

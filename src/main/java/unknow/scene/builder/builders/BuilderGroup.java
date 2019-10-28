package unknow.scene.builder.builders;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class BuilderGroup extends BuilderActor
	{
	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(Group.class, attributes);
		if(o==null)
			throw new SAXException("missing class attribute");
		return o;
		}

	@Override
	public void child(Object self, Object child) throws SAXException
		{
		((Group)self).addActor((Actor)child);
		}
	}
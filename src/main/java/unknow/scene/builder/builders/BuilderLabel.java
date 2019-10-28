package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisLabel;

public class BuilderLabel extends BuilderActor
	{
	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(Label.class, attributes);
		if(o==null)
			o=new VisLabel();
		return o;
		}

	@Override
	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		super.values(sb, attributes);
		String s=attributes.getValue("text");
		if(s!=null)
			append(sb, "setText", s);
		}
	}
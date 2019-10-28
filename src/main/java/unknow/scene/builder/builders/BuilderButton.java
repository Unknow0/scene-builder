package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisImageTextButton;

public class BuilderButton extends BuilderActor
	{
	@Override
	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		super.values(sb, attributes);
		String s=attributes.getValue("text");
		if(s!=null)
			append(sb, "setText", s);
		}

	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(Button.class, attributes);
		if(o==null)
			o=new VisImageTextButton("", (Drawable)null);
		return o;
		}
	}
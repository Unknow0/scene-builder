package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.kotcrab.vis.ui.widget.VisTextField;

public class BuilderInput extends BuilderActor
	{
	@Override
	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		super.values(sb, attributes);
		String v=attributes.getValue("password");
		if(v!=null)
			sb.append("a.setPasswordMode("+v+");");
		v=attributes.getValue("length");
		if(v!=null)
			sb.append("a.setMaxLength("+v+");");
		v=attributes.getValue("placeholder");
		if(v!=null)
			append(sb, "setMessageText", v);
		v=attributes.getValue("text");
		if(v!=null)
			append(sb, "setText", v);
		v=attributes.getValue("disabled");
		if(v!=null)
			sb.append("a.setDisabled(").append(v).append(");");
		}

	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(TextField.class, attributes);
		if(o==null)
			o=new VisTextField();
		return o;
		}
	}
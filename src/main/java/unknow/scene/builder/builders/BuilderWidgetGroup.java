package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;

public class BuilderWidgetGroup extends BuilderGroup
	{
	@Override
	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		super.values(sb, attributes);
		String s=attributes.getValue("fillParent");
		if(s!=null)
			sb.append("a.setFillParent("+s+");");
		}
	}
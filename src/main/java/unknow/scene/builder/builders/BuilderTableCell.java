package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

public class BuilderTableCell extends BuilderActor
	{
	@SuppressWarnings("unchecked")
	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		return ((Cell<Actor>)parent).getTable().add();
		}

	@Override
	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		String s=attributes.getValue("width");
		if(s!=null)
			sb.append("a.width("+s+");");
		s=attributes.getValue("height");
		if(s!=null)
			sb.append("a.height("+s+");");
		s=attributes.getValue("x");
		s=attributes.getValue("align");
		if(s!=null)
			sb.append("a.align(com.badlogic.gdx.utils.Align."+s+");");
		s=attributes.getValue("colspan");
		if(s!=null)
			sb.append("a.colspan("+s+");");
		s=attributes.getValue("rowspan");
		if(s!=null)
			sb.append("a.rowspan("+s+");");
		s=attributes.getValue("expand");
		if(s!=null)
			sb.append("a.expand();");
		s=attributes.getValue("expandY");
		if(s!=null)
			sb.append("a.expandY();");
		s=attributes.getValue("expandX");
		if(s!=null)
			sb.append("a.expandX();");
		s=attributes.getValue("fill");
		if(s!=null)
			sb.append("a.fill();");
		s=attributes.getValue("fillY");
		if(s!=null)
			sb.append("a.fillY();");
		s=attributes.getValue("fillX");
		if(s!=null)
			sb.append("a.fillX();");
		}

	@SuppressWarnings("unchecked")
	@Override
	public void child(Object self, Object child) throws SAXException
		{
		((Cell<Actor>)self).setActor((Actor)child);
		}
	}
package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.kotcrab.vis.ui.widget.VisScrollPane;

public class BuilderScroll extends BuilderWidgetGroup
	{
	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(ScrollPane.class, attributes);
		if(o==null)
			o=new VisScrollPane(null);
		return o;
		}

	@Override
	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		super.values(sb, attributes);
		String s=attributes.getValue("fade");
		if(s!=null)
			sb.append("a.setFadeScrollBars("+s+");");

		}

	@Override
	public void child(Object self, Object child) throws SAXException
		{
		ScrollPane s=(ScrollPane)self;
		if(s.getWidget()!=null)
			throw new SAXException("scroll can't have more than one child");
		s.setWidget((Actor)child);
		}
	}
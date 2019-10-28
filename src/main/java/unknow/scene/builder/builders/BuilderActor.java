package unknow.scene.builder.builders;

import javax.script.ScriptException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class BuilderActor
	{
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(Actor.class, attributes);
		if(o==null)
			throw new SAXException("missing 'class'");
		return o;
		}

	protected Object tryCreate(Class<?> expected, Attributes attributes) throws SAXException
		{
		String v=attributes.getValue("class");
		if(v==null)
			return null;
		try
			{
			Class<?> clazz=Class.forName(v);

			if(!expected.isAssignableFrom(clazz))
				throw new SAXException("class '"+v+"' isn't an "+expected.getName());

			return clazz.newInstance();
			}
		catch (Exception e)
			{
			if(!(e instanceof SAXException))
				e=new SAXException(e);
			throw (SAXException)e;
			}
		}

	public void values(StringBuilder sb, Attributes attributes) throws ScriptException
		{
		String s=attributes.getValue("width");
		if(s!=null)
			sb.append("a.setWidth("+s+");");
		s=attributes.getValue("height");
		if(s!=null)
			sb.append("a.setHeight("+s+");");
		s=attributes.getValue("x");
		if(s!=null)
			sb.append("a.setX("+s+");");
		s=attributes.getValue("y");
		if(s!=null)
			sb.append("a.setY("+s+");");
		s=attributes.getValue("debug");
		if(s!=null)
			sb.append("a.setDebug("+s+");");
		}

	public void child(Object self, Object child) throws SAXException
		{
		}

	protected void append(StringBuilder sb, String method, String v)
		{
		sb.append("a.").append(method).append('(');
		if(v.length()>0&&v.startsWith("#"))
			sb.append(v, 1, v.length());
		else
			sb.append('"').append(v).append('"');
		sb.append(");");
		}
	}
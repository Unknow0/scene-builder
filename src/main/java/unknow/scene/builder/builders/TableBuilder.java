package unknow.scene.builder.builders;

import org.xml.sax.*;

import unknow.scene.builder.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.kotcrab.vis.ui.widget.*;

public class TableBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		try
			{
			String value=attributes.getValue("", "class");
			Table a;
			if(value!=null)
				{
				Class<?> clazz=Class.forName(value);

				if(!Table.class.isAssignableFrom(clazz))
					throw new SAXException("class '"+value+"' isn't a Table");

				a=(Table)clazz.newInstance();
				}
			else
				a=new VisTable();
			if(parent!=null)
				parent.add(a);
			setValues(a, attributes);
			return new TableWrapper(a);
			}
		catch (IllegalAccessException e)
			{
			throw new SAXException(e);
			}
		catch (SecurityException e)
			{
			throw new SAXException(e);
			}
		catch (InstantiationException e)
			{
			throw new SAXException(e);
			}
		catch (ClassNotFoundException e)
			{
			throw new SAXException(e);
			}
		}

	public static class RowBuilder extends Builder
		{
		@Override
		public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
			{
			if(!(parent instanceof TableWrapper))
				throw new SAXException("row isn't in table");
			Cell<?> row=((Table)parent.object()).row();
			setValues(row, attributes);
			return parent;
			}
		}

	public static class TableWrapper extends Wrapper<Table>
		{
		protected TableWrapper(Table actor)
			{
			super(actor);
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			object.add(o);
			}
		}
	}
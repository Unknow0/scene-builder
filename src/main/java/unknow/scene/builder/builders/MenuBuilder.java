package unknow.scene.builder.builders;

import org.xml.sax.*;

import unknow.scene.builder.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.kotcrab.vis.ui.widget.*;

public class MenuBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		PopupMenu menu;
		if(parent instanceof MenuBarWrapper)
			{
			String value=attributes.getValue("", "name");
			if(value==null)
				throw new SAXException("Menu need a name");
			menu=new Menu(value);
			}
		else
			menu=new PopupMenu();

		setValues(menu, attributes);
		if(parent!=null)
			parent.add(menu);
		return new MenuWrapper(menu);
		}

	public static class SeparatorBuilder extends Builder
		{
		@Override
		public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
			{
			if(!(parent instanceof MenuWrapper))
				throw new SAXException("can't add separator in '"+parent.object()+"'");
			((Menu)parent.object()).addSeparator();
			return new ActorBuilder.ActorWrapper(null);
			}
		}

	public static class MenuBarBuilder extends Builder
		{
		@Override
		public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
			{
			MenuBar menuBar=new MenuBar();
			setValues(menuBar, attributes);
			if(parent!=null)
				parent.add(menuBar.getTable());
			return new MenuBarWrapper(menuBar);
			}
		}

	public static class MenuItemBuilder extends Builder
		{
		@Override
		public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
			{
			String value=attributes.getValue("", "name");
			if(value==null)
				throw new SAXException("Menu need a name");
			MenuItem menu=new MenuItem(value);
			setValues(menu, attributes);
			if(parent!=null)
				parent.add(menu);
			return new MenuItemWrapper(menu);
			}
		}

	public static class MenuBarWrapper extends Wrapper<MenuBar>
		{
		protected MenuBarWrapper(MenuBar actor)
			{
			super(actor);
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			if(!(o instanceof Menu))
				throw new SAXException("not a Menu '"+o+"'");
			object.addMenu((Menu)o);
			}

		@Override
		public Actor actor()
			{
			return object.getTable();
			}
		}

	public static class MenuWrapper extends Wrapper<PopupMenu>
		{
		protected MenuWrapper(PopupMenu actor)
			{
			super(actor);
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			if(!(o instanceof MenuItem))
				throw new SAXException("not a MenuItem '"+o+"'");
			object.addItem((MenuItem)o);
			}

		@Override
		public Actor actor()
			{
			return object;
			}
		}

	public static class MenuItemWrapper extends Wrapper<MenuItem>
		{
		protected MenuItemWrapper(MenuItem actor)
			{
			super(actor);
			}

		@Override
		public void add(Actor o) throws SAXException
			{
			if(!(o instanceof PopupMenu))
				throw new SAXException("not a PopupMenu '"+o+"'");
			object.setSubMenu((PopupMenu)o);
			}

		@Override
		public Actor actor()
			{
			return object;
			}
		}
	}
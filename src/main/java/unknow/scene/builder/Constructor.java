package unknow.scene.builder;

import org.xml.sax.*;

import com.kotcrab.vis.ui.widget.file.*;

public interface Constructor<T>
	{
	public T construct(Class<? extends T> clazz, Attributes attr) throws SAXException;

	public static class NameConstructor<T> implements Constructor<T>
		{
		java.lang.reflect.Constructor<? extends T> c;

		public NameConstructor(Class<? extends T> clazz) throws NoSuchMethodException, SecurityException
			{
			try
				{
				c=clazz.getConstructor(String.class);
				}
			catch (NoSuchMethodException e)
				{
				c=clazz.getConstructor(CharSequence.class);
				}
			}

		@Override
		public T construct(Class<? extends T> clazz, Attributes attr) throws SAXException
			{
			try
				{
				String value=attr.getValue("", "name");
				return c.newInstance(value);
				}
			catch (Exception e)
				{
				throw new SAXException(e);
				}
			}
		}

	public static class FileChooserConstructor implements Constructor<FileChooser>
		{
		@Override
		public FileChooser construct(Class<? extends FileChooser> clazz, Attributes attr) throws SAXException
			{
			try
				{
				java.lang.reflect.Constructor<? extends FileChooser> c=clazz.getConstructor(String.class, FileChooser.Mode.class);
				return c.newInstance(attr.getValue("", "name"), FileChooser.Mode.valueOf(attr.getValue("", "mode")));
				}
			catch (Exception e)
				{
				throw new SAXException(e);
				}
			}
		}
	}
package unknow.scene.builder;

import org.xml.sax.*;

public interface Constructor
	{
	public Object construct(Class<?> clazz, Attributes attr) throws SAXException;

	public static class NameConstructor implements Constructor
		{
		java.lang.reflect.Constructor<?> c;

		public NameConstructor(Class<?> clazz) throws NoSuchMethodException, SecurityException
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
		public Object construct(Class<?> clazz, Attributes attr) throws SAXException
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
	}
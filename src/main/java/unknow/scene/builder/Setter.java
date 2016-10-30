package unknow.scene.builder;

import java.util.*;

import com.esotericsoftware.reflectasm.*;

/**
 * Utilitary class that will manage setting parameter.
 */
public class Setter
	{
	/** cache of setter per class */
	private static Map<Class<?>,Setter> setters=new HashMap<Class<?>,Setter>();

	/**
	 * return a cached setter or create a new one
	 */
	public static Setter getSetter(Class<?> clazz)
		{
		synchronized (setters)
			{
			Setter s=setters.get(clazz);
			if(s==null)
				{
				s=new Setter(clazz);
				setters.put(clazz, s);
				}
			return s;
			}
		}

	private MethodAccess acc;
	private Map<String,Info> setter;

	private Setter(Class<?> c)
		{
		setter=new HashMap<String,Info>();
		acc=MethodAccess.get(c);
		Class<?>[][] param=acc.getParameterTypes();
		String[] m=acc.getMethodNames();
		for(int i=0; i<m.length; i++)
			{
			if(param[i].length<=1)
				setter.put(m[i], new Info(i, param[i].length==0?null:param[i][0]));
			}
		}

	/**
	 * set a parameter
	 * @param o the object to act on.
	 * @param param the param to set
	 * @value the value (ignored in case of no arg param)
	 */
	public boolean set(Object o, String param, String value)
		{
		Info i=setter.get(param);
		if(i==null)
			{
			String s="set"+Character.toUpperCase(param.charAt(0))+param.substring(1);
			i=setter.get(s);
			if(i==null)
				return false;
			}
		if(i.param==null)
			acc.invoke(o, i.id);
		else
			acc.invoke(o, i.id, i.cast(value));
		return true;
		}

	/**
	 * class holding info per parameter
	 */
	private static class Info
		{
		int id;
		Class<?> param;

		public Info(int id, Class<?> param)
			{
			this.id=id;
			this.param=param;
			}

		public Object cast(String v)
			{
			if(param.isAssignableFrom(String.class))
				return v;
			if(param==Boolean.class||param==boolean.class)
				return Boolean.parseBoolean(v);
			if(param==Integer.class||param==int.class)
				return Integer.parseInt(v);
			if(param==Long.class||param==long.class)
				return Long.parseLong(v);

			throw new ClassCastException("can't convert '"+v+"' into '"+param.getName()+"'");
			}
		}
	}

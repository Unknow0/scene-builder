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
	private Map<String,List<Info>> setter;

	private Setter(Class<?> c)
		{
		setter=new HashMap<String,List<Info>>();
		acc=MethodAccess.get(c);
		Class<?>[][] param=acc.getParameterTypes();
		String[] m=acc.getMethodNames();
		for(int i=0; i<m.length; i++)
			{
			Class<?>[] p=param[i];
			int l=p.length;
			if(l<=1)
				{
				String name=m[i];
				List<Info> list=setter.get(name);
				if(list==null)
					{
					list=new ArrayList<Info>(1);
					setter.put(name, list);
					}
				list.add(new Info(i, l==0?null:p[0]));
				}
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
		boolean b=false;
		List<Info> info=setter.get(param);
		if(info!=null&&!info.isEmpty())
			b=trySet(o, info, value);
		if(!b)
			{
			String s="set"+Character.toUpperCase(param.charAt(0))+param.substring(1);
			info=setter.get(s);
			if(info!=null&&!info.isEmpty())
				trySet(o, info, value);
			}
		return b;
		}

	private boolean trySet(Object o, List<Info> info, String value)
		{
		Iterator<Info> it=info.iterator();

		while (it.hasNext())
			{
			Info i=it.next();
			if(i.param!=null)
				{
				try
					{
					acc.invoke(o, i.id, i.cast(value));
					return true;
					}
				catch (ClassCastException e)
					{
					it.remove();
					}
				}
			else
				{
				acc.invoke(o, i.id);
				return true;
				}
			}
		return false;
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
			if(param==Float.class||param==float.class)
				return Float.parseFloat(v);
			if(param==Double.class||param==double.class)
				return Double.parseDouble(v);

			throw new ClassCastException("can't convert '"+v+"' into '"+param.getName()+"'");
			}
		}
	}

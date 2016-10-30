package unknow.scene.builder;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.kotcrab.vis.ui.widget.*;

/**
 * Entry point to build scene
 */
public class SceneBuilder
	{
	/** listeners that can be use */
	private Map<String,EventListener> listeners=new HashMap<String,EventListener>();
	/** actor created with a name */
	private Map<String,Object> actors=new HashMap<String,Object>();

	private Map<Class<?>,Constructor> contructors=new HashMap<Class<?>,Constructor>();

	public SceneBuilder()
		{
		try
			{
			contructors.put(VisLabel.class, new Constructor.NameConstructor(VisLabel.class));
			contructors.put(VisTextButton.class, new Constructor.NameConstructor(VisTextButton.class));
			contructors.put(VisTextArea.class, new Constructor.NameConstructor(VisTextArea.class));
			}
		catch (NoSuchMethodException|SecurityException e)
			{
			throw new RuntimeException(e);
			}
		}

	/**
	 * @param listeners listeners to add
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public SceneBuilder(Map<String,EventListener> listeners)
		{
		this();
		this.listeners.putAll(listeners);
		}

	/**
	 * add listener
	 */
	public void addListener(String name, EventListener listener)
		{
		listeners.put(name, listener);
		}

	public void addActor(String name, Object a)
		{
		actors.put(name, a);
		}

	public EventListener getListener(String name)
		{
		return listeners.get(name);
		}

	public Object getActor(String name)
		{
		return actors.get(name);
		}

	public void addConstrtuctor(Class<?> clazz, Constructor constructor)
		{
		contructors.put(clazz, constructor);
		}

	public Object build(String xml) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(new StringReader(xml)));
		}

	public Object build(InputStream is) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(is));
		}

	public Object build(File file) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException
		{
		try (InputStream is=new FileInputStream(file))
			{
			return build(is);
			}
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public Object build(InputSource source) throws ParserConfigurationException, SAXException, IOException
		{
		Wrapper<?> w=buildWrapper(source);
		return w==null?null:w.object;
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public Wrapper<?> buildWrapper(InputSource source) throws ParserConfigurationException, SAXException, IOException
		{
		SAXParser parser=SAXParserFactory.newInstance().newSAXParser();
		Handler h=new Handler(this);
		parser.parse(source, h);
		return h.root();
		}

	/**
	 * empty actor & listeners
	 */
	public void clear()
		{
		actors.clear();
		listeners.clear();
		}

	public Object construct(Class<?> clazz, Attributes attr) throws SAXException
		{
		try
			{
			Constructor c=contructors.get(clazz);
			return c==null?clazz.newInstance():c.construct(clazz, attr);
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
		}
	}

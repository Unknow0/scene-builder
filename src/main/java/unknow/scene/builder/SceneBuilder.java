package unknow.scene.builder;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.kotcrab.vis.ui.widget.*;

/**
 * Entry point to build scene
 */
public class SceneBuilder
	{
	/** listeners that can be used */
	private Map<String,EventListener> listeners=new HashMap<String,EventListener>();
	/** actor created with an id */
	private Map<String,Object> actors=new HashMap<String,Object>();

	private Map<Class<?>,Constructor> contructors=new HashMap<Class<?>,Constructor>();

	private static final Schema schema;
	static
		{
		SchemaFactory factory=SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		try
			{
			schema=factory.newSchema(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("scene-builder.xsd")));
			}
		catch (SAXException e)
			{
			throw new RuntimeException(e);
			}
		}

	public SceneBuilder()
		{
		try
			{
			contructors.put(VisLabel.class, new Constructor.NameConstructor(VisLabel.class));
			contructors.put(VisTextButton.class, new Constructor.NameConstructor(VisTextButton.class));
			contructors.put(VisWindow.class, new Constructor.NameConstructor(VisWindow.class));
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
	public void addListener(String id, EventListener listener)
		{
		listeners.put(id, listener);
		}

	public void addActor(String id, Object a)
		{
		actors.put(id, a);
		}

	public EventListener getListener(String id)
		{
		return listeners.get(id);
		}

	/**
	 * get Actor with this id or null if not found
	 */
	public Object getActorNull(String id)
		{
		return actors.get(id);
		}

	/**
	 * get Actor with this id
	 * @throws NoSuchElementException if no actor found
	 */
	public Object getActor(String id) throws NoSuchElementException
		{
		Object o=actors.get(id);
		if(o==null)
			throw new NoSuchElementException("No actor with id '"+id+"'");
		return o;
		}

	public void addConstrtuctor(Class<?> clazz, Constructor constructor)
		{
		contructors.put(clazz, constructor);
		}

	public Object build(String xml) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(new StringReader(xml)), null);
		}

	public Object build(InputStream is) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(is), null);
		}

	public Object build(File file) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException
		{
		try (InputStream is=new FileInputStream(file))
			{
			return build(is, null);
			}
		}

	public Object build(String xml, Group root) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(new StringReader(xml)), root);
		}

	public Object build(InputStream is, Group root) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(is), root);
		}

	public Object build(File file, Group root) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException
		{
		try (InputStream is=new FileInputStream(file))
			{
			return build(is, root);
			}
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public Object build(InputSource source) throws ParserConfigurationException, SAXException, IOException
		{
		return build(source, null);
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public Object build(InputSource source, Group root) throws ParserConfigurationException, SAXException, IOException
		{
		Wrapper<?> w=buildWrapper(source, root==null?null:new Wrapper.GroupWrapper(root));
		return w==null?null:w.object;
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public Wrapper<?> buildWrapper(InputSource source, Wrapper<?> root) throws ParserConfigurationException, SAXException, IOException
		{
		SAXParserFactory factory=SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		SAXParser parser=factory.newSAXParser();
		Handler h=new Handler(this, root);
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

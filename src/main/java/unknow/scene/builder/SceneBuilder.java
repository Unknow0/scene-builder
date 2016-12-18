package unknow.scene.builder;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.xml.sax.*;

import unknow.scene.builder.builders.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.*;

/**
 * Entry point to build scene
 */
public class SceneBuilder
	{
	/** actor created with an id */
	private Map<String,Object> actors=new HashMap<String,Object>();

	private Map<Class<?>,Constructor<?>> constructors=new HashMap<Class<?>,Constructor<?>>();

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
			constructors.put(VisLabel.class, new Constructor.NameConstructor<VisLabel>(VisLabel.class));
			constructors.put(VisTextButton.class, new Constructor.NameConstructor<VisTextButton>(VisTextButton.class));
			constructors.put(VisWindow.class, new Constructor.NameConstructor<VisWindow>(VisWindow.class));
			constructors.put(FileChooser.class, new Constructor.FileChooserConstructor());
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
	public SceneBuilder(Map<String,Object> actors)
		{
		this();
		this.actors.putAll(actors);
		}

	public void addActor(String id, Object a)
		{
		actors.put(id, a);
		}

	/**
	 * get Actor with this id or null if not found
	 */
	@SuppressWarnings("unchecked")
	public <T> T getActorNull(String id)
		{
		return (T)actors.get(id);
		}

	/**
	 * get Actor with this id
	 * @throws NoSuchElementException if no actor found
	 */
	public <T> T getActor(String id) throws NoSuchElementException
		{
		T o=getActorNull(id);
		if(o==null)
			throw new NoSuchElementException("No actor with id '"+id+"'");
		return o;
		}

	public <T> void addConstrtuctor(Class<T> clazz, Constructor<T> constructor)
		{
		constructors.put(clazz, constructor);
		}

	public <T> T build(String resource) throws SAXException, IOException, ParserConfigurationException
		{
		return build(resource, null);
		}

	public <T> T build(InputStream is) throws SAXException, IOException, ParserConfigurationException
		{
		return build(is, null);
		}

	public <T> T build(File file) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException
		{
		return build(file, null);
		}

	public <T> T build(String resource, Group root) throws SAXException, IOException, ParserConfigurationException
		{
		try (InputStream is=this.getClass().getClassLoader().getResourceAsStream(resource))
			{
			return build(new InputSource(is), root);
			}
		}

	public <T> T build(InputStream is, Group root) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(is), root);
		}

	public <T> T build(File file, Group root) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException
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
	public <T> T build(InputSource source) throws ParserConfigurationException, SAXException, IOException
		{
		return build(source, null);
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public <T> T build(InputSource source, Group root) throws ParserConfigurationException, SAXException, IOException
		{
		Wrapper<T> w=buildWrapper(source, root==null?null:new GroupBuilder.GroupWrapper(root));
		return w==null?null:w.object;
		}

	/**
	 * parse and construct the tree
	 * @return the root element
	 */
	public <T> Wrapper<T> buildWrapper(InputSource source, Wrapper<?> root) throws ParserConfigurationException, SAXException, IOException
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
	 * empty actor
	 */
	public void clear()
		{
		actors.clear();
		}

	@SuppressWarnings("unchecked")
	public <T> T construct(Class<T> clazz, Attributes attr) throws SAXException
		{
		try
			{
			Constructor<T> c=(Constructor<T>)constructors.get(clazz);
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

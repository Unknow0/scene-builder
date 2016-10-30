package unknow.scene.builder;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * Entry point to build scene
 */
public class SceneBuilder
	{
	/** listeners that can be use */
	private Map<String,EventListener> listeners=new HashMap<String,EventListener>();
	/** actor created with a name */
	private Map<String,Object> actors=new HashMap<String,Object>();

	public SceneBuilder()
		{
		}

	/**
	 * @param listeners listeners to add
	 */
	public SceneBuilder(Map<String,EventListener> listeners)
		{
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

	public Actor build(String xml) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(new StringReader(xml)));
		}

	public Actor build(InputStream is) throws SAXException, IOException, ParserConfigurationException
		{
		return build(new InputSource(is));
		}

	public Actor build(File file) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException
		{
		try (InputStream is=new FileInputStream(file))
			{
			return build(is);
			}
		}

	public Actor build(InputSource source) throws ParserConfigurationException, SAXException, IOException
		{
		SAXParser parser=SAXParserFactory.newInstance().newSAXParser();
		Handler h=new Handler(this);
		parser.parse(source, h);
		return (Actor)h.root();
		}
	}

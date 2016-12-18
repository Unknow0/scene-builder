package unknow.scene.builder;

import org.xml.sax.*;

import com.badlogic.gdx.scenes.scene2d.*;

/**
 * wrap object
 */
public abstract class Wrapper<T>
	{
	protected T object;

	protected Wrapper(T actor)
		{
		this.object=actor;
		}

	/**
	 * add and actor into this element
	 */
	public abstract void add(Actor o) throws SAXException;

	/**
	 * return this object as actor
	 */
	public abstract Actor actor();

	public T object()
		{
		return object;
		}
	}

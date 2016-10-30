package unknow.scene.builder;

import org.xml.sax.*;

import com.badlogic.gdx.scenes.scene2d.*;

/**
 * wrap object int scene
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

	public T object()
		{
		return object;
		}

	}

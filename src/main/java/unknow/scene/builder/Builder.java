package unknow.scene.builder;

import java.util.*;

import org.slf4j.*;
import org.xml.sax.*;

/**
 * Build Actor from xml
 */
public abstract class Builder
	{
	private static final Logger log=LoggerFactory.getLogger(Builder.class);
	private static final List<String> EXCLUDED=Arrays.asList("class");

	/**
	 * Build apropriate element
	 * @return the builded element
	 */
	public abstract Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException;

	/**
	 * call the setter from attribute.
	 * (ignore attribute not found)
	 */
	protected void setValues(Object o, Attributes attr)
		{
		Setter s=Setter.getSetter(o.getClass());
		for(int i=0; i<attr.getLength(); i++)
			{
			String name=attr.getQName(i);
			if(EXCLUDED.contains(name))
				continue;
			String value=attr.getValue(i);
			boolean set=s.set(o, name, value);
			log.trace("{} setting value {}={} {}", o, name, value, set);
			}
		}
	}

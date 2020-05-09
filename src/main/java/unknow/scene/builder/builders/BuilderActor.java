package unknow.scene.builder.builders;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Builder for actor
 * 
 * @author unknow
 */
public class BuilderActor {
	private static final Supplier NO_DEFAULT = () -> null;
	private static final BiConsumer NO_CHILD = (a, b) -> {
	};
	protected final Class<?> expected;
	protected final Supplier defaultValue;
	protected final BiConsumer append;

	/**
	 * create new BuilderActor with no default and no child allowed
	 * 
	 * @param expected the expected root class created by this builder
	 */
	public BuilderActor(Class<?> expected) {
		this(expected, NO_DEFAULT, NO_CHILD);
	}

	/**
	 * create new BuilderActor with no child
	 * 
	 * @param expected     the expected root class created by this builder
	 * @param defaultValue called when no class attribute found
	 */
	public BuilderActor(Class<?> expected, Supplier defaultValue) {
		this(expected, defaultValue, NO_CHILD);
	}

	/**
	 * create new BuilderActor with no default
	 * 
	 * @param expected the expected root class created by this builder
	 * @param append   how to handle child (self, child)
	 */
	public BuilderActor(Class<?> expected, BiConsumer append) {
		this(expected, NO_DEFAULT, append);
	}

	/**
	 * create new BuilderActor
	 * 
	 * @param expected     the expected root class created by this builder
	 * @param defaultValue called when no class attribute found
	 * @param append       how to handle child (self, child)
	 */
	public BuilderActor(Class<?> expected, Supplier defaultValue, BiConsumer append) {
		this.expected = expected;
		this.defaultValue = defaultValue;
		this.append = append;
	}

	/**
	 * create this object
	 * 
	 * @param parent     the parent actor
	 * @param attributes xml attributes
	 * @return the created object
	 * @throws SAXException
	 */
	public Object build(@SuppressWarnings("unused") Object parent, Attributes attributes) throws SAXException {
		String v = attributes.getValue("class");
		if (v == null) {
			Object t = defaultValue.get();
			if (t == null)
				throw new SAXException("missing 'class' attribute");
			return t;
		}
		try {
			Class<?> clazz = Class.forName(v);

			if (!expected.isAssignableFrom(clazz))
				throw new SAXException("class '" + v + "' isn't an " + expected.getName());

			return clazz.newInstance();
		} catch (Exception e) {
			if (!(e instanceof SAXException))
				e = new SAXException(e);
			throw (SAXException) e;
		}
	}

	/**
	 * handle child building
	 * 
	 * @param self  created object
	 * @param child the child to add
	 * @throws SAXException
	 */
	public void child(Object self, Object child) throws SAXException {
		append.accept(self, child);
	}

	public static interface BiConsumer {
		public void accept(Object a, Object b) throws SAXException;
	}

	public static interface Supplier {
		public Object get() throws SAXException;
	}
}
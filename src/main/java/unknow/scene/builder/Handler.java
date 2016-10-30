package unknow.scene.builder;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import unknow.scene.builder.builders.*;

/**
 * Sax handler used to parse scene
 */
public class Handler extends DefaultHandler
	{
	/** builder per tag */
	private static Map<String,Builder> builders=new HashMap<String,Builder>();
	static
		{
		builders.put("actor", new ActorBuilder());
		builders.put("table", new TableBuilder());
		builders.put("row", new TableBuilder.RowBuilder());
		builders.put("cell", new TableBuilder.CellBuilder());

		builders.put("menuBar", new MenuBuilder.MenuBarBuilder());
		builders.put("menu", new MenuBuilder());
		builders.put("menuItem", new MenuBuilder.MenuItemBuilder());
		builders.put("separator", new MenuBuilder.SeparatorBuilder());

		builders.put("listener", new ListenerBuilder());
		builders.put("include", new IncludeBuilder());
		}

	/** stack of parsed tag */
	private Deque<Wrapper<?>> stack=new LinkedList<Wrapper<?>>();
	/** first element found */
	private Wrapper<?> root;
	/** the sceneBuilder to add actor & get listener */
	private SceneBuilder sceneBuilder;

	public Handler(SceneBuilder sceneBuilder)
		{
		this.sceneBuilder=sceneBuilder;
		}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
		Wrapper<?> parent=stack.peekFirst();

		Builder builder=builders.get(qName);
		if(builder==null)
			throw new SAXException("invalid tag '"+qName+"'");

		Wrapper<?> a=builder.build(sceneBuilder, parent, attributes);
		if(root==null)
			root=a;
		stack.offerFirst(a);

		String name=attributes.getValue("", "name");
		if(name!=null&&a!=null)
			sceneBuilder.addActor(name, a.object);
		}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
		{
		stack.pollFirst();
		}

	/**
	 * @return the root element or null
	 */
	public Wrapper<?> root()
		{
		return root;
		}
	}
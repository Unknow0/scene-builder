package unknow.scene.builder.builders;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

/**
 * add a cell inn a table
 * 
 * @author unknow
 */
public class BuilderTableCell extends BuilderActor {
	/**
	 * create new BuilderTableCell
	 */
	public BuilderTableCell() {
		super(null);
	}

	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException {
		return ((Cell<?>) parent).getTable().add();
	}

	@Override
	public void child(Object self, Object child) throws SAXException {
		((Cell<?>) self).setActor((Actor) child);
	}
}
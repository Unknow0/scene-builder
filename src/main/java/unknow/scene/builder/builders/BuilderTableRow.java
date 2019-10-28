package unknow.scene.builder.builders;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class BuilderTableRow extends BuilderTableCell
	{
	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		return ((Table)parent).row();
		}

	@Override
	public void child(Object self, Object child) throws SAXException
		{
		}
	}
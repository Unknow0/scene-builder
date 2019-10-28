package unknow.scene.builder.builders;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;

public class BuilderTable extends BuilderWidgetGroup
	{
	@Override
	public Object build(Object parent, Attributes attributes) throws SAXException
		{
		Object o=tryCreate(Table.class, attributes);
		if(o==null)
			o=new VisTable();
		return o;
		}

	@Override
	public void child(Object self, Object child) throws SAXException
		{
		}
	}
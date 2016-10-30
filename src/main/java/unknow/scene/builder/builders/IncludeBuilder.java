package unknow.scene.builder.builders;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import unknow.scene.builder.*;

public class IncludeBuilder extends Builder
	{
	@Override
	public Wrapper<?> build(SceneBuilder sceneBuilder, Wrapper<?> parent, Attributes attributes) throws SAXException
		{
		String value=attributes.getValue("", "source");
		try (InputStream is=sceneBuilder.getClass().getClassLoader().getResourceAsStream(value))
			{
			InputSource source=new InputSource(is);
			value=attributes.getValue("", "charset");
			if(value!=null)
				source.setEncoding(value);
			return sceneBuilder.buildWrapper(source, parent);
			}
		catch (IOException e)
			{
			throw new SAXException(e);
			}
		catch (ParserConfigurationException e)
			{
			throw new SAXException(e);
			}
		}
	}

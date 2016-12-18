package unknow.scene.builder;

import org.xml.sax.*;

public class StaticAttr implements Attributes
	{
	private static class Attr
		{
		String qName;
		String local;
		String uri;
		String type;
		String value;

		public Attr(String qName, String local, String uri, String type, String value)
			{
			this.qName=qName;
			this.local=local;
			this.uri=uri;
			this.type=type;
			this.value=value;
			}
		}

	private Attr[] attrs;

	public StaticAttr(Attributes a)
		{
		attrs=new Attr[a.getLength()];
		for(int i=0; i<attrs.length; i++)
			attrs[i]=new Attr(a.getQName(i), a.getLocalName(i), a.getURI(i), a.getType(i), a.getValue(i));
		}

	@Override
	public int getIndex(String qName)
		{
		for(int i=0; i<attrs.length; i++)
			{
			if(attrs[i].qName.equals(qName))
				return i;
			}
		return -1;
		}

	@Override
	public int getIndex(String local, String uri)
		{
		for(int i=0; i<attrs.length; i++)
			{
			if(attrs[i].local.equals(local)&&attrs[i].uri.equals(uri))
				return i;
			}
		return -1;
		}

	@Override
	public int getLength()
		{
		return attrs.length;
		}

	@Override
	public String getLocalName(int i)
		{
		return attrs[i].local;
		}

	@Override
	public String getQName(int i)
		{
		return attrs[i].qName;
		}

	@Override
	public String getType(int i)
		{
		return attrs[i].type;
		}

	@Override
	public String getType(String qName)
		{
		return attrs[getIndex(qName)].type;
		}

	@Override
	public String getType(String local, String uri)
		{
		return attrs[getIndex(local, uri)].type;
		}

	@Override
	public String getURI(int i)
		{
		return attrs[i].uri;
		}

	@Override
	public String getValue(int i)
		{
		return attrs[i].value;
		}

	@Override
	public String getValue(String qName)
		{
		return attrs[getIndex(qName)].value;
		}

	@Override
	public String getValue(String local, String uri)
		{
		return attrs[getIndex(local, uri)].value;
		}
	}
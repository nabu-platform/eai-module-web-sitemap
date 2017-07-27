package be.nabu.eai.module.web.sitemap.beans;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nabu.web.sitemap.types.SiteMapEntry;

@XmlRootElement(name = "urlset", namespace="http://www.sitemaps.org/schemas/sitemap/0.9")
public class SiteMapEntrySet {
	private List<SiteMapEntry> entries;

	@XmlElement(name = "url")
	public List<SiteMapEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<SiteMapEntry> entries) {
		this.entries = entries;
	}
	
	public String marshal() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			JAXBContext context = JAXBContext.newInstance(getClass());
			context.createMarshaller().marshal(this, output);
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return new String(output.toByteArray(), Charset.forName("UTF-8"));
	}
}

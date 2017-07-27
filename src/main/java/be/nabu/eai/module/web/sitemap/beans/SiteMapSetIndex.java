package be.nabu.eai.module.web.sitemap.beans;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nabu.web.sitemap.types.SiteMapEntry;

@XmlRootElement(name = "sitemapindex", namespace="http://www.sitemaps.org/schemas/sitemap/0.9")
public class SiteMapSetIndex {
	private List<SiteMapEntry> sets;

	@XmlElement(name = "sitemap")
	public List<SiteMapEntry> getSets() {
		return sets;
	}

	public void setSets(List<SiteMapEntry> sets) {
		this.sets = sets;
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

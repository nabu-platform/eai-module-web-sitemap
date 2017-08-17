package be.nabu.eai.module.web.sitemap.beans;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.libs.types.BaseTypeInstance;
import be.nabu.libs.types.binding.xml.XMLMarshaller;
import be.nabu.libs.types.java.BeanInstance;
import be.nabu.libs.types.java.BeanType;

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
		XMLMarshaller marshaller = new XMLMarshaller(new BaseTypeInstance(new BeanType<SiteMapSetIndex>(SiteMapSetIndex.class)));
		marshaller.setAllowDefaultNamespace(true);
		marshaller.setDefaultNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");
		marshaller.setPrettyPrint(true);
		marshaller.setAllowXSI(false);
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(writer, new BeanInstance<SiteMapSetIndex>(this));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
	
}

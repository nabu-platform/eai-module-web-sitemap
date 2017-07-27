package nabu.web.sitemap.types;

import java.net.URI;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entry")
public class SiteMapEntry {
	private URI uri;
	private Date lastModified;
	// between 0 and 1
	private double priority;
	private ChangeFrequency changeFrequency;
	
	@XmlElement(name = "loc")
	@NotNull
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}

	@XmlElement(name = "lastmod")
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@XmlElement(name = "priority")
	public double getPriority() {
		return priority;
	}
	public void setPriority(double priority) {
		this.priority = priority;
	}

	@XmlElement(name = "changefreq")
	public ChangeFrequency getChangeFrequency() {
		return changeFrequency;
	}
	public void setChangeFrequency(ChangeFrequency changeFrequency) {
		this.changeFrequency = changeFrequency;
	}

	public enum ChangeFrequency {
		ALWAYS,
		HOURLY,
		DAILY,
		WEEKLY,
		MONTHLY,
		YEARLY,
		NEVER
	}
}

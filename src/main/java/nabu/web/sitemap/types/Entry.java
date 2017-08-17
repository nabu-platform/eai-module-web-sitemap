package nabu.web.sitemap.types;

import java.net.URI;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.eai.module.web.sitemap.beans.SiteMapEntry.ChangeFrequency;

@XmlRootElement(name = "entry")
public class Entry {
	private URI uri;
	private Date lastModified;
	// between 0 and 1
	private Double priority;
	private ChangeFrequency changeFrequency;
	
	@NotNull
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}

	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Double getPriority() {
		return priority;
	}
	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public ChangeFrequency getChangeFrequency() {
		return changeFrequency;
	}
	public void setChangeFrequency(ChangeFrequency changeFrequency) {
		this.changeFrequency = changeFrequency;
	}
}

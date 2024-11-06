/*
* Copyright (C) 2017 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.web.sitemap.beans;

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
	private Double priority;
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
	public Double getPriority() {
		return priority;
	}
	public void setPriority(Double priority) {
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

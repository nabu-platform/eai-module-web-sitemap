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

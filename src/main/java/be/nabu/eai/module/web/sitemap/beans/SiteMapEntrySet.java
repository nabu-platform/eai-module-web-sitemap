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

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.libs.types.BaseTypeInstance;
import be.nabu.libs.types.binding.xml.XMLMarshaller;
import be.nabu.libs.types.java.BeanInstance;
import be.nabu.libs.types.java.BeanType;

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
		XMLMarshaller marshaller = new XMLMarshaller(new BaseTypeInstance(new BeanType<SiteMapEntrySet>(SiteMapEntrySet.class)));
		marshaller.setAllowDefaultNamespace(true);
		marshaller.setDefaultNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");
		marshaller.setPrettyPrint(true);
		marshaller.setAllowXSI(false);
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(writer, new BeanInstance<SiteMapEntrySet>(this));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
}

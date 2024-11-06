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

package be.nabu.eai.module.web.sitemap;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nabu.web.sitemap.types.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.web.application.WebApplication;
import be.nabu.eai.module.web.sitemap.beans.SiteMapEntry;
import be.nabu.eai.module.web.sitemap.beans.SiteMapEntrySet;
import be.nabu.eai.module.web.sitemap.beans.SiteMapSetIndex;
import be.nabu.libs.events.api.EventHandler;
import be.nabu.libs.http.HTTPCodes;
import be.nabu.libs.http.HTTPException;
import be.nabu.libs.http.api.HTTPRequest;
import be.nabu.libs.http.api.HTTPResponse;
import be.nabu.libs.http.core.DefaultHTTPResponse;
import be.nabu.libs.http.core.HTTPUtils;
import be.nabu.libs.resources.URIUtils;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.mime.impl.MimeHeader;
import be.nabu.utils.mime.impl.PlainMimeContentPart;

public class SiteMapListener implements EventHandler<HTTPRequest, HTTPResponse> {

	private WebApplication application;
	private String path;
	private SiteMap map;
	private URI uri;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String indexPath;
	private String pagePath;
	
	public SiteMapListener(WebApplication application, String path, SiteMap map, URI uri) {
		this.application = application;
		this.uri = uri;
		this.path = path == null ? "/" : path;
		this.map = map;
		indexPath = this.path;
		if (!indexPath.endsWith("/")) {
			indexPath += "/";
		}
		indexPath += map.getId() + ".xml";
		
		pagePath = this.path;
		if (!pagePath.endsWith("/")) {
			pagePath += "/";
		}
		pagePath += map.getId() + "-";
		pagePath = "\\Q" + pagePath + "\\E([0-9]+)\\.xml";
	}
	
	@Override
	public HTTPResponse handle(HTTPRequest request) {
		try {
			URI uri = HTTPUtils.getURI(request, false);
			byte [] bytes = null;
			if (uri.getPath().equals(indexPath)) {
				String siteIndex = getSiteIndex();
				if (siteIndex != null) {
					bytes = siteIndex.getBytes(Charset.forName("UTF-8"));
				}
			}
			else if (uri.getPath().matches(pagePath)) {
				int page = Integer.parseInt(uri.getPath().replaceAll(pagePath, "$1"));
				String siteMap = getSiteMap(page);
				if (siteMap != null) {
					bytes = siteMap.getBytes(Charset.forName("UTF-8"));
				}
			}
			if (bytes != null) {
				PlainMimeContentPart content = new PlainMimeContentPart(null, IOUtils.wrap(bytes, true), 
					new MimeHeader("Content-Type", "application/xml"),
					new MimeHeader("Content-Length", "" + bytes.length));
				HTTPUtils.setContentEncoding(content, request.getContent().getHeaders());
				return new DefaultHTTPResponse(request, 200, HTTPCodes.getMessage(200), content);
			}
			return null;
		}
		catch (Exception e) {
			throw new HTTPException(500, e);
		}
	}

	public WebApplication getApplication() {
		return application;
	}

	public void setApplication(WebApplication application) {
		this.application = application;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SiteMap getMap() {
		return map;
	}

	public void setMap(SiteMap map) {
		this.map = map;
	}
	
	public String getSiteIndex() {
		String content = null;
		if (map.getConfig().getCacheProvider() != null) {
			try {
				content = (String) map.getConfig().getCacheProvider().get(map.getId()).get(application.getId() + "-index");
			}
			catch (Exception e) {
				// ignore
			}
		}
		if (content == null) {
			SiteMapSetIndex index = new SiteMapSetIndex();
			List<SiteMapEntry> sets = new ArrayList<SiteMapEntry>();
			for (int i = 0; i < 1000; i++) {
				String page = getSiteMap(i);
				if (page == null) {
					break;
				}
				SiteMapEntry set = new SiteMapEntry();
				set.setUri(URIUtils.getChild(uri, map.getId() + "-" + i + ".xml"));
				sets.add(set);
			}
			index.setSets(sets);
			content = index.marshal();
			if (map.getConfig().getCacheProvider() != null) {
				try {
					map.getConfig().getCacheProvider().get(map.getId()).put(application.getId() + "-index", content);
				}
				catch (IOException e) {
					logger.error("Could not store index in cache", e);
				}
			}
		}
		return content;
	}
	
	public String getSiteMap(int page) {
		String content = null;
		if (map.getConfig().getCacheProvider() != null) {
			try {
				content = (String) map.getConfig().getCacheProvider().get(map.getId()).get(application.getId() + "-page-" + page);
			}
			catch (Exception e) {
				// ignore
			}
		}
		if (content == null) {
			// the max is 50000 entries or 50mb (unzipped)
			int amount = Math.min(map.getConfig().getEntriesPerPage() != null ? map.getConfig().getEntriesPerPage() : 50000, 50000);
			// TODO: limit per page
			List<Entry> entries = map.generate(application);
			if (entries != null && entries.size() > amount * page) {
				SiteMapEntrySet set = new SiteMapEntrySet();
				List<SiteMapEntry> result = new ArrayList<SiteMapEntry>();
				// we map the results so we can transform the URLs
				for (int i = amount * page; i < Math.min(amount * (page + 1), entries.size()); i++) {
					Entry entry = entries.get(i);
					SiteMapEntry single = new SiteMapEntry();
					single.setChangeFrequency(entry.getChangeFrequency());
					single.setLastModified(entry.getLastModified());
					single.setPriority(entry.getPriority());
					URI uri = entry.getUri();
					if (uri.getScheme() == null) {
						uri = URIUtils.getChild(this.uri, uri.toString().replaceFirst("^[/]+", ""));
					}
					single.setUri(uri);
					result.add(single);
				}
				set.setEntries(result);
				content = set.marshal();
				if (map.getConfig().getCacheProvider() != null) {
					try {
						map.getConfig().getCacheProvider().get(map.getId()).put(application.getId() + "-page-" + page, content);
					}
					catch (IOException e) {
						logger.error("Could not store index in cache", e);
					}
				}
			}
		}
		return content;
	}
	
}

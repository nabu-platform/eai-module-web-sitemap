package be.nabu.eai.module.web.sitemap;

import java.util.List;

import nabu.web.sitemap.types.SiteMapEntry;

public interface SiteMapGenerator {
	public List<SiteMapEntry> generate(Long offset, Integer limit);
}

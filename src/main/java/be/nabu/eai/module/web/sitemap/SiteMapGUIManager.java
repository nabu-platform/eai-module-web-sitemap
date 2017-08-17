package be.nabu.eai.module.web.sitemap;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class SiteMapGUIManager extends BaseJAXBGUIManager<SiteMapConfiguration, SiteMap> {

	public SiteMapGUIManager() {
		super("Site Map", SiteMap.class, new SiteMapManager(), SiteMapConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected SiteMap newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new SiteMap(entry.getId(), entry.getContainer(), entry.getRepository());
	}

	@Override
	public String getCategory() {
		return "Web";
	}
}

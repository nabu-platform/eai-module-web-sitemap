package be.nabu.eai.module.web.sitemap;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class SiteMapManager extends JAXBArtifactManager<SiteMapConfiguration, SiteMap> {

	public SiteMapManager() {
		super(SiteMap.class);
	}

	@Override
	protected SiteMap newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new SiteMap(id, container, repository);
	}

}

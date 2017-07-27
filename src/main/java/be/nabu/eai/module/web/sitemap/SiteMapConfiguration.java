package be.nabu.eai.module.web.sitemap;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.EnvironmentSpecific;
import be.nabu.eai.api.InterfaceFilter;
import be.nabu.eai.repository.api.CacheProviderArtifact;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.services.api.DefinedService;

@XmlRootElement(name = "siteMap")
public class SiteMapConfiguration {
	
	private DefinedService generatorService;
	private CacheProviderArtifact cacheProvider;
	private Integer entriesPerPage;
	
	@NotNull
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	@InterfaceFilter(implement = "be.nabu.eai.module.web.sitemap.SiteMapGenerator.generate")	
	public DefinedService getGeneratorService() {
		return generatorService;
	}
	public void setGeneratorService(DefinedService generator) {
		this.generatorService = generator;
	}

	@EnvironmentSpecific
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public CacheProviderArtifact getCacheProvider() {
		return cacheProvider;
	}
	public void setCacheProvider(CacheProviderArtifact cacheProvider) {
		this.cacheProvider = cacheProvider;
	}
	
	public Integer getEntriesPerPage() {
		return entriesPerPage;
	}
	public void setEntriesPerPage(Integer entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}
	
}

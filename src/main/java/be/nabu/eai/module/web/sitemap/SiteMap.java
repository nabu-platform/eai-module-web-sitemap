package be.nabu.eai.module.web.sitemap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nabu.web.application.types.WebApplicationInformation;
import be.nabu.eai.module.web.application.WebApplication;
import be.nabu.eai.module.web.application.WebApplicationUtils;
import be.nabu.eai.module.web.application.WebFragment;
import be.nabu.eai.module.web.application.WebFragmentConfiguration;
import be.nabu.eai.repository.EAIRepositoryUtils;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.authentication.api.Permission;
import be.nabu.libs.cache.impl.StringSerializer;
import be.nabu.libs.events.api.EventSubscription;
import be.nabu.libs.http.api.HTTPRequest;
import be.nabu.libs.http.api.HTTPResponse;
import be.nabu.libs.http.server.HTTPServerUtils;
import be.nabu.libs.resources.URIUtils;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.Element;

public class SiteMap extends JAXBArtifact<SiteMapConfiguration> implements WebFragment, StartableArtifact {

	private Map<String, EventSubscription<?, ?>> subscriptions = new HashMap<String, EventSubscription<?, ?>>();
	
	public SiteMap(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "site-map.xml", SiteMapConfiguration.class);
	}

	@Override
	public void start(WebApplication artifact, String path) throws IOException {
		if (isStarted(artifact, path)) {
			stop(artifact, path);
		}
		if (getConfig().getGeneratorService() != null) {
			WebApplicationInformation information = WebApplicationUtils.getInformation(artifact);
			String scheme = information.getSecure() != null && information.getSecure() ? "https" : "http";
			URI uri;
			try {
				uri = new URI(scheme, information.getHost() + (information.getPort() != null ? ":" + information.getPort() : ""), path, null, null);
			}
			catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			
			EventSubscription<HTTPRequest, HTTPResponse> subscription = artifact.getDispatcher().subscribe(HTTPRequest.class, new SiteMapListener(artifact, path, this, uri));
			subscription.filter(HTTPServerUtils.limitToPath(path == null ? "/" : path));
			subscriptions.put(getKey(artifact, path), subscription);
			
			String robots = artifact.getRobots();
			String entry = "Sitemap: " + URIUtils.getChild(uri, getId() + ".xml");
			if (robots == null) {
				artifact.setRobots(entry);
			}
			else {
				artifact.setRobots(robots + "\n" + entry);
			}
		}
	}

	@Override
	public void stop(WebApplication artifact, String path) {
		String key = getKey(artifact, path);
		if (subscriptions.containsKey(key)) {
			synchronized(subscriptions) {
				if (subscriptions.containsKey(key)) {
					subscriptions.get(key).unsubscribe();
					subscriptions.remove(key);
				}
			}
		}		
	}
	
	private String getKey(WebApplication artifact, String path) {
		return artifact.getId() + ":" + path;
	}

	@Override
	public List<Permission> getPermissions(WebApplication artifact, String path) {
		return new ArrayList<Permission>();
	}

	@Override
	public boolean isStarted(WebApplication artifact, String path) {
		return subscriptions.containsKey(getKey(artifact, path));
	}

	@Override
	public void start() throws IOException {
		if (!isStarted()) {
			getConfig().getCacheProvider().create(getId(), 1024l*1024l*1024l, 1024l*1024l*1024l, new StringSerializer(), new StringSerializer(), null, null);
		}
	}

	@Override
	public boolean isStarted() {
		try {
			return getConfig().getCacheProvider() == null || getConfig().getCacheProvider().get(getId()) != null;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<WebFragmentConfiguration> getFragmentConfiguration() {
		List<WebFragmentConfiguration> configurations = new ArrayList<WebFragmentConfiguration>();
		if (getConfig().getGeneratorService() != null) {
			Method method = WebApplication.getMethod(SiteMapGenerator.class, "generate");
			List<Element<?>> inputExtensions = EAIRepositoryUtils.getInputExtensions(getConfig().getGeneratorService(), method);
			for (final Element<?> extension : inputExtensions) {
				if (extension.getType() instanceof ComplexType && extension.getType() instanceof DefinedType) {
					configurations.add(new WebFragmentConfiguration() {
						@Override
						public ComplexType getType() {
							return (ComplexType) extension.getType();
						}
						@Override
						public String getPath() {
							return "/";
						}
					});
				}
			}
		}
		return configurations;
	}
}

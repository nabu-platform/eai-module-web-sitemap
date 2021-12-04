package be.nabu.eai.module.web.sitemap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import nabu.web.application.types.WebApplicationInformation;
import nabu.web.sitemap.types.Entry;
import be.nabu.eai.module.web.application.RobotEntryImpl;
import be.nabu.eai.module.web.application.WebApplication;
import be.nabu.eai.module.web.application.WebApplicationUtils;
import be.nabu.eai.module.web.application.WebFragment;
import be.nabu.eai.module.web.application.WebFragmentConfiguration;
import be.nabu.eai.module.web.application.api.RobotEntry;
import be.nabu.eai.repository.EAIRepositoryUtils;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.eai.repository.util.SystemPrincipal;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.authentication.api.Permission;
import be.nabu.libs.cache.impl.StringSerializer;
import be.nabu.libs.events.api.EventSubscription;
import be.nabu.libs.http.api.HTTPRequest;
import be.nabu.libs.http.api.HTTPResponse;
import be.nabu.libs.http.server.HTTPServerUtils;
import be.nabu.libs.resources.URIUtils;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.services.pojo.POJOUtils;
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
			boolean proxied = artifact.getConfig().getVirtualHost().getConfig().getServer().getConfig().isProxied();
			WebApplicationInformation information = WebApplicationUtils.getInformation(artifact);
			boolean secure = information.getSecure() != null && information.getSecure();
			String scheme = secure ? "https" : "http";
			URI uri;
			try {
				Integer port = proxied ? artifact.getConfig().getVirtualHost().getConfig().getServer().getConfig().getProxyPort() : information.getPort();
				uri = new URI(information.getScheme(), information.getHost() + (information.getPort() != null ? ":" + information.getPort() : ""), path, null, null);
			}
			catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			
			EventSubscription<HTTPRequest, HTTPResponse> subscription = artifact.getDispatcher().subscribe(HTTPRequest.class, new SiteMapListener(artifact, path, this, uri));
			subscription.filter(HTTPServerUtils.limitToPath(path == null ? "/" : path));
			subscriptions.put(getKey(artifact, path), subscription);
			
			artifact.getRobotEntries().add(new RobotEntryImpl("Sitemap", URIUtils.getChild(uri, getId() + ".xml").toString()));
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
		// remove from the robots file
		artifact.getRobotEntries().removeIf(new Predicate<RobotEntry>() {
			@Override
			public boolean test(RobotEntry t) {
				return t.getKey().equals("Sitemap");
			}
		});
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
	
	// TODO: need to limit this generation to the web application (only uris allowed that are relative to the host of this application and the path?)
	public List<Entry> generate(WebApplication application) {
		if (getConfig().getGeneratorService() != null) {
			SiteMapGenerator generator = getGenerator(application);
			return generator.generate(application.getId());
		}
		return null;
	}

	private SiteMapGenerator getGenerator(WebApplication application) {
		Method method = WebApplication.getMethod(SiteMapGenerator.class, "generate");
		return POJOUtils.newProxy(SiteMapGenerator.class, application.wrap(getConfig().getGeneratorService(), method), getRepository(), SystemPrincipal.ROOT);
	}
}

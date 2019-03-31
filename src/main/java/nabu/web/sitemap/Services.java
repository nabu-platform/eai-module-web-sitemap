package nabu.web.sitemap;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.validation.constraints.NotNull;

import be.nabu.eai.module.web.application.WebApplication;
import be.nabu.eai.module.web.sitemap.SiteMap;
import be.nabu.libs.services.api.ExecutionContext;
import nabu.web.sitemap.types.Entry;

@WebService
public class Services {
	
	private ExecutionContext executionContext;
	
	@WebResult(name = "entries")
	public List<Entry> entries(@NotNull @WebParam(name = "webApplicationId") String webApplicationId, @NotNull @WebParam(name = "siteMapId") String siteMapId) {
		WebApplication application = executionContext.getServiceContext().getResolver(WebApplication.class).resolve(webApplicationId);
		SiteMap siteMap = executionContext.getServiceContext().getResolver(SiteMap.class).resolve(siteMapId);
		return siteMap.generate(application);
	}
}

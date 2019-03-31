package be.nabu.eai.module.web.sitemap;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.validation.constraints.NotNull;

import nabu.web.sitemap.types.Entry;

public interface SiteMapGenerator {
	@WebResult(name = "entries")
	public List<Entry> generate(@NotNull @WebParam(name = "webApplicationId") String webApplicationId);
}

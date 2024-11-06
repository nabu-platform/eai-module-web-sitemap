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

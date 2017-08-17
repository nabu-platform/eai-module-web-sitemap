package be.nabu.eai.module.web.sitemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.nabu.eai.developer.api.InterfaceLister;
import be.nabu.eai.developer.util.InterfaceDescriptionImpl;

public class SiteMapInterfaceLister implements InterfaceLister {

	private static Collection<InterfaceDescription> descriptions = null;
	
	@Override
	public Collection<InterfaceDescription> getInterfaces() {
		if (descriptions == null) {
			synchronized(SiteMapInterfaceLister.class) {
				if (descriptions == null) {
					List<InterfaceDescription> descriptions = new ArrayList<InterfaceDescription>();
					descriptions.add(new InterfaceDescriptionImpl("Web Application", "Site Map Generator", "be.nabu.eai.module.web.sitemap.SiteMapGenerator.generate"));
					SiteMapInterfaceLister.descriptions = descriptions;
				}
			}
		}
		return descriptions;
	}

}

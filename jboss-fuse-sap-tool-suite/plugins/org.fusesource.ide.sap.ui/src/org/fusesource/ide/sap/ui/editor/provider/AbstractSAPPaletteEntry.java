/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.sap.ui.editor.provider;

import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_GROUP_ID;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;

public abstract class AbstractSAPPaletteEntry implements ICustomPaletteEntry{

	@Override
	public List<Dependency> getRequiredDependencies(String runtimeProvider) {
		List<Dependency> deps = new ArrayList<>();
		Dependency dep = new Dependency();
		dep.setGroupId(CAMEL_SAP_GROUP_ID);
		dep.setArtifactId(new SAPArtifactIdDependenciesManager().getArtifactId(runtimeProvider));
		String currentProjectCamelVersion = CamelUtils.getCurrentProjectCamelVersion();
		if (currentProjectCamelVersion.startsWith("2.15")
				|| currentProjectCamelVersion.startsWith("2.17")) {
			dep.setVersion(new SAPVersionDependenciesManager().computeSapVersion(currentProjectCamelVersion));
		} else {
			// we consider as a shortcut that it is a 7.x version and that there is a bom managing the version
		}
		deps.add(dep);
		return deps;
	}
	
	@Override
	public boolean isValid(String runtimeProvider) {
		// In fact, it is not valid on SpringBoot with Fuse 6.x but it requires too much modifications for this old version,
		// so showing the SAP palette entry for all versions
		return true;
	}

}
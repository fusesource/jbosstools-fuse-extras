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

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;
import org.fusesource.ide.sap.ui.Activator;

public class SAPVersionDependenciesManager implements IDependenciesManager {

	public SAPVersionDependenciesManager() {
		// keep for reflection isntanciation
	}

	@Override
	public void updatePluginDependencies(List<Plugin> currentPlugins, String camelVersion) {
		// do nothing
	}

	@Override
	public void updateDependencies(List<Dependency> currentDependencies, String camelVersion) {
		for (Dependency dependency : currentDependencies) {
			if(isSAPCamelDependency(dependency)){
				dependency.setVersion(camelVersion);
			}
		}
	}

	private boolean isSAPCamelDependency(Dependency dependency) {
		return Activator.CAMEL_SAP_GROUP_ID.equals(dependency.getGroupId()) && Activator.CAMEL_SAP_ARTIFACT_ID.equals(dependency.getArtifactId());
	}

}

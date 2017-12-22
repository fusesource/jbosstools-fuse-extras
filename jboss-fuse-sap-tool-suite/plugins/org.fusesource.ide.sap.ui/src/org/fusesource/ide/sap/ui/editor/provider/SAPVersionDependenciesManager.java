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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;
import org.fusesource.ide.sap.ui.Activator;

public class SAPVersionDependenciesManager implements IDependenciesManager {

	/* Fuse 6.2.1.R9 */
	static final String SAP_VERSION_621_LATEST = "6.2.1.redhat-216";
	/* Fuse 6.3.0.R5 */
	static final String SAP_VERSION_630_LATEST = "6.3.0.redhat-310";

	public static final String LAST_SAP_VERSION = SAP_VERSION_630_LATEST;

	private static Map<String, String> camelToSAPVersionMapping;

	static {
		camelToSAPVersionMapping = new HashMap<>();
		camelToSAPVersionMapping.put("2.15.1", SAP_VERSION_621_LATEST);
		camelToSAPVersionMapping.put("2.17.0", SAP_VERSION_630_LATEST);
		camelToSAPVersionMapping.put("2.17.3", SAP_VERSION_630_LATEST);
		// TODO: update me with every new release of camel and sap supported in
		// tooling
	}

	public SAPVersionDependenciesManager() {
		// keep for reflection instanciation
	}

	@Override
	public void updatePluginDependencies(List<Plugin> currentPlugins, String camelVersion) {
		// do nothing
	}

	@Override
	public void updateDependencies(List<Dependency> currentDependencies, String camelVersion) {
		for (Dependency dependency : currentDependencies) {
			if (isSAPCamelDependency(dependency)) {
				dependency.setVersion(computeSapVersion(camelVersion));
			}
		}
	}

	public String computeSapVersion(String camelVersion) {
		/* Fuse 6.2.1 */
		if (camelVersion != null && camelVersion.matches("2\\.15\\.1\\.redhat-621[0-9][0-9][0-9]")) {
			String buildNumber = camelVersion.substring(17);
			return "6.2.1.redhat-" + buildNumber;
		}

		/* Fuse 6.3.0 */
		if (camelVersion != null && camelVersion.matches("2\\.17\\.0\\.redhat-630[0-9][0-9][0-9]")) {
			String buildNumber = camelVersion.substring(17);
			return "6.3.0.redhat-" + buildNumber;
		}

		/* Check default versions */
		String strippedCamelVersion = camelVersion.replaceAll(".redhat.*", "");
		return camelToSAPVersionMapping.getOrDefault(strippedCamelVersion, LAST_SAP_VERSION);
	}

	private boolean isSAPCamelDependency(Dependency dependency) {
		return Activator.CAMEL_SAP_GROUP_ID.equals(dependency.getGroupId())
				&& Activator.CAMEL_SAP_ARTIFACT_ID.equals(dependency.getArtifactId());
	}

}

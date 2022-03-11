/******************************************************************************* 
 * Copyright (c) 2022 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.sap.ui.editor.provider;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.sap.ui.Activator;

public class SAPArtifactIdDependenciesManager {

	public String getArtifactId(String runtimeProvider) {
		if(CamelCatalogUtils.RUNTIME_PROVIDER_KARAF.equals(runtimeProvider)
				|| CamelCatalogUtils.RUNTIME_PROVIDER_WILDFLY.equals(runtimeProvider)) {
			return Activator.CAMEL_SAP_ARTIFACT_ID;
		} else if (CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equals(runtimeProvider)) {
			return Activator.CAMEL_SAP_ARTIFACT_ID +"-starter";
		} else {
			return Activator.CAMEL_SAP_ARTIFACT_ID;
		}
	}
}

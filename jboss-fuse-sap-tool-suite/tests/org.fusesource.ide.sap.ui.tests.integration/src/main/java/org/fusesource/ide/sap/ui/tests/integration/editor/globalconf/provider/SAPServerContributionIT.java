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
package org.fusesource.ide.sap.ui.tests.integration.editor.globalconf.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.sap.ui.editor.globalconf.provider.SAPServerContribution;
import org.fusesource.ide.sap.ui.editor.provider.SAPVersionDependenciesManager;
import org.junit.Test;

public class SAPServerContributionIT {
	
	@Test
	public void testSAPDepenencyCorrect() throws Exception {
		Dependency dependency = new SAPServerContribution().getElementDependencies().get(0);
		
		assertThat(dependency.getGroupId()).isEqualTo("org.fusesource");
		assertThat(dependency.getArtifactId()).isEqualTo("camel-sap");
		assertThat(dependency.getVersion()).isEqualTo(SAPVersionDependenciesManager.LAST_SAP_VERSION);
	}
	
}

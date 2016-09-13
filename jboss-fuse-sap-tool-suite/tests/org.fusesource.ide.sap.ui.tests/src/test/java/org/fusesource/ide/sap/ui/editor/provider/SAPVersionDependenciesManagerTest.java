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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.fusesource.ide.sap.ui.Activator;
import org.junit.Before;
import org.junit.Test;

public class SAPVersionDependenciesManagerTest {
	
	private List<Dependency> currentDependencies;
	private Dependency dep;
	
	@Before
	public void setup() {
		currentDependencies = new ArrayList<>();
		dep = new Dependency();
		dep.setGroupId(Activator.CAMEL_SAP_GROUP_ID);
		dep.setArtifactId(Activator.CAMEL_SAP_ARTIFACT_ID);
		currentDependencies.add(dep);
	}
	
	@Test
	public void testWithRedHatDependency() throws Exception {
		new SAPVersionDependenciesManager().updateDependencies(currentDependencies , "2.15.1.redhat-084");
		
		assertThat(dep.getVersion()).isEqualTo(SAPVersionDependenciesManager.SAP_VERSION_621);
	}
	
	@Test
	public void testWithoutRedHatDependency() throws Exception {
		new SAPVersionDependenciesManager().updateDependencies(currentDependencies , "2.17.0");
		
		assertThat(dep.getVersion()).isEqualTo(SAPVersionDependenciesManager.SAP_VERSION_630);
	}

	@Test
	public void testDefaultvalueWhenDepdenciesUnknown() throws Exception {
		new SAPVersionDependenciesManager().updateDependencies(currentDependencies , "plop");
		
		assertThat(dep.getVersion()).isEqualTo(SAPVersionDependenciesManager.LAST_SAP_VERSION);
	}
	
}

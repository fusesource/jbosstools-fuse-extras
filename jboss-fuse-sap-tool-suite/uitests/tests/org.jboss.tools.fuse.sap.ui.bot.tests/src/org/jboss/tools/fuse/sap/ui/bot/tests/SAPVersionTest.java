/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.sap.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.XPathEvaluator;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.sap.reddeer.SupportedSAPVersions;
import org.jboss.tools.fuse.sap.reddeer.component.SAPIDocListServer;
import org.jboss.tools.fuse.sap.ui.bot.tests.utils.ProjectFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests for checking a SAP version added by Camel editor.
 * 
 * @author apodhrad
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Ignore("Ignore until SAP Instance is back")
public class SAPVersionTest {

	public static final String PROJECT_NAME = "sap-version";
	public static final ProjectType PROJECT_TYPE = ProjectType.SPRING;

	private String camelVersion;

	@Parameters(name = "{0}")
	public static Collection<String> getCamelVersions() {
		return SupportedCamelVersions.getCamelVersions();
	}

	public SAPVersionTest(String camelVersion) {
		this.camelVersion = camelVersion;
	}

	/**
	 * Deletes all projects
	 */
	@Before
	@After
	public void cleanWorkspaceAndErrorLog() {
		new CleanWorkspaceRequirement().fulfill();
		new LogView().open();
		new LogView().deleteLog();
	}

	/**
	 * <p>
	 * Checks a sap version after adding a SAP component into the Camel editor (for all supported Camel versions).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create an empty Fuse project based on Spring DSL</li>
	 * <li>open Project Explorer view and open the Camel XML file</li>
	 * <li>add a SAP component (namely 'sap-idoclist-server')</li>
	 * <li>save the Camel editor</li>
	 * <li>check the sap version in the file pom.xml</li>
	 * </ol>
	 */
	@Test
	public void testSAPVersion() throws Exception {
		new WorkbenchShell();
		ProjectFactory.newProject(PROJECT_NAME).version(camelVersion).deploymentType(STANDALONE).runtimeType(KARAF)
				.template(EMPTY_SPRING).create();
		new LogView().open();
		new LogView().deleteLog();

		new ProjectExplorer().open();
		new CamelProject(PROJECT_NAME).openCamelContext(PROJECT_TYPE.getCamelContext());
		new CamelEditor(PROJECT_TYPE.getCamelContext()).addCamelComponent(new SAPIDocListServer(), "Route _route1");
		new CamelEditor(PROJECT_TYPE.getCamelContext()).save();

		File pomFile = new File(new CamelProject(PROJECT_NAME).getFile(), "pom.xml");
		XPathEvaluator xpath = new XPathEvaluator(pomFile);
		String sapVersion = xpath.evaluateString("/project/dependencies/dependency[artifactId='camel-sap']/version");

		assertEquals("For Camel '" + camelVersion + "'", SupportedSAPVersions.getSAPVersion(camelVersion), sapVersion);
	}

}

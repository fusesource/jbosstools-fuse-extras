/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.sap.ui.bot.tests;

import static org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63;
import static org.hamcrest.Matchers.equalTo;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.sap.ui.bot.tests.utils.ProjectFactory.newProject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.reddeer.common.condition.WaitCondition;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.XPathEvaluator;
import org.jboss.tools.fuse.reddeer.condition.ContainsText;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.sap.reddeer.dialog.SAPTestDestinationDialog;
import org.jboss.tools.fuse.sap.reddeer.dialog.SAPTestServerDialog;
import org.jboss.tools.fuse.sap.reddeer.editor.SAPConfigurationsEditor;
import org.jboss.tools.fuse.sap.reddeer.requirement.SAPDestination;
import org.jboss.tools.fuse.sap.reddeer.requirement.SAPRequirement;
import org.jboss.tools.fuse.sap.reddeer.requirement.SAPRequirement.SAP;
import org.jboss.tools.fuse.sap.reddeer.requirement.SAPServer;
import org.jboss.tools.fuse.sap.reddeer.wizard.SAPConfigurationWizard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests relevant for SAP Tooling
 * 
 * @author apodhrad
 */
@SAP
@CleanWorkspace
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@Ignore("Ignore until SAP Instance is back")
public class SAPConfigurationTest {

	public static final String PROJECT_NAME = "sap_config";
	public static final String CONFIGURATION_IMPL = "org.fusesource.camel.component.sap.SapConnectionConfiguration";
	public static final String DESTINATION_IMPL = "org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataImpl";
	public static final String SERVER_IMPL = "org.fusesource.camel.component.sap.model.rfc.impl.ServerDataImpl";

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@InjectRequirement
	private SAPRequirement sap;

	@Parameters(name = "{0}")
	public static Collection<ProjectType> data() {
		return Arrays.asList(new ProjectType[] { ProjectType.SPRING, ProjectType.BLUEPRINT });
	}

	private ProjectType type;

	public SAPConfigurationTest(ProjectType type) {
		this.type = type;
	}

	private static String getProjectName(ProjectType type) {
		return PROJECT_NAME + "_" + type;
	}

	private String getProjectName() {
		return getProjectName(type);
	}

	private SAPConfigurationsEditor editor() {
		return new SAPConfigurationsEditor(type.getCamelContext());
	}

	@BeforeClass
	public static void createFuseProjects() {
		new WorkbenchShell().maximize();
		for (ProjectType type : data()) {
			String[] template = type == ProjectType.SPRING ? EMPTY_SPRING : EMPTY_BLUEPRINT;
			newProject(getProjectName(type)).version(CAMEL_VERSION_LATEST_PRODUCTIZED_63).deploymentType(STANDALONE)
					.runtimeType(KARAF).template(template).create();
			new CamelProject(getProjectName(type)).update();
			new CamelProject(getProjectName(type)).openCamelContext(type.getCamelContext());
		}
	}

	/**
	 * Delete any existing SAP configuration
	 */
	@Before
	public void deleteSapConfiguration() {
		try {
			editor().deleteSapConfig();
		} catch (Exception e) {
			// ok
		}
		if (editor().isDirty()) {
			editor().save();
		}
	}

	/**
	 * <p>
	 * Tests adding and removing SAP destinations in Configurations editor.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open Configurations editor</li>
	 * <li>add 2 new destinations</li>
	 * <li>check the xml file</li>
	 * <li>remove the first destination</li>
	 * <li>check the xml file</li>
	 * <li>remove the second destination</li>
	 * <li>check the xml file</li>
	 * </ol>
	 */
	@Test
	public void testAddingAndDeletingDestinations() throws Exception {
		String destinationName1 = "myDestination1";
		String destinationName2 = "myDestination2";

		SAPConfigurationWizard wizard = editor().addSapConfig();
		wizard.addDestination(destinationName1);
		wizard.addDestination(destinationName2);

		Collection<String> destinations = wizard.getDestinations();
		assertTrue(destinations.contains(destinationName1));
		assertTrue(destinations.contains(destinationName2));
		assertEquals(2, destinations.size());

		wizard.finish();
		editor().save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath(DESTINATION_IMPL, destinationQuery(destinationName1) + "/@class");
		assertXPath(DESTINATION_IMPL, destinationQuery(destinationName2) + "/@class");

		wizard = editor().editSapConfig();
		wizard.deleteDestination(destinationName1);

		destinations = wizard.getDestinations();
		assertTrue(destinations.contains(destinationName2));
		assertEquals(1, destinations.size());

		wizard.finish();
		editor().save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath("", destinationQuery(destinationName1) + "/@class");
		assertXPath(DESTINATION_IMPL, destinationQuery(destinationName2) + "/@class");

		wizard = editor().editSapConfig();
		wizard.deleteDestination(destinationName2);

		destinations = wizard.getDestinations();
		assertTrue(destinations.isEmpty());

		wizard.finish();
		editor().save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath("", destinationQuery(destinationName1) + "/@class");
		assertXPath("", destinationQuery(destinationName2) + "/@class");
	}

	/**
	 * <p>
	 * Tests adding and removing SAP destinations in Configurations editor.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open Configurations editor</li>
	 * <li>add 2 new destinations</li>
	 * <li>check the xml file</li>
	 * <li>remove the first destination</li>
	 * <li>check the xml file</li>
	 * <li>remove the second destination</li>
	 * <li>check the xml file</li>
	 * </ol>
	 */
	@Test
	public void testAddingAndDeletingServers() throws Exception {
		String serverName1 = "myServer1";
		String serverName2 = "myServer2";

		SAPConfigurationWizard wizard = editor().addSapConfig();
		wizard.addServer(serverName1);
		wizard.addServer(serverName2);

		Collection<String> servers = wizard.getServers();
		assertTrue(servers.contains(serverName1));
		assertTrue(servers.contains(serverName2));
		assertEquals(2, servers.size());

		wizard.finish();
		editor().save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath(SERVER_IMPL, serverQuery(serverName1) + "/@class");
		assertXPath(SERVER_IMPL, serverQuery(serverName2) + "/@class");

		wizard = editor().editSapConfig();
		wizard.deleteServer(serverName1);

		servers = wizard.getServers();
		assertTrue(servers.contains(serverName2));
		assertEquals(1, servers.size());

		wizard.finish();
		editor().save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath("", serverQuery(serverName1) + "/@class");
		assertXPath(SERVER_IMPL, serverQuery(serverName2) + "/@class");

		wizard = editor().editSapConfig();
		wizard.deleteServer(serverName2);

		servers = wizard.getServers();
		assertTrue(servers.isEmpty());

		wizard.finish();
		editor().save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath("", serverQuery(serverName1) + "/@class");
		assertXPath("", serverQuery(serverName2) + "/@class");
	}

	/**
	 * <p>
	 * Tests all properties of SAP destination in Configurations editor.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open Configurations editor</li>
	 * <li>add a new destination</li>
	 * <li>set all properties</li>
	 * <li>save and check the xml file</li>
	 * </ol>
	 */
	@Test
	public void testDestinationProperties() throws Exception {
		String destinationName = "myDestination";
		SAPConfigurationsEditor editor = editor();

		SAPConfigurationWizard wizard = editor.addSapConfig();
		wizard.addDestination(destinationName);
		wizard.selectDestination(destinationName);

		/* Basic */
		wizard.selectTab("Basic");
		wizard.getSAPApplicationServerTXT().typeText("sap.example.com");
		wizard.getSAPSystemNumberTXT().typeText("12");
		wizard.getSAPClientTXT().typeText("123");
		wizard.getLogonUserTXT().typeText("admin");
		wizard.getLogonPasswordTXT().typeText("admin123$");
		wizard.getLogonLanguageTXT().typeText("xy");

		/* Connection */
		wizard.selectTab("Connection");
		collector.checkThat(wizard.getSAPSystemNumberTXT().getText(), equalTo("12"));
		wizard.getSAPRouterStringTXT().typeText("/H/xyz/S/123/W/abc");
		collector.checkThat(wizard.getSAPApplicationServerTXT().getText(), equalTo("sap.example.com"));
		wizard.getSAPMessageServerTXT().typeText("sap-msg.example.com");
		wizard.getSAPMessageServerPortTXT().typeText("1234");
		wizard.getGatewayHostTXT().typeText("gt.example.com");
		wizard.getGatewayPortTXT().typeText("4321");
		wizard.getSAPSystemIDTXT().typeText("AB");
		wizard.getSAPApplicationServerGroupTXT().typeText("myGroup");

		/* Authentication */
		wizard.selectTab("Authentication");
		wizard.getSAPApplicationTypeCMB().setSelection("CONFIGURED_USER");
		wizard.getSAPApplicationTypeCMB().setSelection("CURRENT_USER");
		collector.checkThat(wizard.getSAPClientTXT().getText(), equalTo("123"));
		collector.checkThat(wizard.getLogonUserTXT().getText(), equalTo("admin"));
		wizard.getLogonUserAliasTXT().typeText("superadmin");
		collector.checkThat(wizard.getLogonPasswordTXT().getText(), equalTo("admin123$"));
		wizard.getSAPSSOLogonTicketTXT().typeText("sso");
		wizard.getSAPX509LoginTicketTXT().typeText("x509");
		collector.checkThat(wizard.getLogonLanguageTXT().getText(), equalTo("xy"));

		/* Special */
		wizard.selectTab("Special");
		wizard.getEnableRFCTraceCHB().toggle(true);
		wizard.getSelectCPICTraceCMB().setSelection("Trace Level 0: No Trace");
		wizard.getSelectCPICTraceCMB().setSelection("Trace Level 1: Only Errors are Logged");
		wizard.getSelectCPICTraceCMB().setSelection("Trace Level 2: Flow and Basic Data Trace");
		wizard.getSelectCPICTraceCMB().setSelection("Trace Level 3: Flow and Complete Data Trace");
		wizard.getEnableLogonCheckCHB().toggle(true);
		wizard.getInitialCodepage().typeText("init");
		wizard.getReqeustSSOTicketCHB().toggle(true);

		/* Pool */
		wizard.selectTab("Pool");
		wizard.getConnectionPoolPeakLimitTXT().typeText("10");
		wizard.getConnectionPoolCapacityTXT().typeText("11");
		wizard.getConnectionPoolExpirationTimeTXT().typeText("12");
		wizard.getConnectionPoolExpireCheckPeriodTXT().typeText("13");
		wizard.getConnectionPoolMaxGetClientTimeTXT().typeText("14");

		/* SNC */
		wizard.selectTab("SNC");
		wizard.getTurnOnSNCModeCHB().toggle(true);
		wizard.getSNCPartnerNameTXT().typeText("snc-partner");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 1: Secure Authentication Only");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 2: Data Integrity Protection");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 3: Data Privacy Protection");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 8: Default Protection");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 9: Maximum Protection");
		wizard.getSNCNameTXT().typeText("snc-name");
		wizard.getSNCLibraryPathTXT().typeText("snc-path");

		/* Repository */
		wizard.selectTab("Repository");
		wizard.getRepositoryDestinationTXT().typeText("repo");
		wizard.getRepositoryLogonUserTXT().typeText("user");
		wizard.getRepositoryLogonPasswordTXT().typeText("user123$");
		wizard.getTurnOnSNCModeforRepositoryDestinationCHB().toggle(true);
		wizard.getUseRFC_METADATA_GETCHB().toggle(true);

		wizard.finish();
		editor.save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath(DESTINATION_IMPL, destinationQuery(destinationName) + "/@class");
		assertDestinationProperty(destinationName, "aliasUser", "superadmin");
		assertDestinationProperty(destinationName, "ashost", "sap.example.com");
		assertDestinationProperty(destinationName, "authType", "CURRENT_USER");
		assertDestinationProperty(destinationName, "client", "123");
		assertDestinationProperty(destinationName, "codepage", "init");
		assertDestinationProperty(destinationName, "cpicTrace", "3");
		assertDestinationProperty(destinationName, "denyInitialPassword", "0");
		assertDestinationProperty(destinationName, "expirationPeriod", "13");
		assertDestinationProperty(destinationName, "expirationTime", "12");
		assertDestinationProperty(destinationName, "getsso2", "1");
		assertDestinationProperty(destinationName, "group", "myGroup");
		assertDestinationProperty(destinationName, "gwhost", "gt.example.com");
		assertDestinationProperty(destinationName, "gwserv", "4321");
		assertDestinationProperty(destinationName, "lang", "xy");
		assertDestinationProperty(destinationName, "lcheck", "1");
		assertDestinationProperty(destinationName, "maxGetTime", "14");
		assertDestinationProperty(destinationName, "mshost", "sap-msg.example.com");
		assertDestinationProperty(destinationName, "msserv", "1234");
		assertDestinationProperty(destinationName, "mysapsso2", "sso");
		assertDestinationProperty(destinationName, "passwd", "admin123$");
		assertDestinationProperty(destinationName, "password", "admin123$");
		assertDestinationProperty(destinationName, "peakLimit", "10");
		assertDestinationProperty(destinationName, "pingOnCreate", "false");
		assertDestinationProperty(destinationName, "poolCapacity", "11");
		assertDestinationProperty(destinationName, "r3name", "AB");
		assertDestinationProperty(destinationName, "repositoryDest", "repo");
		assertDestinationProperty(destinationName, "repositoryPasswd", "user123$");
		assertDestinationProperty(destinationName, "repositoryRoundtripOptimization", "1");
		assertDestinationProperty(destinationName, "repositorySnc", "1");
		assertDestinationProperty(destinationName, "repositoryUser", "user");
		assertDestinationProperty(destinationName, "saprouter", "/H/xyz/S/123/W/abc");
		assertDestinationProperty(destinationName, "sncLibrary", "snc-path");
		assertDestinationProperty(destinationName, "sncMode", "1");
		assertDestinationProperty(destinationName, "sncMyname", "snc-name");
		assertDestinationProperty(destinationName, "sncPartnername", "snc-partner");
		assertDestinationProperty(destinationName, "sncQop", "9");
		assertDestinationProperty(destinationName, "sysnr", "12");
		assertDestinationProperty(destinationName, "trace", "1");
		assertDestinationProperty(destinationName, "userName", "admin");
		assertDestinationProperty(destinationName, "user", "admin");
		assertDestinationProperty(destinationName, "x509cert", "x509");
	}

	/**
	 * <p>
	 * Tests all properties of SAP server in Configurations editor.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open Configurations editor</li>
	 * <li>add a new server</li>
	 * <li>set all properties</li>
	 * <li>save and check the xml file</li>
	 * </ol>
	 */
	@Test
	public void testServerProperties() throws Exception {
		String serverName = "myServer";
		SAPConfigurationsEditor editor = editor();

		SAPConfigurationWizard wizard = editor.addSapConfig();
		wizard.addServer(serverName);
		wizard.selectServer(serverName);

		/* Mandatory */
		wizard.selectTab("Mandatory");
		wizard.getGatewayHostTXT().typeText("host.example.com");
		wizard.getGatewayPortTXT().typeText("3333");
		wizard.getProgramIDTXT().typeText("FOO");
		wizard.getRepositoryDestinationTXT().typeText("myDest");
		wizard.getConnectionCountTXT().typeText("3");

		/* Optional */
		wizard.selectTab("Optional");
		wizard.getEnableRFCTraceCHB().toggle(true);
		wizard.getSAPRouterStringTXT().typeText("/H/abc/S/321/W/xyz");
		wizard.getWorkerThreadCountTXT().typeText("10");
		wizard.getMinimumWorkerThreadCountTXT().typeText("11");
		wizard.getMaximumStartupDelayTXT().typeText("12");
		wizard.getRepositoryMapTXT().typeText("map");

		/* SNC */
		wizard.selectTab("SNC");
		wizard.getTurnOnSNCModeCHB().toggle(true);
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 1: Secure Authentication Only");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 2: Data Integrity Protection");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 3: Data Privacy Protection");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 8: Default Protection");
		wizard.getSNCLevelOfSecurityCMB().setSelection("Security Level 9: Maximum Protection");
		wizard.getSNCNameTXT().typeText("snc-name");
		wizard.getSNCLibraryPathTXT().typeText("snc-path");

		wizard.finish();
		editor.save();

		assertXPath(CONFIGURATION_IMPL, configurationQuery() + "/@class");
		assertXPath(SERVER_IMPL, serverQuery(serverName) + "/@class");
		assertServerProperty(serverName, "gwhost", "host.example.com");
		assertServerProperty(serverName, "gwserv", "3333");
		assertServerProperty(serverName, "progid", "FOO");
		assertServerProperty(serverName, "connectionCount", "3");
		assertServerProperty(serverName, "saprouter", "/H/abc/S/321/W/xyz");
		assertServerProperty(serverName, "maxStartUpDelay", "12");
		assertServerProperty(serverName, "repositoryDestination", "myDest");
		assertServerProperty(serverName, "repositoryMap", "map");
		assertServerProperty(serverName, "workerThreadCount", "10");
		assertServerProperty(serverName, "workerThreadMinCount", "11");
		assertServerProperty(serverName, "sncQop", "9");
		assertServerProperty(serverName, "sncMyname", "snc-name");
		assertServerProperty(serverName, "sncLib", "snc-path");
	}

	/**
	 * <p>
	 * Tests a connection of SAP destination and server in Configurations editor.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open Configurations editor</li>
	 * <li>add a new destination</li>
	 * <li>set basic properties</li>
	 * <li>test the connection</li>
	 * <li>add a new server</li>
	 * <li>set mandatory properties</li>
	 * <li>test the connection</li>
	 * </ol>
	 */
	@Test
	public void testConnectionOfDestinationAndServer() {
		SAPDestination destination = sap.getConfiguration().getDestination();
		SAPServer server = sap.getConfiguration().getServer();

		SAPConfigurationWizard wizard = editor().addSapConfig();
		wizard.addDestination(destination.getName());
		wizard.selectDestination(destination.getName());
		wizard.selectTab("Connection");
		wizard.selectTab("Basic");
		wizard.getSAPApplicationServerTXT().typeText(destination.getAshost());
		wizard.getSAPSystemNumberTXT().typeText(destination.getSysnr());
		wizard.getSAPClientTXT().typeText(destination.getClient());
		wizard.getLogonUserTXT().typeText(destination.getUser());
		wizard.getLogonPasswordTXT().typeText(destination.getPassword());

		SAPTestDestinationDialog destinationDialog = wizard.openDestinationTestDialog(destination.getName());
		String expected = "Connection test for destination '" + destination.getName() + "' succeeded.";
		collector.checkThat(destinationDialog.test(), equalTo(expected));
		destinationDialog.close();

		wizard.addServer(server.getName());
		wizard.selectServer(server.getName());
		wizard.getGatewayHostTXT().typeText(server.getGwhost());
		wizard.getGatewayPortTXT().typeText(server.getGwport());
		wizard.getProgramIDTXT().typeText(server.getProgid());
		wizard.getRepositoryDestinationTXT().typeText(server.getDestination());
		wizard.getConnectionCountTXT().typeText(server.getConnectionCount());

		SAPTestServerDialog serverDialog = wizard.openServerTestDialog(server.getName());
		expected = "Connection test for destination '" + destination.getName() + "' succeeded.";
		serverDialog.clear();
		serverDialog.start();
		WaitCondition isAlive = new ContainsText(serverDialog.getResultText(), "Server state: ALIVE");
		new WaitUntil(isAlive, false);
		collector.checkThat(isAlive.errorMessageUntil(), isAlive.test(), equalTo(true));
		serverDialog.stop();
		WaitCondition isStopped = new ContainsText(serverDialog.getResultText(), "Server state: STOPPED");
		new WaitUntil(isStopped, false);
		collector.checkThat(isStopped.errorMessageUntil(), isStopped.test(), equalTo(true));
		serverDialog.clear();
		collector.checkThat(serverDialog.getResultText().getText().trim(), equalTo(""));
		serverDialog.close();

		wizard.finish();
		editor().save();

		// TODO Model and execute a route with SAP destination or server
	}

	private void assertDestinationProperty(String destination, String property, String expected) throws IOException {
		assertXPath(expected, destinationQuery(destination) + "/property[@name='" + property + "']/@value");
	}

	private void assertServerProperty(String server, String property, String expected) throws IOException {
		assertXPath(expected, serverQuery(server) + "/property[@name='" + property + "']/@value");
	}

	private void assertXPath(String expected, String query) throws IOException {
		File contextFile = new CamelProject(getProjectName()).getCamelContextFile(type.getCamelContext());
		XPathEvaluator xpath = new XPathEvaluator(contextFile);
		collector.checkThat(xpath.evaluateString(query), equalTo(expected));
	}

	private String configurationQuery() {
		return "/" + type.getRootElement() + "/bean[@id='sap-configuration']";
	}

	private String destinationQuery() {
		return configurationQuery() + "/" + "property[@name='destinationDataStore']";
	}

	private String destinationQuery(String name) {
		return destinationQuery() + "/map/entry[@key='" + name + "']/bean";
	}

	private String serverQuery() {
		return configurationQuery() + "/" + "property[@name='serverDataStore']";
	}

	private String serverQuery(String name) {
		return serverQuery() + "/map/entry[@key='" + name + "']/bean";
	}

}

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
package org.jboss.tools.fuse.sap.qe.reddeer.tests;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.condition.ContainsText;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.sap.qe.reddeer.dialog.SAPTestDestinationDialog;
import org.jboss.tools.fuse.sap.qe.reddeer.dialog.SAPTestServerDialog;
import org.jboss.tools.fuse.sap.qe.reddeer.requirement.SAPDestination;
import org.jboss.tools.fuse.sap.qe.reddeer.requirement.SAPRequirement;
import org.jboss.tools.fuse.sap.qe.reddeer.requirement.SAPRequirement.SAP;
import org.jboss.tools.fuse.sap.qe.reddeer.requirement.SAPServer;
import org.jboss.tools.fuse.sap.qe.reddeer.view.SAPConnectionView;
import org.jboss.tools.fuse.sap.qe.reddeer.view.SAPDestinationProperties;
import org.jboss.tools.fuse.sap.qe.reddeer.view.SAPServerProperties;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests relevant for SAP Tooling
 * 
 * @author apodhrad
 */
@SAP
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class SAPConnectionTest {

	@InjectRequirement
	public SAPRequirement sap;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupOpenPropertiesView() {
		new WorkbenchShell().maximize();
		new PropertySheet().open();
	}

	/**
	 * <p>
	 * Tests SAP Connections view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open SAP Connections view</li>
	 * <li>define a new destination</li>
	 * <li>check connection to the destination</li>
	 * <li>create a new server definition in SAP Connections view</li>
	 * <li>start the server</li>
	 * <li>check status of the server</li>
	 * <li>stop the server</li>
	 * <li>check status of the server</li>
	 * </ol>
	 */
	@Test
	public void testSAPConnection() {
		SAPDestination destination = sap.getConfiguration().getDestination();
		SAPServer server = sap.getConfiguration().getServer();

		SAPConnectionView sapConnectionView = new SAPConnectionView();
		sapConnectionView.open();
		sapConnectionView.newDestination(destination.getName());

		SAPDestinationProperties sapDestinationProperties = sapConnectionView
				.openDestinationProperties(destination.getName());
		sapDestinationProperties.getSAPApplicationServerText().setText(destination.getAshost());
		sapDestinationProperties.getSAPSystemNumberText().setText(destination.getSysnr());
		sapDestinationProperties.getSAPClientText().setText(destination.getClient());
		sapDestinationProperties.getLogonUserText().setText(destination.getUser());
		sapDestinationProperties.getLogonPasswordText().setText(destination.getPassword());
		sapDestinationProperties.getLogonLanguageText().setText(destination.getLang());

		SAPTestDestinationDialog testDestinationConnection = sapConnectionView
				.openDestinationTest(destination.getName());
		testDestinationConnection.test();
		String expected = "Connection test for destination '" + destination.getName() + "' succeeded.";
		new WaitUntil(new ContainsText(testDestinationConnection.getResultTXT(), expected));
		testDestinationConnection.clear();
		testDestinationConnection.close();

		sapConnectionView.open();
		sapConnectionView.newServer(server.getName());

		SAPServerProperties sapServerProperties = sapConnectionView.openServerProperties(server.getName());
		sapServerProperties.getGatewayHostText().setText(server.getGwhost());
		sapServerProperties.getGatewayPortText().setText(server.getGwport());
		sapServerProperties.getProgramIDText().setText(server.getProgid());
		sapServerProperties.getRepositoryDestinationText().setText(server.getDestination());
		sapServerProperties.getConnectionCountText().setText(server.getConnectionCount());

		SAPTestServerDialog testServerConnection = sapConnectionView.openServerTest(server.getName());
		testServerConnection.start();
		new WaitUntil(new ContainsText(testServerConnection.getResultText(), "Server state: STARTED"));
		new WaitUntil(new ContainsText(testServerConnection.getResultText(), "Server state: ALIVE"));
		testServerConnection.stop();
		new WaitUntil(new ContainsText(testServerConnection.getResultText(), "Server state: STOPPED"));
		testServerConnection.clear();
		testServerConnection.close();
	}

	// TODO Write test for exporting, wait for https://issues.jboss.org/browse/FUSETOOLS-1374

}

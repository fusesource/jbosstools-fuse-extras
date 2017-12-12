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
package org.jboss.tools.fuse.sap.reddeer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.api.Button;
import org.eclipse.reddeer.swt.api.CCombo;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.Text;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ccombo.LabeledCCombo;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.fuse.sap.reddeer.dialog.SAPTestDestinationDialog;
import org.jboss.tools.fuse.sap.reddeer.dialog.SAPTestServerDialog;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPConfigurationWizard extends WizardDialog {

	public static final String TITLE = "Edit SAP Connection Configuration";

	public SAPConfigurationWizard() {
		super(TITLE);
	}

	public void activate() {
		new DefaultShell(TITLE);
	}

	public void selectDestination(String name) {
		activate();
		new DefaultTreeItem("SAP Connection Configuration", "Destination Data Store", name).select();
	}

	public void addDestination(String name) {
		activate();
		getAddDestinationBTN().click();

		Shell shell = new DefaultShell("Create Destination");
		new LabeledText(shell, "Please provide a destination name").setText(name);
		new OkButton(shell).click();
		new WaitWhile(new ShellIsAvailable(shell));
	}

	public void deleteDestination(String name) {
		selectDestination(name);
		getDeleteBTN().click();
	}

	public List<String> getDestinations() {
		activate();
		List<String> destinations = new ArrayList<>();
		for (TreeItem item : new DefaultTreeItem("SAP Connection Configuration", "Destination Data Store").getItems()) {
			destinations.add(item.getText());
		}
		return destinations;
	}

	public void addServer(String name) {
		activate();
		getAddServerBTN().click();

		Shell shell = new DefaultShell("Create Server");
		new LabeledText(shell, "Please provide a server name").setText(name);
		new OkButton(shell).click();
		new WaitWhile(new ShellIsAvailable(shell));
	}

	public void selectServer(String name) {
		activate();
		new DefaultTreeItem("SAP Connection Configuration", "Server Data Store", name).select();
	}

	public List<String> getServers() {
		activate();
		List<String> destinations = new ArrayList<>();
		for (TreeItem item : new DefaultTreeItem("SAP Connection Configuration", "Server Data Store").getItems()) {
			destinations.add(item.getText());
		}
		return destinations;
	}

	public void deleteServer(String name) {
		selectServer(name);
		getDeleteBTN().click();
	}

	public void selectTab(String tab) {
		new DefaultCTabItem(tab).activate();
	}

	public Button getAddDestinationBTN() {
		return new PushButton("Add Destination");
	}

	public Button getAddServerBTN() {
		return new PushButton("Add Server");
	}

	public SAPTestDestinationDialog openDestinationTestDialog(String destination) {
		selectDestination(destination);
		getTestBTN().click();
		return new SAPTestDestinationDialog();
	}

	public SAPTestServerDialog openServerTestDialog(String server) {
		selectServer(server);
		getTestBTN().click();
		return new SAPTestServerDialog();
	}

	public Button getTestBTN() {
		return new PushButton("Test");
	}

	public Button getDeleteBTN() {
		return new PushButton("Delete");
	}

	public Text getEditSAPDestinationandServerDataStoresTXT() {
		return new LabeledText(this, "Edit SAP Destination and Server Data Stores");
	}

	public Text getRepositoryMapTXT() {
		return new LabeledText(this, "Repository Map:");
	}

	public Text getMaximumStartupDelayTXT() {
		return new LabeledText(this, "Maximum Startup Delay:");
	}

	public Text getMinimumWorkerThreadCountTXT() {
		return new LabeledText(this, "Minimum Worker Thread Count:");
	}

	public Text getWorkerThreadCountTXT() {
		return new LabeledText(this, "Worker Thread Count:");
	}

	public Text getSAPRouterStringTXT() {
		return new LabeledText(this, "SAP Router String:");
	}

	public CheckBox getEnableRFCTraceCHB() {
		return new CheckBox("Enable RFC Trace?");
	}

	public Text getConnectionCountTXT() {
		return new LabeledText(this, "Connection Count:");
	}

	public Text getProgramIDTXT() {
		return new LabeledText(this, "Program ID:");
	}

	public Text getGatewayPortTXT() {
		return new LabeledText(this, "Gateway Port:");
	}

	public Text getGatewayHostTXT() {
		return new LabeledText(this, "Gateway Host:");
	}

	public CheckBox getUseRFC_METADATA_GETCHB() {
		return new CheckBox("Use RFC_METADATA_GET?");
	}

	public CheckBox getTurnOnSNCModeCHB() {
		return new CheckBox("Turn On SNC Mode?");
	}

	public CheckBox getTurnOnSNCModeforRepositoryDestinationCHB() {
		return new CheckBox("Turn On SNC Mode for Repository Destination?");
	}

	public Text getRepositoryLogonPasswordTXT() {
		return new LabeledText(this, "Repository Logon Password:");
	}

	public Text getRepositoryLogonUserTXT() {
		return new LabeledText(this, "Repository Logon User:");
	}

	public Text getRepositoryDestinationTXT() {
		return new LabeledText(this, "Repository Destination:");
	}

	public Text getSNCPartnerNameTXT() {
		return new LabeledText(this, "SNC Partner Name:");
	}

	public Text getSNCNameTXT() {
		return new LabeledText(this, "SNC Name:");
	}

	public Text getSNCLibraryPathTXT() {
		return new LabeledText(this, "SNC Library Path:");
	}

	public Text getConnectionPoolMaxGetClientTimeTXT() {
		return new LabeledText(this, "Connection Pool Max Get Client Time:");
	}

	public Text getConnectionPoolExpireCheckPeriodTXT() {
		return new LabeledText(this, "Connection Pool Expire Check Period:");
	}

	public Text getConnectionPoolExpirationTimeTXT() {
		return new LabeledText(this, "Connection Pool Expiration Time:");
	}

	public Text getConnectionPoolCapacityTXT() {
		return new LabeledText(this, "Connection Pool Capacity:");
	}

	public Text getConnectionPoolPeakLimitTXT() {
		return new LabeledText(this, "Connection Pool Peak Limit:");
	}

	public Button getDenyUseofInitialPasswords() {
		return new CheckBox("Deny Use of Initial Passwords?");
	}

	public CheckBox getReqeustSSOTicketCHB() {
		return new CheckBox("Reqeust SSO Ticket?");
	}

	public Text getInitialCodepage() {
		return new LabeledText(this, "Initial Codepage:");
	}

	public CheckBox getEnableLogonCheckCHB() {
		return new CheckBox("Enable Logon Check?");
	}

	public Text getLogonLanguageTXT() {
		return new LabeledText(this, "Logon Language:");
	}

	public Text getSAPX509LoginTicketTXT() {
		return new LabeledText(this, "SAP X509 Login Ticket:");
	}

	public Text getSAPSSOLogonTicketTXT() {
		return new LabeledText(this, "SAP SSO Logon Ticket:");
	}

	public Text getLogonPasswordTXT() {
		return new LabeledText(this, "Logon Password:");
	}

	public Text getLogonUserAliasTXT() {
		return new LabeledText(this, "Logon User Alias:");
	}

	public Text getLogonUserTXT() {
		return new LabeledText(this, "Logon User:");
	}

	public Text getSAPApplicationServerGroupTXT() {
		return new LabeledText(this, "SAP Application Server Group:");
	}

	public Text getSAPSystemIDTXT() {
		return new LabeledText(this, "SAP System ID:");
	}

	public Text getSAPMessageServerPortTXT() {
		return new LabeledText(this, "SAP Message Server Port:");
	}

	public Text getSAPMessageServerTXT() {
		return new LabeledText(this, "SAP Message Server:");
	}

	public Text getSAPSystemNumberTXT() {
		return new LabeledText(this, "SAP System Number:");
	}

	public Text getSAPClientTXT() {
		return new LabeledText(this, "SAP Client:");
	}

	public Text getSAPApplicationServerTXT() {
		return new LabeledText(this, "SAP Application Server:");
	}

	public CCombo getSAPApplicationTypeCMB() {
		return new LabeledCCombo("SAP Authentication Type:");
	}

	public CCombo getSelectCPICTraceCMB() {
		return new LabeledCCombo("Select CPIC Trace:");
	}

	public CCombo getSNCLevelOfSecurityCMB() {
		return new LabeledCCombo("SNC Level of Security:");
	}

}

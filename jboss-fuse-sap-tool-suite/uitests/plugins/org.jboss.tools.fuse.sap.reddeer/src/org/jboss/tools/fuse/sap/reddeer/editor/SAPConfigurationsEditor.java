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
package org.jboss.tools.fuse.sap.reddeer.editor;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.condition.MenuItemIsEnabled;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.editor.ConfigurationsEditor;
import org.jboss.tools.fuse.sap.reddeer.wizard.SAPConfigurationWizard;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPConfigurationsEditor extends ConfigurationsEditor {

	public static final String[] SAP_CONNECTION_PATH = new String[] { "SAP", "SAP Connection" };
	public static final String[] SAP_CONFIG_PATH = new String[] { "SAP", "sap-configuration (SAP Connection)" };

	public SAPConfigurationsEditor(String title) {
		super(title);
	}

	public SAPConfigurationWizard addSapConfig() {
		addConfig(SAP_CONNECTION_PATH);
		return new SAPConfigurationWizard();
	}

	public SAPConfigurationWizard editSapConfig() {
		editConfig(SAP_CONFIG_PATH);
		return new SAPConfigurationWizard();
	}

	public SAPConfigurationWizard deleteSapConfig() {
		deleteConfig(SAP_CONFIG_PATH);
		return new SAPConfigurationWizard();
	}

	public void selectConfig(String... path) {
		activate();
		new DefaultTreeItem(path).select();
	}

	public void addConfig(String... path) {
		activate();
		new PushButton("Add").click();
		new WaitUntil(new ShellIsAvailable("Create new global element..."));
		new DefaultShell("Create new global element...");
		new DefaultTreeItem(path).select();
		new PushButton("OK").click();
	}

	public void editConfig(String... path) {
		selectConfig(path);
		new PushButton("Edit").click();
	}

	public void deleteConfig(String... path) {
		selectConfig(path);
		new PushButton("Delete").click();
	}

	@Override
	public void save() {
		ShellMenuItem saveItem = new ShellMenuItem(new WorkbenchShell(), "File", "Save");
		new WaitUntil(new MenuItemIsEnabled(saveItem), false);
		super.save();
		new WaitUntil(new ShellIsAvailable("Progress Information"), false);
		new WaitWhile(new ShellIsAvailable("Progress Information"), TimePeriod.LONG);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

}

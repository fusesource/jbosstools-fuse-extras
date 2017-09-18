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
package org.jboss.tools.fuse.sap.qe.reddeer.editor;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.jboss.tools.fuse.qe.reddeer.editor.ConfigurationsEditor;
import org.jboss.tools.fuse.sap.qe.reddeer.wizard.SAPConfigurationWizard;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPConfigurationsEditor extends ConfigurationsEditor {

	public static final String[] SAP_CONNECTION_PATH = new String[] { "SAP", "SAP Connection" };
	public static final String[] SAP_CONFIG_PATH = new String[] { "SAP", "sap-configuration (SAP Connection)" };

	public SAPConfigurationsEditor(String project, String title) {
		super(project, title);
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

	@Override
	public void save() {
		super.save();
		new WaitUntil(new ShellIsAvailable("Progress Information"), false);
		new WaitWhile(new ShellIsAvailable("Progress Information"), TimePeriod.LONG);
	}

}

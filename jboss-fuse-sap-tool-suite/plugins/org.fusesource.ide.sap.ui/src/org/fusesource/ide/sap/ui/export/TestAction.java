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
package org.fusesource.ide.sap.ui.export;

import org.eclipse.jface.action.Action;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.dialog.TestDestinationDialog;
import org.fusesource.ide.sap.ui.dialog.TestServerDialog;

class TestAction extends Action {

	private final SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage;

	public TestAction(SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage) {
		super(Messages.SapGlobalConnectionConfigurationPage_Test, Activator.getDefault().getImageRegistry().getDescriptor(Activator.TEST_IMAGE));
		this.sapGlobalConnectionConfigurationPage = sapGlobalConnectionConfigurationPage;
	}
	
	@Override
	public void run() {
		if (this.sapGlobalConnectionConfigurationPage.selection.size() == 1) {
			Object obj = this.sapGlobalConnectionConfigurationPage.selection.getFirstElement();
			if (obj instanceof DestinationDataStoreEntryImpl) {
				String name = ((DestinationDataStoreEntryImpl) obj).getKey();
				TestDestinationDialog testDestinationDialog = new TestDestinationDialog(this.sapGlobalConnectionConfigurationPage.getShell(), name);
				testDestinationDialog.open();
			} else if (obj instanceof ServerDataStoreEntryImpl) {
				String name = ((ServerDataStoreEntryImpl) obj).getKey();
				TestServerDialog testServerDialog = new TestServerDialog(this.sapGlobalConnectionConfigurationPage.getShell(), name);
				testServerDialog.open();
			}
		}
	}
}
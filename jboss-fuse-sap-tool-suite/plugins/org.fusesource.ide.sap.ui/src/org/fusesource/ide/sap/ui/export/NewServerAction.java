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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.dialogs.InputDialog;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.command.NewStoreValidator;

class NewServerAction extends AbstractNewAction<ServerDataStore> {

	public NewServerAction(SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage, EditingDomain editingDomain) {
		super(Messages.SapGlobalConnectionConfigurationPage_NewServer, Activator.getDefault().getImageRegistry().getDescriptor(Activator.SERVER_DATA_STORE_ENTRY_IMAGE),
				sapGlobalConnectionConfigurationPage, editingDomain);
	}

	@Override
	protected EAttribute getStoreDataEntry() {
		return RfcPackage.Literals.SERVER_DATA_STORE_ENTRY__KEY;
	}

	@Override
	protected EReference getStoreDataValue() {
		return RfcPackage.Literals.SERVER_DATA_STORE_ENTRY__VALUE;
	}

	@Override
	protected EReference getStoreDataFeature() {
		return RfcPackage.Literals.SERVER_DATA_STORE__SERVER_DATA;
	}

	@Override
	protected EReference getStoreEntriesFeature() {
		return RfcPackage.Literals.SERVER_DATA_STORE__ENTRIES;
	}

	/**
	 * @param destinationDataStore
	 * @param destinationDataStoreEntry
	 * @return
	 */
	@Override
	protected InputDialog createNewDialog(ServerDataStore serverDataStore, Object serverDataStoreEntry) {
		return new InputDialog(sapGlobalConnectionConfigurationPage.getShell(), Messages.ServerDialog_shellCreateTitle, Messages.ServerDialog_message, "",
				new NewStoreValidator(serverDataStore.getEntries().keySet(), Messages.ServerDialog_message, Messages.ServerDialog_serverAlreadyExists));
	}

	@Override
	protected boolean isOfCorrectInstanceOf(Object obj) {
		return obj instanceof ServerDataStore;
	}

}
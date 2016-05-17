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

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.dialogs.Dialog;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.dialog.DestinationDialog;

class NewDestinationAction extends AbstractNewAction<DestinationDataStore> {
	
	public NewDestinationAction(SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage, EditingDomain editingDomain) {
		super(Messages.SapGlobalConnectionConfigurationPage_NewDestination, Activator.getDefault().getImageRegistry().getDescriptor(Activator.DESTINATION_DATA_STORE_ENTRY_IMAGE),
				sapGlobalConnectionConfigurationPage, editingDomain);
	}
	
	@Override
	protected EReference getStoreDataValue() {
		return RfcPackage.Literals.DESTINATION_DATA_STORE_ENTRY__VALUE;
	}

	@Override
	protected EReference getStoreDataFeature() {
		return RfcPackage.Literals.DESTINATION_DATA_STORE__DESTINATION_DATA;
	}

	@Override
	protected EReference getStoreEntriesFeature() {
		return RfcPackage.Literals.DESTINATION_DATA_STORE__ENTRIES;
	}

	/**
	 * @param destinationDataStore
	 * @param destinationDataStoreEntry
	 * @return
	 */
	@Override
	protected Dialog createNewDialog(DestinationDataStore destinationDataStore, Object destinationDataStoreEntry) {
		return new DestinationDialog(this.sapGlobalConnectionConfigurationPage.getShell(), DestinationDialog.Type.CREATE, editingDomain,
				(DestinationDataStore) destinationDataStore, (DestinationDataStoreEntryImpl) destinationDataStoreEntry);
	}

	@Override
	protected boolean isOfCorrectInstanceOf(Object obj) {
		return obj instanceof DestinationDataStore;
	}

}
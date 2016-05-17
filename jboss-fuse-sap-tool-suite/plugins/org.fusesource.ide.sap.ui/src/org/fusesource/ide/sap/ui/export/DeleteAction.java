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

import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;

class DeleteAction extends Action {

	private final SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage;
	private final EditingDomain editingDomain;

	public DeleteAction(SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage, EditingDomain editingDomain) {
		super(Messages.SapGlobalConnectionConfigurationPage_Delete, PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		this.sapGlobalConnectionConfigurationPage = sapGlobalConnectionConfigurationPage;
		this.editingDomain = editingDomain;
	}
	
	@Override
	public void run() {
		if (this.sapGlobalConnectionConfigurationPage.selection.size() == 1) {
			Object obj = this.sapGlobalConnectionConfigurationPage.selection.getFirstElement();
			if (obj instanceof EObject) {
				EObject eObject = (EObject) obj;
				if (editingDomain != null) {
					Command removeValueCommand = null;
					Command deleteEntryCommand = DeleteCommand.create(editingDomain, Collections.singletonList(eObject));
					if (eObject instanceof DestinationDataStoreEntryImpl) {
						DestinationData destinationData = ((DestinationDataStoreEntryImpl)eObject).getValue();
						removeValueCommand = RemoveCommand.create(editingDomain, destinationData.eContainer(), RfcPackage.Literals.DESTINATION_DATA_STORE__DESTINATION_DATA,
								Collections.singletonList(destinationData));
					} else if (eObject instanceof ServerDataStoreEntryImpl) {
						ServerData serverData = ((ServerDataStoreEntryImpl)eObject).getValue();
						removeValueCommand = RemoveCommand.create(editingDomain, serverData.eContainer(), RfcPackage.Literals.SERVER_DATA_STORE__SERVER_DATA,
								Collections.singletonList(serverData));
					}
					CompoundCommand command = new CompoundCommand();
					command.append(deleteEntryCommand);
					command.append(removeValueCommand);
					editingDomain.getCommandStack().execute(command);
				}
			}
		}
	}
}
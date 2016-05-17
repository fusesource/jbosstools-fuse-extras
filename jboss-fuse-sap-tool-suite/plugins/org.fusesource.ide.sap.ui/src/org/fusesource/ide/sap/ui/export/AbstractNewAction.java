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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class AbstractNewAction<T> extends Action {

	protected final SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage;
	protected final EditingDomain editingDomain;

	public AbstractNewAction(String text, ImageDescriptor image, SapGlobalConnectionConfigurationPage sapGlobalConnectionConfigurationPage, EditingDomain editingDomain) {
		super(text, image);
		this.sapGlobalConnectionConfigurationPage = sapGlobalConnectionConfigurationPage;
		this.editingDomain = editingDomain;
	}

	@Override
	public void run() {
		Command createDataStoreEntryCommand = null;
		Command createDataCommand = null;
		T dataStore;
		Object dataStoreEntry = null;
		Object data = null;

		if (this.sapGlobalConnectionConfigurationPage.selection.size() == 1) {
			Object obj = this.sapGlobalConnectionConfigurationPage.selection.getFirstElement();
			if (isOfCorrectInstanceOf(obj)) {
				dataStore = (T) obj;
				if (editingDomain != null) {
					Collection<?> descriptors = editingDomain.getNewChildDescriptors(dataStore, null);
					for (Object descriptor : descriptors) {
						CommandParameter parameter = (CommandParameter) descriptor;
						if (parameter.getFeature() == getStoreEntriesFeature()) {
							dataStoreEntry = parameter.getValue();
							createDataStoreEntryCommand = CreateChildCommand.create(editingDomain, dataStore, descriptor,
									Collections.singletonList(dataStore));
							continue;
						} else if (parameter.getFeature() == getStoreDataFeature()) {
							data = parameter.getValue();
							createDataCommand = CreateChildCommand.create(editingDomain, dataStore, descriptor,
									Collections.singletonList(dataStore));
							continue;
						}

					}
					CompoundCommand command = new CompoundCommand();
					command.append(createDataCommand);
					command.append(SetCommand.create(editingDomain, dataStoreEntry, getStoreDataValue(), data));
					command.append(createDataStoreEntryCommand);
					((TransactionalCommandStack) editingDomain.getCommandStack()).begin();
					Dialog newNameDialog = createNewDialog(dataStore, dataStoreEntry);
					int status = newNameDialog.open();
					if (status != Window.OK) {
						((TransactionalCommandStack) editingDomain.getCommandStack()).rollback();
						return;
					}
					editingDomain.getCommandStack().execute(command);
					((TransactionalCommandStack) editingDomain.getCommandStack()).commit();
					sapGlobalConnectionConfigurationPage.setSelectionToViewer(Collections.singleton(dataStoreEntry));
				}
			}
		}
	}

	protected abstract boolean isOfCorrectInstanceOf(Object obj);

	protected abstract Dialog createNewDialog(T dataStore, Object dataStoreEntry);

	protected abstract EReference getStoreDataValue();

	protected abstract EReference getStoreDataFeature();

	protected abstract EReference getStoreEntriesFeature();
}

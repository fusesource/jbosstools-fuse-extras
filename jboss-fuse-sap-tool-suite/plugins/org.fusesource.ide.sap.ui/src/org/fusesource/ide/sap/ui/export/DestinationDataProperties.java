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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.properties.uicreator.AuthenticationDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.BasicDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.ConnectionDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.IDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.PoolDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.RepositoryDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SncDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SpecialDestinationDataUICreator;

public class DestinationDataProperties extends AbstractDataProperties {
	
	private DestinationDataStoreEntryImpl destinationDataStoreEntry;
	private IDestinationDataUICreator basicDestinationDataUICreator = new BasicDestinationDataUICreator();
	private IDestinationDataUICreator connectionDestinationDataUICreator = new ConnectionDestinationDataUICreator();
	private IDestinationDataUICreator authenticationDestinationDataUICreator = new AuthenticationDestinationDataUICreator();
	private IDestinationDataUICreator specialDestinationDataUICreator = new SpecialDestinationDataUICreator();
	private IDestinationDataUICreator poolDestinationDataUICreator = new PoolDestinationDataUICreator();
	private IDestinationDataUICreator sncDestinationDataUICreator = new SncDestinationDataUICreator();
	private IDestinationDataUICreator repositoryPoolDestinationDataUICreator = new RepositoryDestinationDataUICreator();

	/**
	 * @param widgetFactory
	 * @param destinationDataTabFolder
	 */
	@Override
	protected void createTabItems(TabbedPropertySheetWidgetFactory widgetFactory, CTabFolder destinationDataTabFolder) {
		CTabItem basicTabItem = createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_Basic, basicDestinationDataUICreator);
		createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_Connection, connectionDestinationDataUICreator);
		createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_Authentication, authenticationDestinationDataUICreator);
		createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_Special, specialDestinationDataUICreator);
		createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_Pool, poolDestinationDataUICreator);
		createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_SNC, sncDestinationDataUICreator);
		createTabItem(widgetFactory, destinationDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_Repository, repositoryPoolDestinationDataUICreator);
		destinationDataTabFolder.setSelection(basicTabItem);
	}

	public void setInput(ISelection selection) {
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection)selection).getFirstElement();
		Assert.isTrue(input instanceof DestinationDataStoreEntryImpl);
		destinationDataStoreEntry = (DestinationDataStoreEntryImpl) input;
		editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(destinationDataStoreEntry);
		initDataBindings();
	}
	
	private DataBindingContext initDataBindings() {
		if (bindingContext != null) {
			bindingContext.dispose();
			bindingContext = null;
		}
		bindingContext = new DataBindingContext();
		
		basicDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
		connectionDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
		authenticationDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
		specialDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
		poolDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
		sncDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
		repositoryPoolDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);

		return bindingContext;
	}
}
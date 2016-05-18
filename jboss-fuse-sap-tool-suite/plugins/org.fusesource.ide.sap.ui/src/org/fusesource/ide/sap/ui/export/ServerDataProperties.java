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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.properties.uicreator.IServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.MandatoryServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.OptionalServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SncServerDataUICreator;

public class ServerDataProperties extends AbstractDataProperties {
	
	private ServerDataStoreEntryImpl serverDataStoreEntry;

	private IServerDataUICreator mandatoryServerDataUICreator = new MandatoryServerDataUICreator();
	private IServerDataUICreator optionalServerDataUICreator = new OptionalServerDataUICreator();
	private IServerDataUICreator sncServerDataUICreator = new SncServerDataUICreator();

	@Override
	public CTabFolder createControl(Composite properties) {
		TabbedPropertySheetWidgetFactory widgetFactory = new TabbedPropertySheetWidgetFactory();
		CTabFolder serverDataTabFolder = widgetFactory.createTabFolder(properties, SWT.BORDER);
		serverDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		serverDataTabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				// With CTabItem, it seems that widgets are not refreshed
				// automatically as it is the case in Property Sections
				bindingContext.updateTargets();
			}
		});
		
		createTabItems(widgetFactory, serverDataTabFolder);

		return serverDataTabFolder;
	}

	/**
	 * @param widgetFactory
	 * @param serverDataTabFolder
	 */
	@Override
	protected void createTabItems(TabbedPropertySheetWidgetFactory widgetFactory, CTabFolder serverDataTabFolder) {
		CTabItem mandatoryTabItem = createTabItem(widgetFactory, serverDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_ServerDataMandatoryItemTitle,
				mandatoryServerDataUICreator);
		createTabItem(widgetFactory, serverDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_ServerDataOptionalItemTitle, optionalServerDataUICreator);
		createTabItem(widgetFactory, serverDataTabFolder, Messages.SapGlobalConnectionConfigurationPage_ServerDataSncItemTitle, sncServerDataUICreator);
		serverDataTabFolder.setSelection(mandatoryTabItem);
	}
	
	public void setInput(ISelection selection) {
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection)selection).getFirstElement();
		Assert.isTrue(input instanceof ServerDataStoreEntryImpl);
		serverDataStoreEntry = (ServerDataStoreEntryImpl) input;
		editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(serverDataStoreEntry);
		initDataBindings();
	}
	
	private DataBindingContext initDataBindings() {
		if (bindingContext != null) {
			bindingContext.dispose();
			bindingContext = null;
		}
		bindingContext = new DataBindingContext();
		
		mandatoryServerDataUICreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);
		optionalServerDataUICreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);
		sncServerDataUICreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);

		return bindingContext; 
	}

}
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
package org.fusesource.ide.sap.ui.properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.properties.uicreator.IServerDataUICreator;

public abstract class ServerDataPropertySection extends BasePropertySection {

	protected ServerDataStoreEntryImpl serverDataStoreEntry;
	protected EditingDomain editingDomain;
	private IServerDataUICreator uiCreator;

	public ServerDataPropertySection() {
		uiCreator = createUICreator();
	}

	protected abstract IServerDataUICreator createUICreator();

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection)selection).getFirstElement();
		Assert.isTrue(input instanceof ServerDataStoreEntryImpl);
		serverDataStoreEntry = (ServerDataStoreEntryImpl) input;
		editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(serverDataStoreEntry);
		initDataBindings();
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite authContainer = getWidgetFactory().createFlatFormComposite(parent);
		uiCreator.createControls(authContainer, getWidgetFactory());
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = super.initDataBindings();
		uiCreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);
		return bindingContext;
	}

}

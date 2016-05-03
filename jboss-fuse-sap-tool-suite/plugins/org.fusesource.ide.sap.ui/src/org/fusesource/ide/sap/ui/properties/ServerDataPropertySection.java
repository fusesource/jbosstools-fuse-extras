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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;

public abstract class ServerDataPropertySection extends BasePropertySection {

	protected ServerDataStoreEntryImpl serverDataStoreEntry;
	protected EditingDomain editingDomain;

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

}

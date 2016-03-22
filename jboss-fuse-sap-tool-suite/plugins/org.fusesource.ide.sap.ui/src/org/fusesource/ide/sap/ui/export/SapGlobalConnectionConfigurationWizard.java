/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.export;

import static org.fusesource.ide.sap.ui.util.ModelUtil.getSapConnectionConfigurationModelFromDocument;
import static org.fusesource.ide.sap.ui.util.ModelUtil.setSapConnectionConfigurationModelIntoDocument;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.camel.component.sap.util.ComponentDestinationDataProvider;
import org.fusesource.camel.component.sap.util.ComponentServerDataProvider;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.sap.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Wizard to edit SAP Global Connection Configuration.
 * 
 * @author William Collins <punkhornsw@gmail.com>
 *
 */
public class SapGlobalConnectionConfigurationWizard extends Wizard implements IExportWizard, GlobalConfigurationTypeWizard {
	
	public static final String ID = "org.fusesource.ide.sap.ui.SapGlobalConnectionConfigurationWizard"; //$NON-NLS-1$
	
	private DataBindingContext context;
	private SapGlobalConnectionConfigurationPage exportPage;
	
	private Document document;
	private org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration sapConnectionConfigurationModel;
	
	public SapGlobalConnectionConfigurationWizard(Document document) {
		this.document = document;
		this.sapConnectionConfigurationModel = getSapConnectionConfigurationModelFromDocument(this.document);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.SapGlobalConnectionConfigurationWizard_EditSapConnectionConfiguration);
		setNeedsProgressMonitor(true);
		context = new DataBindingContext();
		exportPage = new SapGlobalConnectionConfigurationPage(context, sapConnectionConfigurationModel);
		
		// Register data stores
		ComponentDestinationDataProvider.INSTANCE.addDestinationDataStore(sapConnectionConfigurationModel.getDestinationDataStore());
		ComponentServerDataProvider.INSTANCE.addServerDataStore(sapConnectionConfigurationModel.getServerDataStore());
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(exportPage);
	}
	
	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		setSapConnectionConfigurationModelIntoDocument(this.document, this.sapConnectionConfigurationModel);
		unregisterDataStores();
		return true;
	}
	
	@Override
	public boolean performCancel() {
		unregisterDataStores();
		return true;
	}
	
	private void unregisterDataStores() {
		// Unregister data stores
		ComponentDestinationDataProvider.INSTANCE.removeDestinationDataStore(sapConnectionConfigurationModel.getDestinationDataStore());
		ComponentServerDataProvider.INSTANCE.removeServerDataStore(sapConnectionConfigurationModel.getServerDataStore());
	}

	@Override
	public Element getGlobalConfigurationElementNode() {
		return null;
	}

	@Override
	public void setGlobalConfigurationElementNode(Element node) {
		// NOOP
	}
	
}

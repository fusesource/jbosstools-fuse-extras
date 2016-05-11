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

import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.transform.dom.DOMResult;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.camel.component.sap.util.ComponentDestinationDataProvider;
import org.fusesource.camel.component.sap.util.ComponentServerDataProvider;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;
import org.fusesource.ide.sap.ui.edit.idoc.IdocItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.edit.rfc.RfcItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.jaxb.SapConnectionConfiguration;
import org.fusesource.ide.sap.ui.jaxb.SapConnectionConfigurationBuilder;
import org.w3c.dom.Element;

/**
 * Wizard to edit SAP Global Connection Configuration.
 * 
 * @author William Collins <punkhornsw@gmail.com>
 *
 */
public class SapGlobalConnectionConfigurationWizard extends Wizard implements IExportWizard, GlobalConfigurationTypeWizard {
	
	public static final String ID = "org.fusesource.ide.sap.ui.SapGlobalConnectionConfigurationWizard"; //$NON-NLS-1$
	
	/**
	 * This keeps track of the editing domain that is used to track all changes
	 * to the model.
	 */
	protected AdapterFactoryEditingDomain editingDomain;

	/**
	 * This is the one adapter factory used for providing views of the model.
	 */
	protected ComposedAdapterFactory adapterFactory;

	private DataBindingContext context;
	private SapGlobalConnectionConfigurationPage exportPage;
	
	private CamelFile camelFile;
	private org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration sapConnectionConfigurationModel;

	private Element globalConfigurationElementNode;
	
	public SapGlobalConnectionConfigurationWizard(CamelFile camelFile) {
		this.camelFile = camelFile;
		initializeEditingDomain();
		this.sapConnectionConfigurationModel = getSapConnectionConfigurationModelFromDocument(camelFile.getDocument(), editingDomain);
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
	public boolean performFinish() {
		SapConnectionConfiguration sapConnectionConfiguration = new SapConnectionConfiguration();
		SapConnectionConfigurationBuilder.populateSapConnectionConfiguration(sapConnectionConfigurationModel, sapConnectionConfiguration);
		try {
			DOMResult result = sapConnectionConfiguration.marshal();
			final Element beanNode = (Element) result.getNode().getFirstChild();
			final Element importedBeanNode = (Element) camelFile.getDocument().importNode(beanNode, true);
			setGlobalConfigurationElementNode(importedBeanNode);
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void dispose() {
		unregisterDataStores();
		super.dispose();
	}
	
	protected void initializeEditingDomain() {
		// Create an adapter factory that yields item providers.
		//
		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new RfcItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new IdocItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		// Create the command stack that will notify this editor as commands are
		// executed.
		//
		BasicCommandStack commandStack = new TransactionalCommandStack();

		// Create the editing domain with a special command stack.
		//
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
	}

	private void unregisterDataStores() {
		// Unregister data stores
		ComponentDestinationDataProvider.INSTANCE.removeDestinationDataStore(sapConnectionConfigurationModel.getDestinationDataStore());
		ComponentServerDataProvider.INSTANCE.removeServerDataStore(sapConnectionConfigurationModel.getServerDataStore());
	}

	@Override
	public Element getGlobalConfigurationElementNode() {
		return globalConfigurationElementNode;
	}

	@Override
	public void setGlobalConfigurationElementNode(Element node) {
		this.globalConfigurationElementNode = node;
	}

	/**
	 * /!\ Public for test purpose only
	 * 
	 * @param sapConnectionConfigurationModel
	 *            the sapConnectionConfigurationModel to set
	 */
	public void setSapConnectionConfigurationModel(org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration sapConnectionConfigurationModel) {
		this.sapConnectionConfigurationModel = sapConnectionConfigurationModel;
	}
	
}

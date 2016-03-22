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
package org.fusesource.ide.sap.ui.editor.globalconf.provider;

import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_ARTIFACT_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_GROUP_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_VERSION;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigElementType;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.sap.ui.export.SapGlobalConnectionConfigurationWizard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * SAP Server Contribution - Provides wizard to edit SAP Global Connection Configuration.
 * 
 * @author William Collins <punkhornsw@gmail.com>
 */
public class SAPServerContribution implements ICustomGlobalConfigElementContribution {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#createGlobalElement(org.fusesource.ide.camel.model.service.core.model.CamelFile)
	 */
	@Override
	public GlobalConfigurationTypeWizard createGlobalElement(CamelFile camelFile) {
		return createWizard(camelFile.getDocument());
	}
	
	/*
	 * 	(non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#modifyGlobalElement(org.w3c.dom.Document, org.w3c.dom.Node)
	 */
	@Override
	public GlobalConfigurationTypeWizard modifyGlobalElement(Document document) {
		return createWizard(document);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#onGlobalElementDeleted(org.w3c.dom.Node)
	 */
	@Override
	public void onGlobalElementDeleted(Node node) {
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#canHandle(org.w3c.dom.Node)
	 */
	@Override
	public boolean canHandle(Node nodeToHandle) {
		return ((Element)nodeToHandle).getAttribute("class").equals("org.fusesource.camel.component.sap.SapConnectionConfiguration");
	}	
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#getGlobalConfigElementType()
	 */
	@Override
	public GlobalConfigElementType getGlobalConfigElementType() {
		return GlobalConfigElementType.GLOBAL_ELEMENT;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#getElementDependencies()
	 */
	@Override
	public List<Dependency> getElementDependencies() {
		List<Dependency> deps = new ArrayList<Dependency>();
		
		Dependency dep = new Dependency();
        dep.setGroupId(CAMEL_SAP_GROUP_ID);
        dep.setArtifactId(CAMEL_SAP_ARTIFACT_ID);
        dep.setVersion(CAMEL_SAP_VERSION);
        deps.add(dep);
		
		return deps;
	}

	/**
	 * Creates SAP Global Connection Configuration Wizard
	 * 
	 * @param document - document edited by wizard
	 * @return SAP Global Connection Configuration Wizard
	 */
	private GlobalConfigurationTypeWizard createWizard(Document document) {
		IWorkbenchWizard wizard = new SapGlobalConnectionConfigurationWizard(document);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if (!(selection instanceof IStructuredSelection)) {
			selection = StructuredSelection.EMPTY;
		}
		wizard.init(PlatformUI.getWorkbench(), (StructuredSelection) selection);
		return (GlobalConfigurationTypeWizard) wizard;
	}

}

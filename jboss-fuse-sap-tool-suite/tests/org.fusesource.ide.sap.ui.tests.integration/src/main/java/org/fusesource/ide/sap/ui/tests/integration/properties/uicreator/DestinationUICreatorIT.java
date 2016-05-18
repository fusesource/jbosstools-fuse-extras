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
package org.fusesource.ide.sap.ui.tests.integration.properties.uicreator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.properties.uicreator.AuthenticationDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.BasicDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.ConnectionDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.IDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.PoolDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.RepositoryDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SncDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SpecialDestinationDataUICreator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class DestinationUICreatorIT {

	@Test
	public void testAuthenticationDestinationCreation() {
		testUICreator(new AuthenticationDestinationDataUICreator());
	}

	@Test
	public void testBasicDestinationCreation() {
		testUICreator(new BasicDestinationDataUICreator());
	}

	@Test
	public void testConnectionDestinationCreation() {
		testUICreator(new ConnectionDestinationDataUICreator());
	}

	@Test
	public void testPoolDestinationCreation() {
		testUICreator(new PoolDestinationDataUICreator());
	}

	@Test
	public void testRepositoryDestinationCreation() {
		testUICreator(new RepositoryDestinationDataUICreator());
	}

	@Test
	public void testSncDestinationCreation() {
		testUICreator(new SncDestinationDataUICreator());
	}

	@Test
	public void testSpecialDestinationCreation() {
		testUICreator(new SpecialDestinationDataUICreator());
	}


	/**
	 * @param uiCreator
	 */
	private void testUICreator(IDestinationDataUICreator uiCreator) {
		uiCreator.createControls(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new TabbedPropertySheetWidgetFactory());

		DestinationDataStore destinationDataStore = RfcFactory.eINSTANCE.createDestinationDataStore();
		DestinationData destinationData = RfcFactory.eINSTANCE.createDestinationData();
		destinationData.setAliasUser("aliasUserTest");
		destinationData.setPeakLimit("12");
		destinationDataStore.getEntries().put("test", destinationData);
		EcoreEMap<?, ?> destinationDataStoreEntries = (EcoreEMap<?, ?>) destinationDataStore.eGet(RfcPackage.Literals.DESTINATION_DATA_STORE__ENTRIES);
		DestinationDataStoreEntryImpl destinationDataStoreEntry = (DestinationDataStoreEntryImpl) destinationDataStoreEntries.entrySet().iterator().next();
		assertThat(destinationDataStoreEntry).isNotNull();

		uiCreator.initDataBindings(new DataBindingContext(), AdapterFactoryEditingDomain.getEditingDomainFor(destinationDataStoreEntry), destinationDataStoreEntry);
	}
}

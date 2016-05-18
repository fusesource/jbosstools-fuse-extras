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
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.properties.uicreator.IServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.MandatoryServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.OptionalServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SncServerDataUICreator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class ServerUICreatorIT {

	@Test
	public void testMandatoryServerCreation() {
		testUICreator(new MandatoryServerDataUICreator());
	}

	@Test
	public void testOptionalServerCreation() {
		testUICreator(new OptionalServerDataUICreator());
	}

	@Test
	public void testSncServerCreation() {
		testUICreator(new SncServerDataUICreator());
	}

	/**
	 * @param uiCreator
	 */
	private void testUICreator(IServerDataUICreator uiCreator) {
		uiCreator.createControls(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new TabbedPropertySheetWidgetFactory());

		ServerDataStore serverDataStore = RfcFactory.eINSTANCE.createServerDataStore();
		ServerData serverData = RfcFactory.eINSTANCE.createServerData();
		serverData.setConnectionCount("12");
		serverData.setSaprouter("sapRouterTest");
		serverDataStore.getEntries().put("test", serverData);
		EcoreEMap<?, ?> serverDataStoreEntries = (EcoreEMap<?, ?>) serverDataStore.eGet(RfcPackage.Literals.SERVER_DATA_STORE__ENTRIES);
		ServerDataStoreEntryImpl serverDataStoreEntry = (ServerDataStoreEntryImpl) serverDataStoreEntries.entrySet().iterator().next();
		assertThat(serverDataStoreEntry).isNotNull();

		uiCreator.initDataBindings(new DataBindingContext(), AdapterFactoryEditingDomain.getEditingDomainFor(serverDataStoreEntry), serverDataStoreEntry);
	}

}

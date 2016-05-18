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
package org.fusesource.ide.sap.ui.tests.integration.properties;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.export.ServerDataProperties;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class ServerDataPropertiesIT {

	@Test
	public void testCreation() {
		final ServerDataProperties serverDataProperties = new ServerDataProperties();
		serverDataProperties.createControl(Display.getDefault().getActiveShell());
		ServerDataStore serverDataStore = RfcFactory.eINSTANCE.createServerDataStore();
		serverDataStore.getEntries().put("test", RfcFactory.eINSTANCE.createServerData());
		EcoreEMap<?, ?> serverDataStoreEntries = (EcoreEMap<?, ?>) serverDataStore.eGet(RfcPackage.Literals.SERVER_DATA_STORE__ENTRIES);
		ServerDataStoreEntryImpl serverDataStoreEntry = (ServerDataStoreEntryImpl) serverDataStoreEntries.entrySet().iterator().next();
		serverDataProperties.setInput(new StructuredSelection(serverDataStoreEntry));
	}

}

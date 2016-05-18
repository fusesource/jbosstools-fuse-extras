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
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.export.DestinationDataProperties;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class DestinationDataPropertiesIT {

	@Test
	public void testCreation() {
		final DestinationDataProperties destinationDataProperties = new DestinationDataProperties();
		destinationDataProperties.createControl(Display.getDefault().getActiveShell());
		DestinationDataStore destinationDataStore = RfcFactory.eINSTANCE.createDestinationDataStore();
		destinationDataStore.getEntries().put("test", RfcFactory.eINSTANCE.createDestinationData());
		EcoreEMap<?, ?> destinationDataStoreEntries = (EcoreEMap<?, ?>) destinationDataStore.eGet(RfcPackage.Literals.DESTINATION_DATA_STORE__ENTRIES);
		DestinationDataStoreEntryImpl destinationDataStoreEntry = (DestinationDataStoreEntryImpl) destinationDataStoreEntries.entrySet().iterator().next();
		destinationDataProperties.setInput(new StructuredSelection(destinationDataStoreEntry));
	}

}

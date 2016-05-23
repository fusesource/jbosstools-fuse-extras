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
package org.fusesource.ide.sap.ui.properties.uicreator;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SAPDestinationContentProposalProviderTest {

	@Test
	public void testFoundProposals() throws Exception {
		SapConnectionConfiguration sapConnectionConfiguration = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
		DestinationDataStore destinationDataStore = RfcFactory.eINSTANCE.createDestinationDataStore();
		destinationDataStore.getEntries().put("test", RfcFactory.eINSTANCE.createDestinationData());
		destinationDataStore.getEntries().put("anotherFiltered", RfcFactory.eINSTANCE.createDestinationData());
		destinationDataStore.getEntries().put("test2", RfcFactory.eINSTANCE.createDestinationData());
		sapConnectionConfiguration.setDestinationDataStore(destinationDataStore);
		ServerDataStoreEntryImpl serverDataStoreEntry = createServerDataStoreWithOneEntry(sapConnectionConfiguration);

		final IContentProposal[] proposals = new SAPDestinationContentProposalProvider(serverDataStoreEntry).getProposals("te", 0);

		assertThat(proposals).hasSize(2);
	}

	@Test
	public void testNoProposalFound() throws Exception {
		SapConnectionConfiguration sapConnectionConfiguration = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
		DestinationDataStore destinationDataStore = RfcFactory.eINSTANCE.createDestinationDataStore();
		destinationDataStore.getEntries().put("test", RfcFactory.eINSTANCE.createDestinationData());
		sapConnectionConfiguration.setDestinationDataStore(destinationDataStore);
		ServerDataStoreEntryImpl serverDataStoreEntry = createServerDataStoreWithOneEntry(sapConnectionConfiguration);

		final IContentProposal[] proposals = new SAPDestinationContentProposalProvider(serverDataStoreEntry).getProposals("plop", 0);

		assertThat(proposals).isEmpty();
	}

	/**
	 * @param sapConnectionConfiguration
	 * @return
	 */
	private ServerDataStoreEntryImpl createServerDataStoreWithOneEntry(SapConnectionConfiguration sapConnectionConfiguration) {
		ServerDataStore serverDataStore = RfcFactory.eINSTANCE.createServerDataStore();
		serverDataStore.getEntries().put("Server test", RfcFactory.eINSTANCE.createServerData());
		EcoreEMap<?, ?> serverDataStoreEntries = (EcoreEMap<?, ?>) serverDataStore.eGet(RfcPackage.Literals.SERVER_DATA_STORE__ENTRIES);
		ServerDataStoreEntryImpl serverDataStoreEntry = (ServerDataStoreEntryImpl) serverDataStoreEntries.entrySet().iterator().next();
		sapConnectionConfiguration.setServerDataStore(serverDataStore);
		return serverDataStoreEntry;
	}

}

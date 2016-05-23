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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.impl.SapConnectionConfigurationImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;

/**
 * @author Aurelien Pupier
 *
 */
final class SAPDestinationContentProposalProvider implements IContentProposalProvider {

	private ServerDataStoreEntryImpl serverDataStoreEntry;

	public SAPDestinationContentProposalProvider(ServerDataStoreEntryImpl serverDataStoreEntry) {
		this.serverDataStoreEntry = serverDataStoreEntry;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		SapConnectionConfigurationImpl eContainer = (SapConnectionConfigurationImpl) serverDataStoreEntry.eContainer().eContainer();
		EMap<String, DestinationData> entriesDestination = eContainer.getDestinationDataStore().getEntries();
		List<ContentProposal> proposals = new ArrayList<>();
		for (Entry<String, DestinationData> destinations : entriesDestination.entrySet()) {
			final String destinationName = destinations.getKey();
			if (destinationName.startsWith(contents)) {
				proposals.add(new ContentProposal(destinationName));
			}
		}
		return proposals.toArray(new ContentProposal[proposals.size()]);
	}
}
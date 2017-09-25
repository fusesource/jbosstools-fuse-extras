/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.sap.reddeer.view;

import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.swt.api.Text;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPServerProperties extends PropertySheet {

	public void selectMandatory() {
		activate();
		selectTab("Mandatory");
	}

	public void selectOptional() {
		activate();
		selectTab("Optional");
	}

	public void selectSNC() {
		activate();
		selectTab("SNC");
	}

	public Text getGatewayHostText() {
		return new LabeledText("Gateway Host:");
	}

	public Text getGatewayPortText() {
		return new LabeledText("Gateway Port:");
	}

	public Text getProgramIDText() {
		return new LabeledText("Program ID:");
	}

	public Text getRepositoryDestinationText() {
		return new LabeledText("Repository Destination:");
	}

	public Text getConnectionCountText() {
		return new LabeledText("Connection Count:");
	}

}

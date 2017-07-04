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
package org.jboss.tools.fuse.sap.qe.reddeer.view;

import org.jboss.reddeer.eclipse.ui.views.properties.PropertiesView;
import org.jboss.reddeer.swt.api.Text;
import org.jboss.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPDestinationProperties extends PropertiesView {

	public void selectBasic() {
		activate();
		selectTab("Basic");
	}

	public void selectConnection() {
		activate();
		selectTab("Connection");
	}

	public Text getSAPApplicationServerText() {
		return new LabeledText("SAP Application Server:");
	}

	public Text getSAPSystemNumberText() {
		return new LabeledText("SAP System Number:");
	}

	public Text getSAPClientText() {
		return new LabeledText("SAP Client:");
	}

	public Text getLogonUserText() {
		return new LabeledText("Logon User:");
	}

	public Text getLogonPasswordText() {
		return new LabeledText("Logon Password:");
	}

	public Text getLogonLanguageText() {
		return new LabeledText("Logon Language:");
	}

}

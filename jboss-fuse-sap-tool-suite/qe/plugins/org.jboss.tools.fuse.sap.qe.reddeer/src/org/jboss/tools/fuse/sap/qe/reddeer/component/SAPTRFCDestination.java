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
package org.jboss.tools.fuse.sap.qe.reddeer.component;

import static org.jboss.tools.fuse.sap.qe.reddeer.component.SAPLabels.DESTINATION;
import static org.jboss.tools.fuse.sap.qe.reddeer.component.SAPLabels.RFC;

import org.jboss.tools.fuse.qe.reddeer.component.AbstractURICamelComponent;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPTRFCDestination extends AbstractURICamelComponent {

	public SAPTRFCDestination() {
		super("sap-trfc-destination");
		addProperty(DESTINATION, "destination");
		addProperty(RFC, "rfc");
	}

	@Override
	public String getPaletteEntry() {
		return "SAP tRFC Destination";
	}

}

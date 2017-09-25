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
package org.jboss.tools.fuse.sap.reddeer.component;

import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.RFC;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.SERVER;

import org.jboss.tools.fuse.reddeer.component.AbstractURICamelComponent;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPTRFCServer extends AbstractURICamelComponent {

	public SAPTRFCServer() {
		super("sap-trfc-server");
		addProperty(SERVER, "server");
		addProperty(RFC, "rfc");
	}

	@Override
	public String getPaletteEntry() {
		return "SAP tRFC Server";
	}

}

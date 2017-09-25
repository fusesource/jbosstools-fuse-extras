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

import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.APPLICATION_RELEASE;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.DESTINATION;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.IDOC_TYPE;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.IDOC_TYPE_EXTENSION;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.SYSTEM_RELEASE;

import org.jboss.tools.fuse.reddeer.component.AbstractURICamelComponent;

/**
 * 
 * @author apodhrad
 *
 */
public class SAPIDocDestination extends AbstractURICamelComponent {

	public SAPIDocDestination() {
		super("sap-idoc-destination");
		addProperty(DESTINATION, "destination");
		addProperty(IDOC_TYPE, "idocType");
		addProperty(IDOC_TYPE_EXTENSION, "idocTypeExtension");
		addProperty(SYSTEM_RELEASE, "systemRelease");
		addProperty(APPLICATION_RELEASE, "applicationRelease");
	}

	@Override
	public String getPaletteEntry() {
		return "SAP IDoc Destination";
	}

}

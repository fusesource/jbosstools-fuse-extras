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
package org.jboss.tools.fuse.sap.reddeer;

import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;

/**
 * 
 * @author apodhrad
 *
 */
public class SupportedSAPVersions extends SupportedCamelVersions {

	public static final String SAP_621_REDHAT_084 = "6.2.1.redhat-084";
	public static final String SAP_621_REDHAT_117 = "6.2.1.redhat-117";
	public static final String SAP_621_REDHAT_159 = "6.2.1.redhat-159";
	public static final String SAP_621_REDHAT_169 = "6.2.1.redhat-169";
	public static final String SAP_621_REDHAT_186 = "6.2.1.redhat-186";
	public static final String SAP_630_REDHAT_187 = "6.3.0.redhat-187";
	public static final String SAP_630_REDHAT_224 = "6.3.0.redhat-224";
	public static final String SAP_630_REDHAT_254 = "6.3.0.redhat-254";
	public static final String SAP_630_REDHAT_262 = "6.3.0.redhat-262";

	public static String getSAPVersion(String camelVersion) {
		switch (camelVersion) {
		case CAMEL_2_15_1_REDHAT_621084:
			return SAP_621_REDHAT_084;
		case CAMEL_2_15_1_REDHAT_621117:
			return SAP_621_REDHAT_117;
		case CAMEL_2_15_1_REDHAT_621159:
			return SAP_621_REDHAT_159;
		case CAMEL_2_15_1_REDHAT_621169:
			return SAP_621_REDHAT_169;
		case CAMEL_2_15_1_REDHAT_621186:
			return SAP_621_REDHAT_186;
		case CAMEL_2_17_0_REDHAT_630187:
			return SAP_630_REDHAT_187;
		case CAMEL_2_17_0_REDHAT_630224:
			return SAP_630_REDHAT_224;
		case CAMEL_2_17_0_REDHAT_630254:
			return SAP_630_REDHAT_254;
		case CAMEL_2_17_0_REDHAT_630262:
			return SAP_630_REDHAT_262;
		default:
			return SAP_630_REDHAT_262;
		}
	}

}

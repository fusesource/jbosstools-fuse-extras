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
package org.fusesource.ide.sap.ui.editor.provider;

import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_ARTIFACT_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_GROUP_ID;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;

public abstract class AbstractSAPPaletteEntry implements ICustomPaletteEntry{

	@Override
	public List<Dependency> getRequiredDependencies() {
	    List<Dependency> deps = new ArrayList<>();
	    Dependency dep = new Dependency();
	    dep.setGroupId(CAMEL_SAP_GROUP_ID);
	    dep.setArtifactId(CAMEL_SAP_ARTIFACT_ID);
	    dep.setVersion(CamelUtils.getCurrentProjectCamelVersion());
	    deps.add(dep);
	    return deps;
	}

}
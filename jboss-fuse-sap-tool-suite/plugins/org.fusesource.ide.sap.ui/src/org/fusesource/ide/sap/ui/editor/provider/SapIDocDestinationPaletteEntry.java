package org.fusesource.ide.sap.ui.editor.provider;

import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_ARTIFACT_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_GROUP_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_VERSION;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;


public class SapIDocDestinationPaletteEntry implements
		ICustomPaletteEntry {
	
	private static final String PROTOCOL = "sap-idoc-destination";

	public static final String COMPONENT_NAME = "SAP IDoc Destination"; //$NON-NLS-1$ 
	public static final String COMPONENT_DESCRIPTION = "Creates an SAP IDoc Destination endpoint..."; //$NON-NLS-1$
	public static final String COMPONENT_URL = PROTOCOL + ":destination:idocType:idocTypeExtension:systemRelease:applicationRelease"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		return new CreateEndpointFigureFeature(fp, COMPONENT_NAME, COMPONENT_DESCRIPTION, COMPONENT_URL, getRequiredDependencies()); 
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return PROTOCOL;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#providesProtocol(java.lang.String)
	 */
	@Override
	public boolean providesProtocol(String protocol) {
		return PROTOCOL.equalsIgnoreCase(protocol);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getRequiredDependencies()
	 */
	@Override
	public List<Dependency> getRequiredDependencies() {
		List<Dependency> deps = new ArrayList<Dependency>();
        Dependency dep = new Dependency();
        dep.setGroupId(CAMEL_SAP_GROUP_ID);
        dep.setArtifactId(CAMEL_SAP_ARTIFACT_ID);
        dep.setVersion(CAMEL_SAP_VERSION);
        deps.add(dep);
        return deps;
	}

}

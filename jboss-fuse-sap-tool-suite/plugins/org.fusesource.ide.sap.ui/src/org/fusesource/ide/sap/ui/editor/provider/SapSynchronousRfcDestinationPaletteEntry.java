package org.fusesource.ide.sap.ui.editor.provider;

import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_ARTIFACT_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_GROUP_ID;
import static org.fusesource.ide.sap.ui.Activator.CAMEL_SAP_VERSION;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider.CATEGORY_TYPE;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.catalog.Dependency;

public class SapSynchronousRfcDestinationPaletteEntry implements
		ICustomPaletteEntry {
	
	public static final String COMPONENT_NAME = "SAP sRFC Destination"; //$NON-NLS-1$ 
	public static final String COMPONENT_DESCRIPTION = "Creates an SAP Synchronous RFC Destination endpoint..."; //$NON-NLS-1$
	public static final String COMPONENT_URL = "sap-srfc-destination:<destination>:<rfc>"; //$NON-NLS-1$
	
	@Override
	public String getPaletteCategory() {
		return CATEGORY_TYPE.COMPONENTS.name();
	}

	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		return new CreateEndpointFigureFeature(fp, COMPONENT_NAME, COMPONENT_DESCRIPTION, new Endpoint(COMPONENT_URL)); 
	}

	@Override
	public IImageDecorator getImageDecorator(Object object) {
		return null;
	}

	@Override
	public String getTypeName() {
		return COMPONENT_NAME;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(Class type) {
		return false;
	}

	@Override
	public List<Dependency> getRequiredCapabilities(Object object) {
        List<Dependency> deps = new ArrayList<Dependency>();
        Dependency dep = new Dependency();
        dep.setGroupId(CAMEL_SAP_GROUP_ID);
        dep.setArtifactId(CAMEL_SAP_ARTIFACT_ID);
        dep.setVersion(CAMEL_SAP_VERSION);
        deps.add(dep);
        return deps;
	}

}

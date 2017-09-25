package org.jboss.tools.fuse.sap.reddeer.requirement;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.reddeer.junit.requirement.ConfigurableRequirement;
import org.jboss.tools.fuse.sap.reddeer.requirement.SAPRequirement.SAP;

public class SAPRequirement implements ConfigurableRequirement<SAPConfiguration, SAP> {

	private SAP annotation;
	private SAPConfiguration configuration;

	@Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
	@Documented
	public @interface SAP {

	}

	@Override
	public void fulfill() {

	}

	@Override
	public void setDeclaration(SAP annotation) {
		this.annotation = annotation;
	}

	@Override
	public SAP getDeclaration() {
		return annotation;
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public Class<SAPConfiguration> getConfigurationClass() {
		return SAPConfiguration.class;
	}

	@Override
	public void setConfiguration(SAPConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public SAPConfiguration getConfiguration() {
		return configuration;
	}

}

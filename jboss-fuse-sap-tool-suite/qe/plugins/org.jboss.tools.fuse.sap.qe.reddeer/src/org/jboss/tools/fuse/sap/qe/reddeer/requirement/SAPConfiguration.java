package org.jboss.tools.fuse.sap.qe.reddeer.requirement;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

public class SAPConfiguration implements RequirementConfiguration {

	private SAPDestination destination;
	private SAPServer server;

	@Override
	public String getId() {
		return "SAP";
	}

	public SAPDestination getDestination() {
		return destination;
	}

	public void setDestination(SAPDestination destination) {
		this.destination = destination;
	}

	public SAPServer getServer() {
		return server;
	}

	public void setServer(SAPServer server) {
		this.server = server;
	}

}

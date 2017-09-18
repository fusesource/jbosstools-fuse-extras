package org.jboss.tools.fuse.sap.qe.reddeer.requirement;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

public class SAPServer implements RequirementConfiguration {

	private String name;
	private String destination;
	private String gwhost;
	private String gwport;
	private String progid;
	private String connectionCount;

	@Override
	public String getId() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getGwhost() {
		return gwhost;
	}

	public void setGwhost(String gwhost) {
		this.gwhost = gwhost;
	}

	public String getGwport() {
		return gwport;
	}

	public void setGwport(String gwport) {
		this.gwport = gwport;
	}

	public String getProgid() {
		return progid;
	}

	public void setProgid(String progid) {
		this.progid = progid;
	}

	public String getConnectionCount() {
		return connectionCount;
	}

	public void setConnectionCount(String connectionCount) {
		this.connectionCount = connectionCount;
	}

}

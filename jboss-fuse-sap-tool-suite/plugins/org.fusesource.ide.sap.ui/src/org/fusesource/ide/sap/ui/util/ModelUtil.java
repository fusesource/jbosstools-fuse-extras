/*******************************************************************************
* Copyright (c) 2016 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/
package org.fusesource.ide.sap.ui.util;

import static org.fusesource.ide.sap.ui.util.XMLUtils.getAttributeValue;
import static org.fusesource.ide.sap.ui.util.XMLUtils.getFirstChildElementWithName;
import static org.fusesource.ide.sap.ui.util.XMLUtils.getNextSiblingElementWithName;
import static org.fusesource.ide.sap.ui.util.XMLUtils.hasAttributeValue;
import static org.fusesource.ide.sap.ui.util.XMLUtils.removeChildNodes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.fusesource.camel.component.sap.model.SAPEditPlugin;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelUtil {

	public static class EmptyNodeList implements NodeList {
		@Override
		public Node item(int index) {
			return null;
		}

		@Override
		public int getLength() {
			return 0;
		}
	}

	private static final String ID_ATTRIBUTE = "id";
	private static final String VALUE_ATTRIBUTE = "value";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String SAP_CONNECTION_CONFIGURATION_CLASS = "org.fusesource.camel.component.sap.SapConnectionConfiguration";
	private static final String PROPERTY_TAG = "property";
	private static final String BEAN_TAG = "bean";
	private static final String DESTINATION_DATA_STORE_NAME_ATTRIBUTE = "destinationDataStore";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String ENTRY_TAG = "entry";

	//
	// Model Conversion
	//

	public static void extractDestinationData(DestinationData destinationDataModel, Element destinationDataConfig) {
		if (destinationDataModel == null || destinationDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(destinationDataConfig, "value-ref");
		if (beanName != null) {
			destinationDataConfig = getGlobalConfigurationById(destinationDataConfig.getOwnerDocument(), beanName);
		}

		Map<String, String> properties = getPropertyValues(destinationDataConfig);
		for (String property : properties.keySet()) {
			setDestinationDataProperty(destinationDataModel, property, properties.get(property));
		}
	}
	
	public static void populateDestinationData(DestinationData destinationDataModel, Element destinationDataConfig) {
		if (destinationDataModel == null || destinationDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(destinationDataConfig, "value-ref");
		if (beanName != null) {
			destinationDataConfig = getGlobalConfigurationById(destinationDataConfig.getOwnerDocument(), beanName);
		}

		Map<String,String> destinationDataProperties = extractDestinationDataProperties(destinationDataModel);
		
		setPropertyValues(destinationDataConfig, destinationDataProperties);
	}

	public static void extractServerData(ServerData serverDataModel, Element serverDataConfig) {
		if (serverDataModel == null || serverDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(serverDataConfig, "value-ref");
		if (beanName != null) {
			serverDataConfig = getGlobalConfigurationById(serverDataConfig.getOwnerDocument(), beanName);
		}

		Map<String, String> properties = getPropertyValues(serverDataConfig);
		for (String property : properties.keySet()) {
			setServerDataProperty(serverDataModel, property, properties.get(property));
		}
	}
	
	public static void populateServerData(ServerData serverDataModel, Element serverDataConfig) {
		if (serverDataModel == null || serverDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(serverDataConfig, "value-ref");
		if (beanName != null) {
			serverDataConfig = getGlobalConfigurationById(serverDataConfig.getOwnerDocument(), beanName);
		}

		Map<String,String> serverDataProperties = extractServerDataProperties(serverDataModel);
		
		setPropertyValues(serverDataConfig, serverDataProperties);
	}

	public static Map<String, String> extractServerDataProperties(ServerData serverDataModel) {
		Map<String, String> serverDataProperties = new HashMap<String, String>();
		serverDataProperties.put("connectionCount", serverDataModel.getConnectionCount());
		serverDataProperties.put("gwhost", serverDataModel.getGwhost());
		serverDataProperties.put("gwserv", serverDataModel.getGwserv());
		serverDataProperties.put("maxStartUpDelay", serverDataModel.getMaxStartUpDelay());
		serverDataProperties.put("progid", serverDataModel.getProgid());
		serverDataProperties.put("repositoryDestination", serverDataModel.getRepositoryDestination());
		serverDataProperties.put("repositoryMap", serverDataModel.getRepositoryMap());
		serverDataProperties.put("saprouter", serverDataModel.getSaprouter());
		serverDataProperties.put("sncLib", serverDataModel.getSncLib());
		serverDataProperties.put("sncMode", serverDataModel.getSncMode());
		serverDataProperties.put("sncMyname", serverDataModel.getSncMyname());
		serverDataProperties.put("sncQop", serverDataModel.getSncQop());
		serverDataProperties.put("trace", serverDataModel.getTrace());
		serverDataProperties.put("workerThreadCount", serverDataModel.getWorkerThreadCount());
		serverDataProperties.put("workerThreadMinCount", serverDataModel.getWorkerThreadMinCount());
		return serverDataProperties;
	}

	public static void setServerDataProperty(ServerData serverData, String attributeName,
			String attributeValue) {
		switch (attributeName) {
		case "gwhost":
			serverData.setGwhost(attributeValue);
			break;
		case "gwserv":
			serverData.setGwserv(attributeValue);
			break;
		case "progid":
			serverData.setProgid(attributeValue);
			break;
		case "connectionCount":
			serverData.setConnectionCount(attributeValue);
			break;
		case "saprouter":
			serverData.setSaprouter(attributeValue);
			break;
		case "maxStartUpDelay":
			serverData.setMaxStartUpDelay(attributeValue);
			break;
		case "repositoryDestination":
			serverData.setRepositoryDestination(attributeValue);
			break;
		case "repositoryMap":
			serverData.setRepositoryMap(attributeValue);
			break;
		case "trace":
			serverData.setTrace(attributeValue);
			break;
		case "workerThreadCount":
			serverData.setWorkerThreadCount(attributeValue);
			break;
		case "workerThreadMinCount":
			serverData.setWorkerThreadMinCount(attributeValue);
			break;
		case "sncMode":
			serverData.setSncMode(attributeValue);
			break;
		case "sncQop":
			serverData.setSncQop(attributeValue);
			break;
		case "sncMyname":
			serverData.setSncMyname(attributeValue);
			break;
		case "sncLib":
			serverData.setSncLib(attributeValue);
			break;
		default:
			// NOOP
		}
	}
	
	public static Map<String,String> extractDestinationDataProperties(DestinationData destinationDataModel) {
		Map<String,String> destinationDataProperties = new HashMap<String,String>();
		destinationDataProperties.put("aliasUser", destinationDataModel.getAliasUser());
		destinationDataProperties.put("ashost", destinationDataModel.getAshost());
		destinationDataProperties.put("authType", destinationDataModel.getAuthType());
		destinationDataProperties.put("client", destinationDataModel.getClient());
		destinationDataProperties.put("codepage", destinationDataModel.getCodepage());
		destinationDataProperties.put("cpicTrace", destinationDataModel.getCpicTrace());
		destinationDataProperties.put("denyInitialPassword", destinationDataModel.getDenyInitialPassword());
		destinationDataProperties.put("expirationPeriod", destinationDataModel.getExpirationPeriod());
		destinationDataProperties.put("expirationTime", destinationDataModel.getExpirationTime());
		destinationDataProperties.put("getsso2", destinationDataModel.getGetsso2());
		destinationDataProperties.put("group", destinationDataModel.getGroup());
		destinationDataProperties.put("gwhost", destinationDataModel.getGwhost());
		destinationDataProperties.put("gwserv", destinationDataModel.getGwserv());
		destinationDataProperties.put("lang", destinationDataModel.getLang());
		destinationDataProperties.put("lcheck", destinationDataModel.getLcheck());
		destinationDataProperties.put("maxGetTime", destinationDataModel.getMaxGetTime());
		destinationDataProperties.put("mshost", destinationDataModel.getMshost());
		destinationDataProperties.put("msserv", destinationDataModel.getMsserv());
		destinationDataProperties.put("mysapsso2", destinationDataModel.getMysapsso2());
		destinationDataProperties.put("password", destinationDataModel.getPassword());
		destinationDataProperties.put("passwd", destinationDataModel.getPasswd());
		destinationDataProperties.put("pcs", destinationDataModel.getPcs());
		destinationDataProperties.put("peakLimit", destinationDataModel.getPeakLimit());
		destinationDataProperties.put("pingOnCreate", destinationDataModel.getPingOnCreate());
		destinationDataProperties.put("poolCapacity", destinationDataModel.getPoolCapacity());
		destinationDataProperties.put("r3name", destinationDataModel.getR3name());
		destinationDataProperties.put("repositoryDest", destinationDataModel.getRepositoryDest());
		destinationDataProperties.put("repositoryPasswd", destinationDataModel.getRepositoryPasswd());
		destinationDataProperties.put("repositoryRoundtripOptimization", destinationDataModel.getRepositoryRoundtripOptimization());
		destinationDataProperties.put("repositorySnc", destinationDataModel.getRepositorySnc());
		destinationDataProperties.put("repositoryUser", destinationDataModel.getRepositoryUser());
		destinationDataProperties.put("saprouter", destinationDataModel.getSaprouter());
		destinationDataProperties.put("sncLibrary", destinationDataModel.getSncLibrary());
		destinationDataProperties.put("sncMode", destinationDataModel.getSncMode());
		destinationDataProperties.put("sncMyname", destinationDataModel.getSncMyname());
		destinationDataProperties.put("sncPartnername", destinationDataModel.getSncPartnername());
		destinationDataProperties.put("sncQop", destinationDataModel.getSncQop());
		destinationDataProperties.put("sysnr", destinationDataModel.getSysnr());
		destinationDataProperties.put("tphost", destinationDataModel.getTphost());
		destinationDataProperties.put("tpname", destinationDataModel.getTpname());
		destinationDataProperties.put("trace", destinationDataModel.getTrace());
		destinationDataProperties.put("type", destinationDataModel.getType());
		destinationDataProperties.put("userName", destinationDataModel.getUserName());
		destinationDataProperties.put("user", destinationDataModel.getUser());
		destinationDataProperties.put("userId", destinationDataModel.getUserId());
		destinationDataProperties.put("useSapgui", destinationDataModel.getUseSapgui());
		destinationDataProperties.put("x509cert", destinationDataModel.getX509cert());
		return destinationDataProperties;
	}

	public static void setDestinationDataProperty(DestinationData destinationData, String attributeName,
			String attributeValue) {
		switch (attributeName) {
		case "aliasValue":
			destinationData.setAliasUser(attributeValue);
			break;
		case "ashost":
			destinationData.setAshost(attributeValue);
			break;
		case "authType":
			destinationData.setAuthType(attributeValue);
			break;
		case "client":
			destinationData.setClient(attributeValue);
			break;
		case "codepage":
			destinationData.setCodepage(attributeValue);
			break;
		case "cpicTrace":
			destinationData.setCpicTrace(attributeValue);
			break;
		case "denyInitialPassword":
			destinationData.setDenyInitialPassword(attributeValue);
			break;
		case "expirationPeriod":
			destinationData.setExpirationPeriod(attributeValue);
			break;
		case "expirationTime":
			destinationData.setExpirationTime(attributeValue);
			break;
		case "getsso2":
			destinationData.setGetsso2(attributeValue);
			break;
		case "group":
			destinationData.setGroup(attributeValue);
			break;
		case "gwhost":
			destinationData.setGwhost(attributeValue);
			break;
		case "gwserv":
			destinationData.setGwserv(attributeValue);
			break;
		case "lang":
			destinationData.setLang(attributeValue);
			break;
		case "lcheck":
			destinationData.setLcheck(attributeValue);
			break;
		case "maxGetTime":
			destinationData.setMaxGetTime(attributeValue);
			break;
		case "mshost":
			destinationData.setMshost(attributeValue);
			break;
		case "msserv":
			destinationData.setMsserv(attributeValue);
			break;
		case "mysapsso2":
			destinationData.setMysapsso2(attributeValue);
			break;
		case "passwd":
			destinationData.setPasswd(attributeValue);
			break;
		case "password":
			destinationData.setPassword(attributeValue);
			break;
		case "pcs":
			destinationData.setPcs(attributeValue);
			break;
		case "peakLimit":
			destinationData.setPeakLimit(attributeValue);
			break;
		case "pingOnCreate":
			destinationData.setPingOnCreate(attributeValue);
			break;
		case "poolCapacity":
			destinationData.setPoolCapacity(attributeValue);
			break;
		case "r3name":
			destinationData.setR3name(attributeValue);
			break;
		case "repositoryDest":
			destinationData.setRepositoryDest(attributeValue);
			break;
		case "repositoryPasswd":
			destinationData.setRepositoryPasswd(attributeValue);
			break;
		case "repositoryRoundtripOptimization":
			destinationData.setRepositoryRoundtripOptimization(attributeValue);
			break;
		case "repositorySnc":
			destinationData.setRepositorySnc(attributeValue);
			break;
		case "repositoryUser":
			destinationData.setRepositoryUser(attributeValue);
			break;
		case "saprouter":
			destinationData.setSaprouter(attributeValue);
			break;
		case "sncLibrary":
			destinationData.setSncLibrary(attributeValue);
			break;
		case "sncMode":
			destinationData.setSncMode(attributeValue);
			break;
		case "sncMyname":
			destinationData.setSncMyname(attributeValue);
			break;
		case "sncPartnername":
			destinationData.setSncPartnername(attributeValue);
			break;
		case "sncQop":
			destinationData.setSncQop(attributeValue);
			break;
		case "sysnr":
			destinationData.setSysnr(attributeValue);
			break;
		case "tphost":
			destinationData.setTphost(attributeValue);
			break;
		case "tpname":
			destinationData.setTpname(attributeValue);
			break;
		case "trace":
			destinationData.setTrace(attributeValue);
			break;
		case "type":
			destinationData.setType(attributeValue);
			break;
		case "userName":
			destinationData.setUserName(attributeValue);
			break;
		case "user":
			destinationData.setUser(attributeValue);
			break;
		case "userId":
			destinationData.setUserId(attributeValue);
			break;
		case "useSapgui":
			destinationData.setUseSapgui(attributeValue);
			break;
		case "x509cert":
			destinationData.setX509cert(attributeValue);
			break;
		default:
			// NOOP
		}
	}

	//
	// Configuration Manipulation
	//

	public static SapConnectionConfiguration getModel(ResourceSet resourceSet) {
		SapConnectionConfiguration sapConnectionConfiguration = null;
		String path = SAPEditPlugin.getPlugin().getStateLocation().append("resource.spi").toOSString(); //$NON-NLS-1$
		URI resourceURI = URI.createFileURI(path);
		Resource resource;
		try {
			resource = resourceSet.getResource(resourceURI, true);
		} catch (Exception e) {
			resource = resourceSet.getResource(resourceURI, false);
		}

		// Get resource adapter model from resource or create and add to
		// resource.
		if (resource.getContents().isEmpty()) {
			sapConnectionConfiguration = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
			resource.getContents().add(sapConnectionConfiguration);
		} else {
			EObject root = resource.getContents().get(0);

			// Replace root if it is not a resource adapter model element.
			if (!(root instanceof org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration)) {
				root = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
				resource.getContents().set(0, root);
			}

			// Ensure single root in resource.
			if (resource.getContents().size() > 1) {
				resource.getContents().clear();
				resource.getContents().add(root);
			}
			sapConnectionConfiguration = (org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration) root;
		}

		try {
			resource.save(null);
		} catch (IOException e) {
			Activator.getLogger().warning(Messages.ModelUtil_ErrorWhenSavingSapConnectionConfiguration, e);
		}

		return sapConnectionConfiguration;
	}

	//
	// SAP Configuration Utilities
	//

	public static Element getSapConfiguration(Document document) {
		return getGlobalConfigurationByClass(document, SAP_CONNECTION_CONFIGURATION_CLASS);
	}

	public static Element getDestinationDataStore(Element sapConfiguration) {
		if (sapConfiguration == null) {
			return null;
		}

		for (Element e = getFirstChildElementWithName(sapConfiguration,
				PROPERTY_TAG); e != null; e = getNextSiblingElementWithName(e, PROPERTY_TAG)) {
			if (hasAttributeValue(e, NAME_ATTRIBUTE, DESTINATION_DATA_STORE_NAME_ATTRIBUTE)) {
				return e;
			}
		}

		return null;
	}

	public static NodeList getDestinationDataEntries(Element destinationDataStore) {
		if (destinationDataStore == null) {
			return new EmptyNodeList();
		}
		return destinationDataStore.getElementsByTagName(ENTRY_TAG);
	}

	public static Element getServerDataStore(Element sapConfiguration) {
		if (sapConfiguration == null) {
			return null;
		}

		for (Element e = getFirstChildElementWithName(sapConfiguration,
				PROPERTY_TAG); e != null; e = getNextSiblingElementWithName(e, PROPERTY_TAG)) {
			if (hasAttributeValue(e, NAME_ATTRIBUTE, "serverDataStore")) {
				return e;
			}
		}

		return null;
	}

	public static NodeList getServerDataEntries(Element destinationDataStore) {
		if (destinationDataStore == null) {
			return new EmptyNodeList();
		}
		return destinationDataStore.getElementsByTagName(ENTRY_TAG);
	}
	
	public static Element getGlobalConfigurationById(Document document, String beanName) {
		if (document == null || beanName == null) {
			return null;
		}

		Element documentElement = document.getDocumentElement();
		for (Element e = getFirstChildElementWithName(documentElement,
				BEAN_TAG); e != null; e = getNextSiblingElementWithName(e, BEAN_TAG)) {
			if (hasAttributeValue(e, ID_ATTRIBUTE, beanName)) {
				return e;
			}
		}
		return null;
	}

	public static Element getGlobalConfigurationByClass(Document document, String className) {
		if (document == null || className == null) {
			return null;
		}

		Element documentElement = document.getDocumentElement();
		for (Element e = getFirstChildElementWithName(documentElement,
				BEAN_TAG); e != null; e = getNextSiblingElementWithName(e, BEAN_TAG)) {
			if (hasAttributeValue(e, CLASS_ATTRIBUTE, className)) {
				return e;
			}
		}
		return null;
	}

	public static Map<String, String> getPropertyValues(Element configElement) {
		Map<String, String> properties = new HashMap<String, String>();

		if (configElement == null) {
			return properties;
		}

		for (Element e = XMLUtils.getFirstChildElementWithName(configElement,
				PROPERTY_TAG); e != null; e = getNextSiblingElementWithName(e, PROPERTY_TAG)) {
			String attributeName = getAttributeValue(e, NAME_ATTRIBUTE);
			String attributeValue = getAttributeValue(e, VALUE_ATTRIBUTE);
			if (attributeName != null && attributeValue != null) {
				properties.put(attributeName, attributeValue);
			}
		}
		return properties;
	}

	public static void setPropertyValues(Element configElement, Map<String, String> properties) {
		Document document = configElement.getOwnerDocument();
		removeChildNodes(configElement);
		for (String propertyName: properties.keySet()) {
			Element property = document.createElement(PROPERTY_TAG);
			property.setAttribute(NAME_ATTRIBUTE, propertyName);
			property.setAttribute(VALUE_ATTRIBUTE, properties.get(propertyName));
			configElement.appendChild(property);
		}
	}
}

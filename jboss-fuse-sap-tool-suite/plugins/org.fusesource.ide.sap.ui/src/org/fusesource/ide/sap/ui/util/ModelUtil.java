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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.fusesource.camel.component.sap.model.SAPEditPlugin;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.util.Util;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities to convert SAP Global Connection Configuration and SAP Connection Configuration Model. 
 * 
 * @author William Collins <punkhornsw@gmail.com>
 *
 */
public class ModelUtil {

	private static final String SERVER_DATA_STORE_NAME_ATTRIBUTE = "serverDataStore"; //$NON-NLS-1$
	private static final String MAP_TAG = "map"; //$NON-NLS-1$
	private static final String SAP_CONNECTION_CONFIGURATION_ID = "sap-configuration"; //$NON-NLS-1$
	private static final String KEY_ATTRIBUTE = "key"; //$NON-NLS-1$

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
	
	private static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$
	private static final String VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
	private static final String SAP_CONNECTION_CONFIGURATION_CLASS = "org.fusesource.camel.component.sap.SapConnectionConfiguration"; //$NON-NLS-1$
	private static final String DESTINATION_DATA_CLASS = "org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataImpl"; //$NON-NLS-1$
	private static final String SERVER_DATA_CLASS = "org.fusesource.camel.component.sap.model.rfc.impl.ServerDataImpl"; //$NON-NLS-1$
	private static final String PROPERTY_TAG = "property"; //$NON-NLS-1$
	private static final String BEAN_TAG = "bean"; //$NON-NLS-1$
	private static final String DESTINATION_DATA_STORE_NAME_ATTRIBUTE = "destinationDataStore"; //$NON-NLS-1$
	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private static final String ENTRY_TAG = "entry"; //$NON-NLS-1$

	//
	// Model Conversion
	//
	
	private static Resource getResource() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		File tmpFile = File.createTempFile("resource", ".spi"); //$NON-NLS-1$ //$NON-NLS-2$
		URI resourceURI = URI.createFileURI(tmpFile.getAbsolutePath());
		Resource resource;
		try {
			resource = resourceSet.getResource(resourceURI, true);
		} catch (Exception e) {
			resource = resourceSet.getResource(resourceURI, false);
		}
		return resource;
	}

	public static SapConnectionConfiguration getSapConnectionConfigurationModelFromDocument(Document document) {
		if (document == null) {
			return null;
		}
		
		Resource resource = null;
		try {
			resource = getResource();
		} catch (IOException e) {
			return null;
		}
		
		Element sapConfiguration = getSapConfiguration(document);
		Element destinationDataStore = getDestinationDataStore(sapConfiguration);
		NodeList destinationDataEntries = getDestinationDataEntries(destinationDataStore);
		Element serverDataStore = getServerDataStore(sapConfiguration);
		NodeList serverDataEntries = getServerDataEntries(serverDataStore);
		
		
		SapConnectionConfiguration sapConnectionConfigurationModel = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
		resource.getContents().add(sapConnectionConfigurationModel);
		DestinationDataStore destinationDataStoreModel = sapConnectionConfigurationModel.getDestinationDataStore();
		for (int i = 0; i < destinationDataEntries.getLength(); i++) {
			Element destinationData = (Element) destinationDataEntries.item(i);
			String key = getAttributeValue(destinationData, KEY_ATTRIBUTE);
			if (key == null) {
				continue;
			}
			DestinationData destinationDataModel = RfcFactory.eINSTANCE.createDestinationData();
			populateDestinationDataConfigIntoModel(destinationDataModel, destinationData);
			destinationDataStoreModel.getDestinationData().add(destinationDataModel);
			destinationDataStoreModel.getEntries().put(key, destinationDataModel);
		}
		
		ServerDataStore serverDataStoreModel = sapConnectionConfigurationModel.getServerDataStore();
		for (int i = 0; i < serverDataEntries.getLength(); i++) {
			Element serverData = (Element) serverDataEntries.item(i);
			String key = getAttributeValue(serverData, KEY_ATTRIBUTE);
			if (key == null) {
				continue;
			}
			ServerData serverDataModel = RfcFactory.eINSTANCE.createServerData();
			populateServerDataConfigIntoModel(serverDataModel, serverData);
			serverDataStoreModel.getServerData().add(serverDataModel);
			serverDataStoreModel.getEntries().put(key, serverDataModel);
		}		
		
		return sapConnectionConfigurationModel;
	}
	
	public static void setSapConnectionConfigurationModelIntoDocument(Document document, SapConnectionConfiguration sapConnectionConfigurationModel) {
		if (document == null || sapConnectionConfigurationModel == null) {
			return;
		}
		
		
		Element sapConfigurationConfig = getSapConfiguration(document);
		Element destinationDataStoreConfig = getDestinationDataStore(sapConfigurationConfig);
		Element serverDataStoreConfig = getServerDataStore(sapConfigurationConfig);
		
		DestinationDataStore destinationDataStoreModel = sapConnectionConfigurationModel.getDestinationDataStore();
		populateDestinationDataStoreModelIntoConfig(destinationDataStoreModel, destinationDataStoreConfig);
		
		ServerDataStore serverDataStoreModel = sapConnectionConfigurationModel.getServerDataStore();
		populateServerDataStoreModelIntoConfig(serverDataStoreModel, serverDataStoreConfig);
	}
	
	public static void populateDestinationDataStoreModelIntoConfig(DestinationDataStore destinationDataStoreModel, Element destinationDataStoreConfig) {
		if (destinationDataStoreModel == null || destinationDataStoreConfig == null) {
			return;
		}
		
		Element map = getFirstChildElementWithName(destinationDataStoreConfig, MAP_TAG);
		if (map == null) {
			map = destinationDataStoreConfig.getOwnerDocument().createElement(MAP_TAG);
			destinationDataStoreConfig.appendChild(map);
		}
		
		removeChildNodes(map);
		for (Map.Entry<String,DestinationData> entry: destinationDataStoreModel.getEntries()) {
			Element entryConfig = destinationDataStoreConfig.getOwnerDocument().createElement(ENTRY_TAG);
			entryConfig.setAttribute(KEY_ATTRIBUTE, entry.getKey());
			entryConfig.setAttribute(CLASS_ATTRIBUTE, DESTINATION_DATA_CLASS);
			map.appendChild(entryConfig);
			populateDestinationDataModelIntoConfig(entry.getValue(), entryConfig);
		}
	}
	
	public static void populateDestinationDataConfigIntoModel(DestinationData destinationDataModel, Element destinationDataConfig) {
		if (destinationDataModel == null || destinationDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(destinationDataConfig, "value-ref"); //$NON-NLS-1$
		if (beanName != null) {
			destinationDataConfig = getGlobalConfigurationById(destinationDataConfig.getOwnerDocument(), beanName);
		}

		Map<String, String> properties = getPropertyValues(destinationDataConfig);
		for (String property : properties.keySet()) {
			setDestinationDataProperty(destinationDataModel, property, properties.get(property));
		}
	}
	
	public static void populateDestinationDataModelIntoConfig(DestinationData destinationDataModel, Element destinationDataConfig) {
		if (destinationDataModel == null || destinationDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(destinationDataConfig, "value-ref"); //$NON-NLS-1$
		if (beanName != null) {
			destinationDataConfig = getGlobalConfigurationById(destinationDataConfig.getOwnerDocument(), beanName);
		}

		Map<String,String> destinationDataProperties = extractDestinationDataProperties(destinationDataModel);
		
		setPropertyValues(destinationDataConfig, destinationDataProperties);
	}

	public static void populateServerDataStoreModelIntoConfig(ServerDataStore serverDataStoreModel, Element serverDataStoreConfig) {
		if (serverDataStoreModel == null || serverDataStoreConfig == null) {
			return;
		}
		
		Element map = getFirstChildElementWithName(serverDataStoreConfig, MAP_TAG);
		if (map == null) {
			map = serverDataStoreConfig.getOwnerDocument().createElement(MAP_TAG);
			serverDataStoreConfig.appendChild(map);
		}
		
		removeChildNodes(map);
		for (Map.Entry<String,ServerData> entry: serverDataStoreModel.getEntries()) {
			Element entryConfig = serverDataStoreConfig.getOwnerDocument().createElement(ENTRY_TAG);
			entryConfig.setAttribute(KEY_ATTRIBUTE, entry.getKey());
			entryConfig.setAttribute(CLASS_ATTRIBUTE, SERVER_DATA_CLASS);
			map.appendChild(entryConfig);
			populateServerDataModelIntoConfig(entry.getValue(), entryConfig);
		}
	}
	
	public static void populateServerDataConfigIntoModel(ServerData serverDataModel, Element serverDataConfig) {
		if (serverDataModel == null || serverDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(serverDataConfig, "value-ref"); //$NON-NLS-1$
		if (beanName != null) {
			serverDataConfig = getGlobalConfigurationById(serverDataConfig.getOwnerDocument(), beanName);
		}

		Map<String, String> properties = getPropertyValues(serverDataConfig);
		for (String property : properties.keySet()) {
			setServerDataProperty(serverDataModel, property, properties.get(property));
		}
	}
	
	public static void populateServerDataModelIntoConfig(ServerData serverDataModel, Element serverDataConfig) {
		if (serverDataModel == null || serverDataConfig == null) {
			return;
		}
		
		// Check if configuration entry is a reference to global bean config
		String beanName = getAttributeValue(serverDataConfig, "value-ref"); //$NON-NLS-1$
		if (beanName != null) {
			serverDataConfig = getGlobalConfigurationById(serverDataConfig.getOwnerDocument(), beanName);
		}

		Map<String,String> serverDataProperties = extractServerDataProperties(serverDataModel);
		
		setPropertyValues(serverDataConfig, serverDataProperties);
	}

	public static Map<String, String> extractServerDataProperties(ServerData serverDataModel) {
		Map<String, String> serverDataProperties = new HashMap<String, String>();
		serverDataProperties.put("connectionCount", serverDataModel.getConnectionCount()); //$NON-NLS-1$
		serverDataProperties.put("gwhost", serverDataModel.getGwhost()); //$NON-NLS-1$
		serverDataProperties.put("gwserv", serverDataModel.getGwserv()); //$NON-NLS-1$
		serverDataProperties.put("maxStartUpDelay", serverDataModel.getMaxStartUpDelay()); //$NON-NLS-1$
		serverDataProperties.put("progid", serverDataModel.getProgid()); //$NON-NLS-1$
		serverDataProperties.put("repositoryDestination", serverDataModel.getRepositoryDestination()); //$NON-NLS-1$
		serverDataProperties.put("repositoryMap", serverDataModel.getRepositoryMap()); //$NON-NLS-1$
		serverDataProperties.put("saprouter", serverDataModel.getSaprouter()); //$NON-NLS-1$
		serverDataProperties.put("sncLib", serverDataModel.getSncLib()); //$NON-NLS-1$
		serverDataProperties.put("sncMode", serverDataModel.getSncMode()); //$NON-NLS-1$
		serverDataProperties.put("sncMyname", serverDataModel.getSncMyname()); //$NON-NLS-1$
		serverDataProperties.put("sncQop", serverDataModel.getSncQop()); //$NON-NLS-1$
		serverDataProperties.put("trace", serverDataModel.getTrace()); //$NON-NLS-1$
		serverDataProperties.put("workerThreadCount", serverDataModel.getWorkerThreadCount()); //$NON-NLS-1$
		serverDataProperties.put("workerThreadMinCount", serverDataModel.getWorkerThreadMinCount()); //$NON-NLS-1$
		return serverDataProperties;
	}

	public static void setServerDataProperty(ServerData serverData, String attributeName,
			String attributeValue) {
		switch (attributeName) {
		case "gwhost": //$NON-NLS-1$
			serverData.setGwhost(attributeValue);
			break;
		case "gwserv": //$NON-NLS-1$
			serverData.setGwserv(attributeValue);
			break;
		case "progid": //$NON-NLS-1$
			serverData.setProgid(attributeValue);
			break;
		case "connectionCount": //$NON-NLS-1$
			serverData.setConnectionCount(attributeValue);
			break;
		case "saprouter": //$NON-NLS-1$
			serverData.setSaprouter(attributeValue);
			break;
		case "maxStartUpDelay": //$NON-NLS-1$
			serverData.setMaxStartUpDelay(attributeValue);
			break;
		case "repositoryDestination": //$NON-NLS-1$
			serverData.setRepositoryDestination(attributeValue);
			break;
		case "repositoryMap": //$NON-NLS-1$
			serverData.setRepositoryMap(attributeValue);
			break;
		case "trace": //$NON-NLS-1$
			serverData.setTrace(attributeValue);
			break;
		case "workerThreadCount": //$NON-NLS-1$
			serverData.setWorkerThreadCount(attributeValue);
			break;
		case "workerThreadMinCount": //$NON-NLS-1$
			serverData.setWorkerThreadMinCount(attributeValue);
			break;
		case "sncMode": //$NON-NLS-1$
			serverData.setSncMode(attributeValue);
			break;
		case "sncQop": //$NON-NLS-1$
			serverData.setSncQop(attributeValue);
			break;
		case "sncMyname": //$NON-NLS-1$
			serverData.setSncMyname(attributeValue);
			break;
		case "sncLib": //$NON-NLS-1$
			serverData.setSncLib(attributeValue);
			break;
		default:
			// NOOP
		}
	}
	
	public static Map<String,String> extractDestinationDataProperties(DestinationData destinationDataModel) {
		Map<String,String> destinationDataProperties = new HashMap<String,String>();
		destinationDataProperties.put("aliasUser", destinationDataModel.getAliasUser()); //$NON-NLS-1$
		destinationDataProperties.put("ashost", destinationDataModel.getAshost()); //$NON-NLS-1$
		destinationDataProperties.put("authType", destinationDataModel.getAuthType()); //$NON-NLS-1$
		destinationDataProperties.put("client", destinationDataModel.getClient()); //$NON-NLS-1$
		destinationDataProperties.put("codepage", destinationDataModel.getCodepage()); //$NON-NLS-1$
		destinationDataProperties.put("cpicTrace", destinationDataModel.getCpicTrace()); //$NON-NLS-1$
		destinationDataProperties.put("denyInitialPassword", destinationDataModel.getDenyInitialPassword()); //$NON-NLS-1$
		destinationDataProperties.put("expirationPeriod", destinationDataModel.getExpirationPeriod()); //$NON-NLS-1$
		destinationDataProperties.put("expirationTime", destinationDataModel.getExpirationTime()); //$NON-NLS-1$
		destinationDataProperties.put("getsso2", destinationDataModel.getGetsso2()); //$NON-NLS-1$
		destinationDataProperties.put("group", destinationDataModel.getGroup()); //$NON-NLS-1$
		destinationDataProperties.put("gwhost", destinationDataModel.getGwhost()); //$NON-NLS-1$
		destinationDataProperties.put("gwserv", destinationDataModel.getGwserv()); //$NON-NLS-1$
		destinationDataProperties.put("lang", destinationDataModel.getLang()); //$NON-NLS-1$
		destinationDataProperties.put("lcheck", destinationDataModel.getLcheck()); //$NON-NLS-1$
		destinationDataProperties.put("maxGetTime", destinationDataModel.getMaxGetTime()); //$NON-NLS-1$
		destinationDataProperties.put("mshost", destinationDataModel.getMshost()); //$NON-NLS-1$
		destinationDataProperties.put("msserv", destinationDataModel.getMsserv()); //$NON-NLS-1$
		destinationDataProperties.put("mysapsso2", destinationDataModel.getMysapsso2()); //$NON-NLS-1$
		destinationDataProperties.put("password", destinationDataModel.getPassword()); //$NON-NLS-1$
		destinationDataProperties.put("passwd", destinationDataModel.getPasswd()); //$NON-NLS-1$
		destinationDataProperties.put("pcs", destinationDataModel.getPcs()); //$NON-NLS-1$
		destinationDataProperties.put("peakLimit", destinationDataModel.getPeakLimit()); //$NON-NLS-1$
		destinationDataProperties.put("pingOnCreate", destinationDataModel.getPingOnCreate()); //$NON-NLS-1$
		destinationDataProperties.put("poolCapacity", destinationDataModel.getPoolCapacity()); //$NON-NLS-1$
		destinationDataProperties.put("r3name", destinationDataModel.getR3name()); //$NON-NLS-1$
		destinationDataProperties.put("repositoryDest", destinationDataModel.getRepositoryDest()); //$NON-NLS-1$
		destinationDataProperties.put("repositoryPasswd", destinationDataModel.getRepositoryPasswd()); //$NON-NLS-1$
		destinationDataProperties.put("repositoryRoundtripOptimization", destinationDataModel.getRepositoryRoundtripOptimization()); //$NON-NLS-1$
		destinationDataProperties.put("repositorySnc", destinationDataModel.getRepositorySnc()); //$NON-NLS-1$
		destinationDataProperties.put("repositoryUser", destinationDataModel.getRepositoryUser()); //$NON-NLS-1$
		destinationDataProperties.put("saprouter", destinationDataModel.getSaprouter()); //$NON-NLS-1$
		destinationDataProperties.put("sncLibrary", destinationDataModel.getSncLibrary()); //$NON-NLS-1$
		destinationDataProperties.put("sncMode", destinationDataModel.getSncMode()); //$NON-NLS-1$
		destinationDataProperties.put("sncMyname", destinationDataModel.getSncMyname()); //$NON-NLS-1$
		destinationDataProperties.put("sncPartnername", destinationDataModel.getSncPartnername()); //$NON-NLS-1$
		destinationDataProperties.put("sncQop", destinationDataModel.getSncQop()); //$NON-NLS-1$
		destinationDataProperties.put("sysnr", destinationDataModel.getSysnr()); //$NON-NLS-1$
		destinationDataProperties.put("tphost", destinationDataModel.getTphost()); //$NON-NLS-1$
		destinationDataProperties.put("tpname", destinationDataModel.getTpname()); //$NON-NLS-1$
		destinationDataProperties.put("trace", destinationDataModel.getTrace()); //$NON-NLS-1$
		destinationDataProperties.put("type", destinationDataModel.getType()); //$NON-NLS-1$
		destinationDataProperties.put("userName", destinationDataModel.getUserName()); //$NON-NLS-1$
		destinationDataProperties.put("user", destinationDataModel.getUser()); //$NON-NLS-1$
		destinationDataProperties.put("userId", destinationDataModel.getUserId()); //$NON-NLS-1$
		destinationDataProperties.put("useSapgui", destinationDataModel.getUseSapgui()); //$NON-NLS-1$
		destinationDataProperties.put("x509cert", destinationDataModel.getX509cert()); //$NON-NLS-1$
		return destinationDataProperties;
	}

	public static void setDestinationDataProperty(DestinationData destinationData, String attributeName,
			String attributeValue) {
		switch (attributeName) {
		case "aliasValue": //$NON-NLS-1$
			destinationData.setAliasUser(attributeValue);
			break;
		case "ashost": //$NON-NLS-1$
			destinationData.setAshost(attributeValue);
			break;
		case "authType": //$NON-NLS-1$
			destinationData.setAuthType(attributeValue);
			break;
		case "client": //$NON-NLS-1$
			destinationData.setClient(attributeValue);
			break;
		case "codepage": //$NON-NLS-1$
			destinationData.setCodepage(attributeValue);
			break;
		case "cpicTrace": //$NON-NLS-1$
			destinationData.setCpicTrace(attributeValue);
			break;
		case "denyInitialPassword": //$NON-NLS-1$
			destinationData.setDenyInitialPassword(attributeValue);
			break;
		case "expirationPeriod": //$NON-NLS-1$
			destinationData.setExpirationPeriod(attributeValue);
			break;
		case "expirationTime": //$NON-NLS-1$
			destinationData.setExpirationTime(attributeValue);
			break;
		case "getsso2": //$NON-NLS-1$
			destinationData.setGetsso2(attributeValue);
			break;
		case "group": //$NON-NLS-1$
			destinationData.setGroup(attributeValue);
			break;
		case "gwhost": //$NON-NLS-1$
			destinationData.setGwhost(attributeValue);
			break;
		case "gwserv": //$NON-NLS-1$
			destinationData.setGwserv(attributeValue);
			break;
		case "lang": //$NON-NLS-1$
			destinationData.setLang(attributeValue);
			break;
		case "lcheck": //$NON-NLS-1$
			destinationData.setLcheck(attributeValue);
			break;
		case "maxGetTime": //$NON-NLS-1$
			destinationData.setMaxGetTime(attributeValue);
			break;
		case "mshost": //$NON-NLS-1$
			destinationData.setMshost(attributeValue);
			break;
		case "msserv": //$NON-NLS-1$
			destinationData.setMsserv(attributeValue);
			break;
		case "mysapsso2": //$NON-NLS-1$
			destinationData.setMysapsso2(attributeValue);
			break;
		case "passwd": //$NON-NLS-1$
			destinationData.setPasswd(attributeValue);
			break;
		case "password": //$NON-NLS-1$
			destinationData.setPassword(attributeValue);
			break;
		case "pcs": //$NON-NLS-1$
			destinationData.setPcs(attributeValue);
			break;
		case "peakLimit": //$NON-NLS-1$
			destinationData.setPeakLimit(attributeValue);
			break;
		case "pingOnCreate": //$NON-NLS-1$
			destinationData.setPingOnCreate(attributeValue);
			break;
		case "poolCapacity": //$NON-NLS-1$
			destinationData.setPoolCapacity(attributeValue);
			break;
		case "r3name": //$NON-NLS-1$
			destinationData.setR3name(attributeValue);
			break;
		case "repositoryDest": //$NON-NLS-1$
			destinationData.setRepositoryDest(attributeValue);
			break;
		case "repositoryPasswd": //$NON-NLS-1$
			destinationData.setRepositoryPasswd(attributeValue);
			break;
		case "repositoryRoundtripOptimization": //$NON-NLS-1$
			destinationData.setRepositoryRoundtripOptimization(attributeValue);
			break;
		case "repositorySnc": //$NON-NLS-1$
			destinationData.setRepositorySnc(attributeValue);
			break;
		case "repositoryUser": //$NON-NLS-1$
			destinationData.setRepositoryUser(attributeValue);
			break;
		case "saprouter": //$NON-NLS-1$
			destinationData.setSaprouter(attributeValue);
			break;
		case "sncLibrary": //$NON-NLS-1$
			destinationData.setSncLibrary(attributeValue);
			break;
		case "sncMode": //$NON-NLS-1$
			destinationData.setSncMode(attributeValue);
			break;
		case "sncMyname": //$NON-NLS-1$
			destinationData.setSncMyname(attributeValue);
			break;
		case "sncPartnername": //$NON-NLS-1$
			destinationData.setSncPartnername(attributeValue);
			break;
		case "sncQop": //$NON-NLS-1$
			destinationData.setSncQop(attributeValue);
			break;
		case "sysnr": //$NON-NLS-1$
			destinationData.setSysnr(attributeValue);
			break;
		case "tphost": //$NON-NLS-1$
			destinationData.setTphost(attributeValue);
			break;
		case "tpname": //$NON-NLS-1$
			destinationData.setTpname(attributeValue);
			break;
		case "trace": //$NON-NLS-1$
			destinationData.setTrace(attributeValue);
			break;
		case "type": //$NON-NLS-1$
			destinationData.setType(attributeValue);
			break;
		case "userName": //$NON-NLS-1$
			destinationData.setUserName(attributeValue);
			break;
		case "user": //$NON-NLS-1$
			destinationData.setUser(attributeValue);
			break;
		case "userId": //$NON-NLS-1$
			destinationData.setUserId(attributeValue);
			break;
		case "useSapgui": //$NON-NLS-1$
			destinationData.setUseSapgui(attributeValue);
			break;
		case "x509cert": //$NON-NLS-1$
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
			Activator.logWarning(Messages.ModelUtil_ErrorWhenSavingSapConnectionConfiguration, e);
		}

		return sapConnectionConfiguration;
	}

	//
	// SAP Configuration Utilities
	//

	public static Element getSapConfiguration(Document document) {
		
		if (document == null) {
			return null;
		}
		
		Element sapConfigurationElement = getGlobalConfigurationByClass(document, SAP_CONNECTION_CONFIGURATION_CLASS);
		
		if (sapConfigurationElement == null) {
			sapConfigurationElement = document.createElement(BEAN_TAG);
			sapConfigurationElement.setAttribute(ID_ATTRIBUTE, SAP_CONNECTION_CONFIGURATION_ID);
			sapConfigurationElement.setAttribute(CLASS_ATTRIBUTE, SAP_CONNECTION_CONFIGURATION_CLASS);
			document.getDocumentElement().appendChild(sapConfigurationElement);
		}
		
		return sapConfigurationElement;
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

		Element destinationDataStore = sapConfiguration.getOwnerDocument().createElement(PROPERTY_TAG);
		destinationDataStore.setAttribute(NAME_ATTRIBUTE, DESTINATION_DATA_STORE_NAME_ATTRIBUTE);
		sapConfiguration.appendChild(destinationDataStore);
		
		Element map = sapConfiguration.getOwnerDocument().createElement(MAP_TAG);
		destinationDataStore.appendChild(map);
		
		return destinationDataStore;
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
			if (hasAttributeValue(e, NAME_ATTRIBUTE, SERVER_DATA_STORE_NAME_ATTRIBUTE)) {
				return e;
			}
		}

		Element serverDataStore = sapConfiguration.getOwnerDocument().createElement(PROPERTY_TAG);
		serverDataStore.setAttribute(NAME_ATTRIBUTE, SERVER_DATA_STORE_NAME_ATTRIBUTE);
		sapConfiguration.appendChild(serverDataStore);
		
		Element map = sapConfiguration.getOwnerDocument().createElement(MAP_TAG);
		serverDataStore.appendChild(map);
		
		return serverDataStore;
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
			String propertyValue = properties.get(propertyName);
			if (propertyValue != null && propertyValue.length() != 0) {
				Element property = document.createElement(PROPERTY_TAG);
				property.setAttribute(NAME_ATTRIBUTE, propertyName);
				property.setAttribute(VALUE_ATTRIBUTE, properties.get(propertyName));
				configElement.appendChild(property);
			}
		}
	}
	
	public static void print(SapConnectionConfiguration sapConnectionConfiguration) throws IOException {
		System.out.println("<sapConnectionConfiguration>"); //$NON-NLS-1$
		System.out.println("  <destinationDataStore>"); //$NON-NLS-1$
		for (Map.Entry<String, DestinationData> entry: sapConnectionConfiguration.getDestinationDataStore().getEntries()) {
			System.out.println("    <entry key=\"" + entry.getKey() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
			Util.print(entry.getValue());
			System.out.println("    </entry>"); //$NON-NLS-1$
		}
		System.out.println("  </destinationDataStore>"); //$NON-NLS-1$
		System.out.println("  <serverDataStore>"); //$NON-NLS-1$
		for (Map.Entry<String,ServerData> entry: sapConnectionConfiguration.getServerDataStore().getEntries()) {
			System.out.println("    <entry key=\"" + entry.getKey() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
			Util.print(entry.getValue());
			System.out.println("    </entry>"); //$NON-NLS-1$
		}
		System.out.println("  </serverDataStore>"); //$NON-NLS-1$
		System.out.println("</sapConnectionConfiguration>"); //$NON-NLS-1$
	}
}

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
package org.fusesource.ide.sap.ui.tests.integration;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.sap.ui.export.SapGlobalConnectionConfigurationWizard;
import org.fusesource.ide.sap.ui.tests.integration.util.FuseProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Element;

/**
 * @author Aurelien Pupier
 *
 */
public class SapGlobalConfigurationWizardIT {

	@Rule
	public FuseProject fuseProject = new FuseProject(SapGlobalConfigurationWizardIT.class.getSimpleName());

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void testGeneratesCorrectGlobalNodeForBlueprint() throws Exception {
		testGeneratesGlobalNode("blueprintDefault.xml", "expectedSAPBlueprintNode.xml");
	}

	@Test
	public void testGeneratesCorrectGlobalNodeForSpring() throws Exception {
		testGeneratesGlobalNode("springDefault.xml", "expectedSAPSpringNode.xml");
	}

	/**
	 * @param initialFileName
	 * @param expectedNodeFileName
	 * @throws IOException
	 * @throws CoreException
	 * @throws TransformerException
	 */
	private void testGeneratesGlobalNode(final String initialFileName, final String expectedNodeFileName) throws IOException, CoreException, TransformerException {
		CamelFile camelFile = getInitialCamelFile(initialFileName);
		SapConnectionConfiguration sapConnectionConfigurationModel = generateSAPConfig();
		
		final SapGlobalConnectionConfigurationWizard sapGlobalConnectionConfigurationWizard = new SapGlobalConnectionConfigurationWizard(camelFile);
		sapGlobalConnectionConfigurationWizard.setSapConnectionConfigurationModel(sapConnectionConfigurationModel);
		sapGlobalConnectionConfigurationWizard.performFinish();
		
		assertThat(toString(sapGlobalConnectionConfigurationWizard.getGlobalConfigurationElementNode())).isXmlEqualToContentOf(getFile(expectedNodeFileName));
	}

	public String toString(Element element) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(element), new StreamResult(writer));
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}

	private File getFile(String fileName) {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(fileName, ".xml", tmpFolder.getRoot());
			Files.copy(this.getClass().getResourceAsStream("/" + fileName), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpFile;
	}

	/**
	 * @return
	 */
	private SapConnectionConfiguration generateSAPConfig() {
		SapConnectionConfiguration sapConnectionConfigurationModel = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
		DestinationDataStore destinationDataStore = sapConnectionConfigurationModel.getDestinationDataStore();
		DestinationData destinationDataModel = RfcFactory.eINSTANCE.createDestinationData();
		destinationDataModel.setAliasUser("myAliasUser");
		destinationDataStore.getDestinationData().add(destinationDataModel);
		destinationDataStore.getEntries().put("destinationDataStoreEntry", destinationDataModel);
		return sapConnectionConfigurationModel;
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	private CamelFile getInitialCamelFile(String name) throws IOException, CoreException {
		InputStream inputStream = SapGlobalConfigurationWizardIT.class.getClassLoader().getResourceAsStream("/" + name);
		
		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = SapGlobalConfigurationWizardIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		return new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());
	}

}

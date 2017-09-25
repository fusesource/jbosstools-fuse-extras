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
package org.jboss.tools.fuse.sap.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.APPLICATION_RELEASE;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.DESTINATION;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.IDOC_TYPE;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.IDOC_TYPE_EXTENSION;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.QUEUE;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.RFC;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.SERVER;
import static org.jboss.tools.fuse.sap.reddeer.component.SAPLabels.SYSTEM_RELEASE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.log.LogMessage;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.component.AbstractURICamelComponent;
import org.jboss.tools.fuse.reddeer.editor.CamelComponentEditPart;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.sap.reddeer.component.SAPIDocDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPIDocListDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPIDocListServer;
import org.jboss.tools.fuse.sap.reddeer.component.SAPQIDocDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPQIDocListDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPQRFCDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPSRFCDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPSRFCServer;
import org.jboss.tools.fuse.sap.reddeer.component.SAPTRFCDestination;
import org.jboss.tools.fuse.sap.reddeer.component.SAPTRFCServer;
import org.jboss.tools.fuse.sap.ui.bot.tests.utils.ProjectFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for all SAP components in Camel editor.
 * 
 * @author apodhrad
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class SAPComponentTest {

	private static Logger log = Logger.getLogger(SAPComponentTest.class);

	public static final String PROJECT_NAME = "sap-components";
	public static final ProjectType PROJECT_TYPE = ProjectType.SPRING;

	private CamelEditor editor;
	private AbstractURICamelComponent component;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupResetCamelContext() throws Exception {
		new WorkbenchShell();
		ProjectFactory.newProject(PROJECT_NAME).version(CAMEL_2_17_0_REDHAT_630187).type(PROJECT_TYPE).create();
		new LogView().open();
		new LogView().deleteLog();
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupDeleteProjects() {
		new WorkbenchShell();
		new ProjectExplorer().deleteAllProjects();
	}

	@Before
	public void openCamelCOntext() {
		new ProjectExplorer().open();
		new CamelProject(PROJECT_NAME).openCamelContext(PROJECT_TYPE.getCamelContext());
		editor = new CamelEditor(PROJECT_TYPE.getCamelContext());
	}

	@After
	public void removeComponent() {
		// a workaround for FUSETOOLS-2184
		// the Properties view must be activated before deleting a component
		new PropertySheet().open();
		editor = new CamelEditor(PROJECT_TYPE.getCamelContext());
		new CamelComponentEditPart("sap").delete();
		editor.save();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPIDocListServer() throws Exception {
		component = new SAPIDocListServer();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, APPLICATION_RELEASE, "appRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SERVER, "myServer");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE, "abc");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE_EXTENSION, "cba");
		assertEquals("FUSETOOLS-1779", component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SYSTEM_RELEASE, "sysRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPSRFCServer() throws Exception {
		component = new SAPSRFCServer();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, RFC, "XYZ");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SERVER, "myServer");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPTRFCServer() throws Exception {
		component = new SAPTRFCServer();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, RFC, "XYZ");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SERVER, "myServer");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPIDocDestination() throws Exception {
		component = new SAPIDocDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, APPLICATION_RELEASE, "appRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE, "abc");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE_EXTENSION, "cba");
		assertEquals("FUSETOOLS-1779", component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SYSTEM_RELEASE, "sysRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPIDocListDestination() throws Exception {
		component = new SAPIDocListDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, APPLICATION_RELEASE, "appRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE, "abc");
		assertEquals("FUSETOOLS-1779", component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE_EXTENSION, "cba");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SYSTEM_RELEASE, "sysRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPQIDocDestination() throws Exception {
		component = new SAPQIDocDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, APPLICATION_RELEASE, "appRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE_EXTENSION, "cba");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE, "abc");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, QUEUE, "myQueue");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SYSTEM_RELEASE, "sysRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPQIDocListDestination() throws Exception {
		component = new SAPQIDocListDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, APPLICATION_RELEASE, "appRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE, "abc");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, IDOC_TYPE_EXTENSION, "cba");
		assertEquals("FUSETOOLS-1779", component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, QUEUE, "myQueue");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, SYSTEM_RELEASE, "sysRel");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPQRFCDestination() throws Exception {
		component = new SAPQRFCDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, QUEUE, "myQueue");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, RFC, "XYZ");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPSRFCDestination() throws Exception {
		component = new SAPSRFCDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, RFC, "XYZ");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	/**
	 * <p>
	 * Tries to create/delete SAP component (see the method name) in the Camel Editor (empty Fuse project).
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a SAP component (see the method name) in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>set all properties in Advanced tab</li>
	 * <li>check the xml file for each property change</li>
	 * <li>check Error Log</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 * 
	 * @throws Exception
	 *             Something bad happened
	 */
	@Test
	public void testSAPTRFCDestination() throws Exception {
		component = new SAPTRFCDestination();
		log.info("Testing camel component '" + component.getPaletteEntry() + "'");
		editor.addCamelComponent(component, "Route _route1");
		editor.save();
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		new CamelComponentEditPart("Route _route1").select();
		editor.setAdvancedProperty(component, RFC, "XYZ");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));
		editor.setAdvancedProperty(component, DESTINATION, "myDestination");
		assertEquals(component.getUri(), editor.xpath("//route/from/@uri"));

		assertErrorLog();
	}

	private static void assertErrorLog() {
		List<LogMessage> errors = new LogView().getErrorMessages();
		if (!errors.isEmpty()) {
			log.warn("The following errors occured in Error Log:");
			for (LogMessage error : errors) {
				log.warn(error.getMessage());
			}
		}
	}

}

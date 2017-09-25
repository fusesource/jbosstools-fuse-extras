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
package org.jboss.tools.fuse.sap.ui.bot.tests.suite;

import junit.framework.TestSuite;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.sap.ui.bot.tests.SAPComponentTest;
import org.jboss.tools.fuse.sap.ui.bot.tests.SAPConfigurationTest;
import org.jboss.tools.fuse.sap.ui.bot.tests.SAPConnectionTest;
import org.jboss.tools.fuse.sap.ui.bot.tests.SAPVersionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs smoke tests on SAP Tooling
 * 
 * @author tsedmik
 */
@SuiteClasses({ SAPComponentTest.class, SAPConfigurationTest.class, SAPConnectionTest.class, SAPVersionTest.class })
@RunWith(RedDeerSuite.class)
public class AllTests extends TestSuite {

}

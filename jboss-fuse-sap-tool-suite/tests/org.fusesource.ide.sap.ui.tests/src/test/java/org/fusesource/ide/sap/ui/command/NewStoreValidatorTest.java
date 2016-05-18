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
package org.fusesource.ide.sap.ui.command;

import java.util.Collections;

import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.sap.ui.Messages;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NewStoreValidatorTest {

	@Test
	public void testIsValid() throws Exception {
		assertThat(new NewStoreValidator(Collections.<String> emptySet(), "", "").isValid("Test1")).isNull();
	}

	@Test
	public void testIsNotValidDuplication() throws Exception {
		assertThat(new NewStoreValidator(Collections.singleton("TestDuplicate"), "", Messages.DestinationDialog_destinationAlreadyExists).isValid("TestDuplicate"))
				.isEqualTo(NLS.bind(Messages.DestinationDialog_destinationAlreadyExists, "TestDuplicate"));
	}

	@Test
	public void testIsNotValidEmpty() throws Exception {
		assertThat(new NewStoreValidator(Collections.<String> emptySet(), Messages.DestinationDialog_message, "").isValid("")).isEqualTo(Messages.DestinationDialog_message);
	}

	@Test
	public void testIsNotValidNull() throws Exception {
		assertThat(new NewStoreValidator(Collections.<String> emptySet(), Messages.DestinationDialog_message, "").isValid(null)).isEqualTo(Messages.DestinationDialog_message);
	}

}

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
package org.fusesource.ide.sap.ui.converter;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SncQos2SncQosComboSelectionConverterTest {

	@Test
	public void testRoundTripWithNull() throws Exception {
		assertThat(new SncQosComboSelection2SncQosConverter().convert(new SncQos2SncQosComboSelectionConverter().convert(null))).isEqualTo(null);
	}

}

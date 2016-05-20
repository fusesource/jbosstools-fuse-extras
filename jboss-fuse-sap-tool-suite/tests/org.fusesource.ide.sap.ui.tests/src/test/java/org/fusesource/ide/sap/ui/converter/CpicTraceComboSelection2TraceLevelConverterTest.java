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

public class CpicTraceComboSelection2TraceLevelConverterTest {

	@Test
	public void testRoundTrip() throws Exception {
		assertThat(new CpicTraceComboSelection2TraceLevelConverter().convert(new TraceLevel2CpicTraceComboSelectionConverter().convert("1"))).isEqualTo("1");
	}

	@Test
	public void testRoundTripWithNotInitializedValue() throws Exception {
		assertThat(new CpicTraceComboSelection2TraceLevelConverter().convert(new TraceLevel2CpicTraceComboSelectionConverter().convert(null))).isEqualTo(null);
	}
}

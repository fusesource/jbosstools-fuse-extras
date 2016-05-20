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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SncQosComboSelection2SncQosConverterTest {

	@Parameters
	public static Collection<Integer> data() {
		return Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5 });
	}

	private Integer index;

	public SncQosComboSelection2SncQosConverterTest(Integer index) {
		this.index = index;
	}

	@Test
	public void testRoundTripConversion() throws Exception {
		assertThat(new SncQos2SncQosComboSelectionConverter().convert(new SncQosComboSelection2SncQosConverter().convert(index))).isEqualTo(index);
	}

}

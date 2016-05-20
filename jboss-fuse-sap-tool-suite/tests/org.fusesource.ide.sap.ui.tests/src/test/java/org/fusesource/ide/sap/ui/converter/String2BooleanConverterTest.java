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

public class String2BooleanConverterTest {

	@Test
	public void tesRoundTripConversionForTrue() throws Exception {
		assertThat(new String2BooleanConverter().convert(new Boolean2StringConverter().convert(true))).isEqualTo(true);
	}

	@Test
	public void tesRoundTripConversionForFalse() throws Exception {
		assertThat(new String2BooleanConverter().convert(new Boolean2StringConverter().convert(false))).isEqualTo(false);
	}

	@Test
	public void tesRoundTripConversionFor1() throws Exception {
		assertThat(new Boolean2StringConverter().convert(new String2BooleanConverter().convert("1"))).isEqualTo("1");
	}

	@Test
	public void tesRoundTripConversionFor0() throws Exception {
		assertThat(new Boolean2StringConverter().convert(new String2BooleanConverter().convert("0"))).isEqualTo("0");
	}

	@Test
	public void tesRoundTripConversionForNullHandledAsFalse() throws Exception {
		assertThat(new Boolean2StringConverter().convert(new String2BooleanConverter().convert(null))).isEqualTo("0");
	}
}

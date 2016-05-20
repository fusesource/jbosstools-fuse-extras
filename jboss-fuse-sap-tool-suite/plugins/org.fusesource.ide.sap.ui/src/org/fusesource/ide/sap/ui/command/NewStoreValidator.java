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

import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.osgi.util.NLS;

/**
 * @author Aurelien Pupier
 *
 */
public final class NewStoreValidator implements IInputValidator {

	private Set<String> existingValues;
	private String messageForDuplicatedValue;

	public NewStoreValidator(Set<String> existingValues, String messageForDuplicatedValue) {
		this.existingValues = existingValues;
		this.messageForDuplicatedValue = messageForDuplicatedValue;
	}

	@Override
	public String isValid(String newText) {
		if (newText == null || newText.isEmpty()) {
			return "";
		}
		if (existingValues.contains(newText)) {
			return NLS.bind(messageForDuplicatedValue, newText);
		}
		return null;
	}
}
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
package org.fusesource.ide.sap.ui.properties.uicreator;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * @author Aurelien Pupier
 *
 */
public class HelpDecorator {

	private HelpDecorator() {
	}

	public static void createHelpDecoration(String description, Control control) {
		if (description != null) {
			ControlDecoration helpDecoration = new ControlDecoration(control, SWT.BOTTOM | SWT.LEFT);
			helpDecoration.setShowOnlyOnFocus(true);
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
			helpDecoration.setImage(fieldDecoration.getImage());
			helpDecoration.setDescriptionText(description);
			control.setToolTipText(description);
		}
	}

}

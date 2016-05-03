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
package org.fusesource.ide.sap.ui.properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public abstract class BasePropertySection extends AbstractPropertySection {

	private DataBindingContext bindingContext;

	protected Composite createFlatFormComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE) {
			// This is overridden to prevent issues with JFace bindings in property sections.
			@Override
			public boolean setFocus() {
				return true;
			}
		};
		composite.setBackground(getWidgetFactory().getColors().getBackground());
		getWidgetFactory().paintBordersFor(composite);
        FormLayout layout = new FormLayout();
        layout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
        layout.marginHeight = ITabbedPropertyConstants.VSPACE;
        layout.spacing = ITabbedPropertyConstants.VMARGIN + 1;
        composite.setLayout(layout);
        return composite;
	}

	protected DataBindingContext initDataBindings() {
		if (bindingContext != null) {
			bindingContext.dispose();
			bindingContext = null;
		}
		
		return bindingContext = new DataBindingContext();
	}
		
	@Override
	public void refresh() {
		bindingContext.updateTargets();
	}
	
}

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
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

public abstract class BasePropertySection extends AbstractPropertySection {

	private DataBindingContext bindingContext;

	protected DataBindingContext initDataBindings() {
		if (bindingContext != null) {
			bindingContext.dispose();
			bindingContext = null;
		}
		
		return bindingContext = new DataBindingContext();
	}
		
	@Override
	public void refresh() {
		super.refresh();
		// bindingContext.updateTargets();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#
	 * aboutToBeShown()
	 */
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		// initDataBindings();
	}

}

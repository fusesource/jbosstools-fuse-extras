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
package org.fusesource.ide.sap.ui.export;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.sap.ui.properties.uicreator.IDataUICreator;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class AbstractDataProperties {

	protected EditingDomain editingDomain;
	protected DataBindingContext bindingContext;

	public CTabFolder createControl(Composite parent) {
		TabbedPropertySheetWidgetFactory widgetFactory = new TabbedPropertySheetWidgetFactory();
	
		CTabFolder dataTabFolder = widgetFactory.createTabFolder(parent, SWT.BORDER);
		dataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		dataTabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				// With CTabItem, it seems that widgets are not refreshed
				// automatically as it is the case in Property Sections
				bindingContext.updateTargets();
			}
		});
	
		createTabItems(widgetFactory, dataTabFolder);
	
		return dataTabFolder;
	}

	/**
	 * @param widgetFactory
	 * @param destinationDataTabFolder
	 */
	protected abstract void createTabItems(TabbedPropertySheetWidgetFactory widgetFactory, CTabFolder destinationDataTabFolder);

	/**
	 * @param widgetFactory
	 * @param destinationDataTabFolder
	 * @param tabItemName
	 * @param uiCreator
	 */
	protected CTabItem createTabItem(TabbedPropertySheetWidgetFactory widgetFactory, CTabFolder destinationDataTabFolder, final String tabItemName,
			final IDataUICreator uiCreator) {
		CTabItem dataBasicItem = widgetFactory.createTabItem(destinationDataTabFolder, SWT.NONE);
		dataBasicItem.setText(tabItemName);
	
		Composite container = widgetFactory.createFlatFormComposite(destinationDataTabFolder);
		dataBasicItem.setControl(container);
		container.setLayout(compositeFormLayout());
	
		uiCreator.createControls(container, new TabbedPropertySheetWidgetFactory());
		return dataBasicItem;
	}

	private FormLayout compositeFormLayout() {
		FormLayout layout = new FormLayout();
		layout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
		layout.marginHeight = ITabbedPropertyConstants.VSPACE;
		layout.spacing = ITabbedPropertyConstants.VMARGIN + 1;
		return layout;
	}

}
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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.SncQos2SncQosComboSelectionConverter;
import org.fusesource.ide.sap.ui.converter.SncQosComboSelection2SncQosConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;

/**
 * @author Aurelien Pupier
 *
 */
public class SncServerDataUICreator implements IServerDataUICreator {

	private Button sncModeBtn;
	private CCombo sncQopCombo;
	private Text sncMynameText;
	private Text sncLibraryText;

	@Override
	public void createControls(Composite sncContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		sncModeBtn = widgetFactory.createButton(sncContainer, Messages.SncServerPropertySection_SncModeLabel, SWT.CHECK);
		sncModeBtn.setToolTipText(Messages.SncServerPropertySection_SncModeToolTip);
		sncModeBtn.setLayoutData(LayoutUtil.firstEntryLayoutData());

		sncQopCombo = widgetFactory.createCCombo(sncContainer, SWT.READ_ONLY);
		sncQopCombo.setToolTipText(Messages.SncServerPropertySection_SncQopToolTip);
		sncQopCombo.setItems(new String[] { "", Messages.SncServerPropertySection_SncSecurityLevel1Label, Messages.SncServerPropertySection_SncSecurityLevel2Label, //$NON-NLS-1$
				Messages.SncServerPropertySection_SncSecurityLevel3Label, Messages.SncServerPropertySection_SncSecurityLevel8Label,
				Messages.SncServerPropertySection_SncSecurityLevel9Label });
		sncQopCombo.setLayoutData(LayoutUtil.entryLayoutData(sncModeBtn));
		sncQopCombo.select(0);

		CLabel sncQopLbl = widgetFactory.createCLabel(sncContainer, Messages.SncServerPropertySection_SncQopLabel);
		sncQopLbl.setLayoutData(LayoutUtil.labelLayoutData(sncQopCombo));
		sncQopLbl.setAlignment(SWT.RIGHT);

		sncMynameText = widgetFactory.createText(sncContainer, null);
		sncMynameText.setToolTipText(Messages.SncServerPropertySection_SncMynameToolTip);
		sncMynameText.setLayoutData(LayoutUtil.entryLayoutData(sncQopCombo));

		CLabel sncMynameLbl = widgetFactory.createCLabel(sncContainer, Messages.SncServerPropertySection_SncMynameLabel);
		sncMynameLbl.setLayoutData(LayoutUtil.labelLayoutData(sncMynameText));
		sncMynameLbl.setAlignment(SWT.RIGHT);

		sncLibraryText = widgetFactory.createText(sncContainer, null);
		sncLibraryText.setToolTipText(Messages.SncServerPropertySection_SncLibraryToolTip);
		sncLibraryText.setLayoutData(LayoutUtil.entryLayoutData(sncMynameText));

		CLabel sncLibraryLbl = widgetFactory.createCLabel(sncContainer, Messages.SncServerPropertySection_SncLibraryLabel);
		sncLibraryLbl.setLayoutData(LayoutUtil.labelLayoutData(sncLibraryText));
		sncLibraryLbl.setAlignment(SWT.RIGHT);
	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, ServerDataStoreEntryImpl serverDataStoreEntry) {
		//
		IObservableValue observeSelectionSncModeBtnObserveWidget = WidgetProperties.selection().observe(sncModeBtn);
		IObservableValue managedConnectionFactorySncModeObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_MODE)).observe(serverDataStoreEntry);
		UpdateValueStrategy strategy_13 = new UpdateValueStrategy();
		strategy_13.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy sncModeModelStrategy = new UpdateValueStrategy();
		sncModeModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionSncModeBtnObserveWidget, managedConnectionFactorySncModeObserveValue, strategy_13, sncModeModelStrategy);
		//
		IObservableValue observeSingleSelectionIndexSncQopComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(sncQopCombo);
		IObservableValue managedConnectionFactorySncQopObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_QOP)).observe(serverDataStoreEntry);
		UpdateValueStrategy sncQopStategy = new UpdateValueStrategy();
		sncQopStategy.setConverter(new SncQosComboSelection2SncQosConverter());
		UpdateValueStrategy sncQopModelStrategy = new UpdateValueStrategy();
		sncQopModelStrategy.setConverter(new SncQos2SncQosComboSelectionConverter());
		bindingContext.bindValue(observeSingleSelectionIndexSncQopComboObserveWidget, managedConnectionFactorySncQopObserveValue, sncQopStategy, sncQopModelStrategy);
		//
		IObservableValue observeTextSncMynameTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, sncMynameText);
		IObservableValue managedConnectionFactorySncMynameObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_MYNAME)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextSncMynameTextObserveWidget, managedConnectionFactorySncMynameObserveValue);
		//
		IObservableValue observeTextSncLibraryTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, sncLibraryText);
		IObservableValue managedConnectionFactorySncLibraryObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_LIB)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextSncLibraryTextObserveWidget, managedConnectionFactorySncLibraryObserveValue);

	}

}

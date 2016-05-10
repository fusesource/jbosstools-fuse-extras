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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;

/**
 * @author Aurelien Pupier
 *
 */
public class RepositoryDestinationDataUICreator implements IDestinationDataUICreator {

	private Text repositoryDestinationText;
	private Text repositoryUserText;
	private Text repositoryPasswordText;
	private Button respositorySncBtn;
	private Button repositoryRoundtripOptimizationBtn;

	@Override
	public void createControls(Composite repositoryContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		repositoryDestinationText = widgetFactory.createText(repositoryContainer, null);
		repositoryDestinationText.setToolTipText(Messages.RepositoryPropertySection_RepositoryDestinationToolTip);
		repositoryDestinationText.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel repositoryDestinationLbl = widgetFactory.createCLabel(repositoryContainer, Messages.RepositoryPropertySection_RepositoryDestinationLabel);
		repositoryDestinationLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryDestinationText));
		repositoryDestinationLbl.setAlignment(SWT.RIGHT);

		repositoryUserText = widgetFactory.createText(repositoryContainer, null);
		repositoryUserText.setToolTipText(Messages.RepositoryPropertySection_RepositoryUserToolTip);
		repositoryUserText.setLayoutData(LayoutUtil.entryLayoutData(repositoryDestinationText));

		CLabel repositoryUserLbl = widgetFactory.createCLabel(repositoryContainer, Messages.RepositoryPropertySection_RepositoryUserLabel);
		repositoryUserLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryUserText));
		repositoryUserLbl.setAlignment(SWT.RIGHT);

		repositoryPasswordText = widgetFactory.createText(repositoryContainer, null, SWT.PASSWORD);
		repositoryPasswordText.setToolTipText(Messages.RepositoryPropertySection_RepositoryPasswordToolTip);
		repositoryPasswordText.setLayoutData(LayoutUtil.entryLayoutData(repositoryUserText));

		CLabel repositoryPasswordLbl = widgetFactory.createCLabel(repositoryContainer, Messages.RepositoryPropertySection_RepositoryPasswordLabel);
		repositoryPasswordLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryPasswordText));
		repositoryPasswordLbl.setAlignment(SWT.RIGHT);

		respositorySncBtn = widgetFactory.createButton(repositoryContainer, Messages.RepositoryPropertySection_RepositorySncLabel, SWT.CHECK);
		respositorySncBtn.setToolTipText(Messages.RepositoryPropertySection_RepositorySncToolTip);
		respositorySncBtn.setLayoutData(LayoutUtil.entryLayoutData(repositoryPasswordText));

		repositoryRoundtripOptimizationBtn = widgetFactory.createButton(repositoryContainer, Messages.RepositoryPropertySection_RepositoryRoundtripOptimizationLabel, SWT.CHECK);
		repositoryRoundtripOptimizationBtn.setToolTipText(Messages.RepositoryPropertySection_RepositoryRoundtripOptimizationToolTip);
		repositoryRoundtripOptimizationBtn.setLayoutData(LayoutUtil.entryLayoutData(respositorySncBtn));

	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, DestinationDataStoreEntryImpl destinationDataStoreEntry) {
		//
		IObservableValue observeTextRepositoryDestinationTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, repositoryDestinationText);
		IObservableValue managedConnectionFactoryRepositoryDestObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_DEST))
				.observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextRepositoryDestinationTextObserveWidget, managedConnectionFactoryRepositoryDestObserveValue);
		//
		IObservableValue observeTextRepositoryUserTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, repositoryUserText);
		IObservableValue managedConnectionFactoryRepositoryUserObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_USER))
				.observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextRepositoryUserTextObserveWidget, managedConnectionFactoryRepositoryUserObserveValue);
		//
		IObservableValue observeTextRepositoryPasswordTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, repositoryPasswordText);
		IObservableValue managedConnectionFactoryRepositoryPasswdObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_PASSWD))
				.observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextRepositoryPasswordTextObserveWidget, managedConnectionFactoryRepositoryPasswdObserveValue);
		//
		IObservableValue observeSelectionRespositorySncBtnObserveWidget = WidgetProperties.selection().observe(respositorySncBtn);
		IObservableValue managedConnectionFactoryRepositorySncObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_SNC))
				.observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_15 = new UpdateValueStrategy();
		strategy_15.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy repositorySncModelStrategy = new UpdateValueStrategy();
		repositorySncModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionRespositorySncBtnObserveWidget, managedConnectionFactoryRepositorySncObserveValue, strategy_15, repositorySncModelStrategy);
		//
		IObservableValue observeSelectionRepositoryRoundtripOptimizationBtnObserveWidget = WidgetProperties.selection().observe(repositoryRoundtripOptimizationBtn);
		IObservableValue managedConnectionFactoryRepositoryRoundtripOptimizationObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_ROUNDTRIP_OPTIMIZATION))
				.observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_16 = new UpdateValueStrategy();
		strategy_16.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy repositoryRoundtripOptimizationModelStrategy = new UpdateValueStrategy();
		repositoryRoundtripOptimizationModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionRepositoryRoundtripOptimizationBtnObserveWidget, managedConnectionFactoryRepositoryRoundtripOptimizationObserveValue, strategy_16,
				repositoryRoundtripOptimizationModelStrategy);
	}

}

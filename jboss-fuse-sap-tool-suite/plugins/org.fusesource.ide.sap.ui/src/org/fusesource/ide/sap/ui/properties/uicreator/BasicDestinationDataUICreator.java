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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.ClientNumberValidator;
import org.fusesource.ide.sap.ui.validator.LanguageValidator;
import org.fusesource.ide.sap.ui.validator.SystemNumberValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class BasicDestinationDataUICreator implements IDestinationDataUICreator {

	private Text ashostText;
	private Text sysnrText;
	private Text clientText;
	private Text passwordText;
	private Text languageText;
	private Text userText;

	@Override
	public void createControls(Composite basicContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		ashostText = widgetFactory.createText(basicContainer, null);
		ashostText.setToolTipText(Messages.BasicPropertySection_AshostToolTip);
		ashostText.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel ashostLbl = widgetFactory.createCLabel(basicContainer, Messages.BasicPropertySection_AshostLabel);
		ashostLbl.setLayoutData(LayoutUtil.labelLayoutData(ashostText));
		ashostLbl.setAlignment(SWT.RIGHT);

		sysnrText = widgetFactory.createText(basicContainer, null);
		sysnrText.setToolTipText(Messages.BasicPropertySection_SysnrToolTip);
		sysnrText.setLayoutData(LayoutUtil.entryLayoutData(ashostText));

		CLabel systemNumberLbl = widgetFactory.createCLabel(basicContainer, Messages.BasicPropertySection_SysnrLabel);
		systemNumberLbl.setLayoutData(LayoutUtil.labelLayoutData(sysnrText));
		systemNumberLbl.setAlignment(SWT.RIGHT);

		clientText = widgetFactory.createText(basicContainer, null);
		clientText.setToolTipText(Messages.BasicPropertySection_ClientToolTip);
		clientText.setLayoutData(LayoutUtil.entryLayoutData(sysnrText));

		CLabel clientLbl = widgetFactory.createCLabel(basicContainer, Messages.BasicPropertySection_ClientLabel);
		clientLbl.setLayoutData(LayoutUtil.labelLayoutData(clientText));
		clientLbl.setAlignment(SWT.RIGHT);

		userText = widgetFactory.createText(basicContainer, null);
		userText.setToolTipText(Messages.BasicPropertySection_UserToolTip);
		userText.setLayoutData(LayoutUtil.entryLayoutData(clientText));

		CLabel userLbl = widgetFactory.createCLabel(basicContainer, Messages.BasicPropertySection_UserLabel);
		userLbl.setLayoutData(LayoutUtil.labelLayoutData(userText));
		userLbl.setAlignment(SWT.RIGHT);

		passwordText = widgetFactory.createText(basicContainer, null, SWT.PASSWORD);
		passwordText.setToolTipText(Messages.BasicPropertySection_PasswordToolTip);
		passwordText.setLayoutData(LayoutUtil.entryLayoutData(userText));

		CLabel passwordLbl = widgetFactory.createCLabel(basicContainer, Messages.BasicPropertySection_PasswordLabel);
		passwordLbl.setLayoutData(LayoutUtil.labelLayoutData(passwordText));
		passwordLbl.setAlignment(SWT.RIGHT);

		languageText = widgetFactory.createText(basicContainer, null);
		languageText.setToolTipText(Messages.BasicPropertySection_LanguageToolTip);
		languageText.setLayoutData(LayoutUtil.entryLayoutData(passwordText));
		new Label(basicContainer, SWT.NONE);
		new Label(basicContainer, SWT.NONE);

		CLabel languageLbl = widgetFactory.createCLabel(basicContainer, Messages.BasicPropertySection_LanguageLabel);
		languageLbl.setLayoutData(LayoutUtil.labelLayoutData(languageText));
		languageLbl.setAlignment(SWT.RIGHT);
	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, DestinationDataStoreEntryImpl destinationDataStoreEntry) {
		//
		IObservableValue observeTextAshostTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, ashostText);
		IObservableValue destinationAshostObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ASHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextAshostTextObserveWidget, destinationAshostObserveValue);
		//
		IObservableValue observeTextSysnrTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sysnrText);
		IObservableValue destinationSysnrObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SYSNR)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setBeforeSetValidator(new SystemNumberValidator());
		Binding sysnrBinding = bindingContext.bindValue(observeTextSysnrTextObserveWidget, destinationSysnrObserveValue, strategy_1, null);
		//
		IObservableValue observeTextClientTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(clientText);
		IObservableValue destinationClientObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CLIENT)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new ClientNumberValidator());
		Binding clientBinding = bindingContext.bindValue(observeTextClientTextObserveWidget, destinationClientObserveValue, strategy, null);
		//
		IObservableValue observeTextUserTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userText);
		IObservableValue destinationUserNameObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__USER_NAME)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextUserTextObserveWidget, destinationUserNameObserveValue);
		//
		IObservableValue observeTextPasswordTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(passwordText);
		IObservableValue destinationPasswordObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PASSWORD)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextPasswordTextObserveWidget, destinationPasswordObserveValue);
		//
		IObservableValue observeTextLanguageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(languageText);
		IObservableValue destinationLangObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LANG)).observe(destinationDataStoreEntry);
		UpdateValueStrategy langStrategy = new UpdateValueStrategy();
		langStrategy.setBeforeSetValidator(new LanguageValidator());
		Binding langBinding = bindingContext.bindValue(observeTextLanguageTextObserveWidget, destinationLangObserveValue, langStrategy, null);

		ControlDecorationSupport.create(sysnrBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(clientBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(langBinding, SWT.TOP | SWT.LEFT);
	}

}

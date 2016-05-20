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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.ClientNumberValidator;
import org.fusesource.ide.sap.ui.validator.LanguageValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class AuthenticationDestinationDataUICreator implements IDestinationDataUICreator {

	private CCombo authTypeCombo;
	private Text clientText2;
	private Text userText2;
	private Text userAlias;
	private Text passwordText2;
	private Text mysapsso2Text;
	private Text x509certText;
	private Text languageText2;

	@Override
	public void createControls(Composite authContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		authTypeCombo = widgetFactory.createCCombo(authContainer, SWT.READ_ONLY);
		authTypeCombo.setToolTipText(Messages.AuthenticationPropertySection_AuthTypeToolTip);
		authTypeCombo.setItems(new String[] { "CONFIGURED_USER", "CURRENT_USER" }); //$NON-NLS-1$ //$NON-NLS-2$
		authTypeCombo.select(0);
		authTypeCombo.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel authTypeLbl = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_AuthTypeLable);
		authTypeLbl.setLayoutData(LayoutUtil.labelLayoutData(authTypeCombo));
		authTypeLbl.setAlignment(SWT.RIGHT);

		clientText2 = widgetFactory.createText(authContainer, null);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_ClientToolTip, clientText2);
		clientText2.setLayoutData(LayoutUtil.entryLayoutData(authTypeCombo));

		CLabel clientLbl2 = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_ClientLable);
		clientLbl2.setLayoutData(LayoutUtil.labelLayoutData(clientText2));
		clientLbl2.setAlignment(SWT.RIGHT);

		userText2 = widgetFactory.createText(authContainer, null);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_UserToolTip, userText2);
		userText2.setLayoutData(LayoutUtil.entryLayoutData(clientText2));

		CLabel userLbl2 = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_UserLabel);
		userLbl2.setLayoutData(LayoutUtil.labelLayoutData(userText2));
		userLbl2.setAlignment(SWT.RIGHT);

		userAlias = widgetFactory.createText(authContainer, null);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_UserAliasToolTip, userAlias);
		userAlias.setLayoutData(LayoutUtil.entryLayoutData(userText2));

		CLabel userAliasLbl = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_UserAliasLabel);
		userAliasLbl.setLayoutData(LayoutUtil.labelLayoutData(userAlias));
		userAliasLbl.setAlignment(SWT.RIGHT);

		passwordText2 = widgetFactory.createText(authContainer, null, SWT.PASSWORD);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_PasswordToolTip, passwordText2);
		passwordText2.setLayoutData(LayoutUtil.entryLayoutData(userAlias));

		CLabel passwordLbl2 = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_PasswordLabel);
		passwordLbl2.setLayoutData(LayoutUtil.labelLayoutData(passwordText2));
		passwordLbl2.setAlignment(SWT.RIGHT);

		mysapsso2Text = widgetFactory.createText(authContainer, null);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_Mysapsso2ToolTip, mysapsso2Text);
		mysapsso2Text.setLayoutData(LayoutUtil.entryLayoutData(passwordText2));

		CLabel mysapsso2Lbl = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_Mysapsso2Label);
		mysapsso2Lbl.setLayoutData(LayoutUtil.labelLayoutData(mysapsso2Text));
		mysapsso2Lbl.setAlignment(SWT.RIGHT);

		x509certText = widgetFactory.createText(authContainer, null);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_X509certToolTip, x509certText);
		x509certText.setLayoutData(LayoutUtil.entryLayoutData(mysapsso2Text));

		CLabel x509certLbl = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_X509certLabel);
		x509certLbl.setLayoutData(LayoutUtil.labelLayoutData(x509certText));
		x509certLbl.setAlignment(SWT.RIGHT);

		languageText2 = widgetFactory.createText(authContainer, null);
		HelpDecorator.createHelpDecoration(Messages.AuthenticationPropertySection_LanguageToolTip, languageText2);
		languageText2.setLayoutData(LayoutUtil.entryLayoutData(x509certText));

		CLabel languageLbl2 = widgetFactory.createCLabel(authContainer, Messages.AuthenticationPropertySection_LanguageLabel);
		languageLbl2.setLayoutData(LayoutUtil.labelLayoutData(languageText2));
		languageLbl2.setAlignment(SWT.RIGHT);
	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, DestinationDataStoreEntryImpl destinationDataStoreEntry) {
		//
		IObservableValue observeTextAuthTypeComboObserveWidget = WidgetProperties.text().observe(authTypeCombo);
		IObservableValue managedConnectionFactoryAuthTypeObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__AUTH_TYPE)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextAuthTypeComboObserveWidget, managedConnectionFactoryAuthTypeObserveValue);
		//
		IObservableValue observeTextClientText2ObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, clientText2);
		IObservableValue managedConnectionFactoryClientObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CLIENT)).observe(destinationDataStoreEntry);
		UpdateValueStrategy clientStrategy2 = new UpdateValueStrategy();
		clientStrategy2.setBeforeSetValidator(new ClientNumberValidator());
		Binding clientBinding2 = bindingContext.bindValue(observeTextClientText2ObserveWidget, managedConnectionFactoryClientObserveValue, clientStrategy2, null);
		//
		IObservableValue observeTextUserText2ObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, userText2);
		IObservableValue managedConnectionFactoryUserObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__USER)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextUserText2ObserveWidget, managedConnectionFactoryUserObserveValue);
		//
		IObservableValue observeTextUserAliasObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, userAlias);
		IObservableValue managedConnectionFactoryAliasUserObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ALIAS_USER)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextUserAliasObserveWidget, managedConnectionFactoryAliasUserObserveValue);
		//
		IObservableValue observeTextPasswordText2ObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, passwordText2);
		IObservableValue managedConnectionFactoryPasswordObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PASSWORD)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextPasswordText2ObserveWidget, managedConnectionFactoryPasswordObserveValue);
		//
		IObservableValue observeTextMysapsso2TextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, mysapsso2Text);
		IObservableValue managedConnectionFactoryMysapsso2ObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MYSAPSSO2)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextMysapsso2TextObserveWidget, managedConnectionFactoryMysapsso2ObserveValue);
		//
		IObservableValue observeTextX509certTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, x509certText);
		IObservableValue managedConnectionFactoryX509certObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__X509CERT)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextX509certTextObserveWidget, managedConnectionFactoryX509certObserveValue);
		//
		IObservableValue observeTextLanguageText2ObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, languageText2);
		IObservableValue managedConnectionFactoryLangObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LANG)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setBeforeSetValidator(new LanguageValidator());
		Binding langBinding2 = bindingContext.bindValue(observeTextLanguageText2ObserveWidget, managedConnectionFactoryLangObserveValue, strategy_3, null);

		ControlDecorationSupport.create(clientBinding2, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(langBinding2, SWT.TOP | SWT.LEFT);
	}

}

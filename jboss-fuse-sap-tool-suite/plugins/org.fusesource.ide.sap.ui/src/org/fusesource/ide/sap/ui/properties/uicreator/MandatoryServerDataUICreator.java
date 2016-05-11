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
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class MandatoryServerDataUICreator implements IServerDataUICreator {

	private Text gwhostText;
	private Text gwservText;
	private Text progidText;
	private Text repositoryDestinationText;
	private Text connectionCountText;

	@Override
	public void createControls(Composite mandatoryContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		gwhostText = widgetFactory.createText(mandatoryContainer, null);
		gwhostText.setToolTipText(Messages.MandatoryServerPropertySection_GwhostToolTip);
		gwhostText.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel gwhostLbl = widgetFactory.createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_GwhostLabel);
		gwhostLbl.setLayoutData(LayoutUtil.labelLayoutData(gwhostText));
		gwhostLbl.setAlignment(SWT.RIGHT);

		gwservText = widgetFactory.createText(mandatoryContainer, null);
		gwservText.setToolTipText(Messages.MandatoryServerPropertySection_GwservToolTip);
		gwservText.setLayoutData(LayoutUtil.entryLayoutData(gwhostText));

		CLabel gwservLbl = widgetFactory.createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_GwservLabel);
		gwservLbl.setLayoutData(LayoutUtil.labelLayoutData(gwservText));
		gwservLbl.setAlignment(SWT.RIGHT);

		progidText = widgetFactory.createText(mandatoryContainer, null);
		progidText.setToolTipText(Messages.MandatoryServerPropertySection_ProgidToolTip);
		progidText.setLayoutData(LayoutUtil.entryLayoutData(gwservText));

		CLabel progidLbl = widgetFactory.createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_ProgidLabel);
		progidLbl.setLayoutData(LayoutUtil.labelLayoutData(progidText));
		progidLbl.setAlignment(SWT.RIGHT);

		repositoryDestinationText = widgetFactory.createText(mandatoryContainer, null);
		repositoryDestinationText.setToolTipText(Messages.OptionalServerPropertySection_RepositoryDestinationToolTip);
		repositoryDestinationText.setLayoutData(LayoutUtil.entryLayoutData(progidText));

		CLabel repositoryDestinationLbl = widgetFactory.createCLabel(mandatoryContainer, Messages.OptionalServerPropertySection_RepositoryDestinationLabel);
		repositoryDestinationLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryDestinationText));
		repositoryDestinationLbl.setAlignment(SWT.RIGHT);

		connectionCountText = widgetFactory.createText(mandatoryContainer, null);
		connectionCountText.setToolTipText(Messages.MandatoryServerPropertySection_ConnectionCountToolTip);
		connectionCountText.setLayoutData(LayoutUtil.entryLayoutData(repositoryDestinationText));
		new Label(mandatoryContainer, SWT.NONE);
		new Label(mandatoryContainer, SWT.NONE);

		CLabel connectionCountLbl = widgetFactory.createCLabel(mandatoryContainer, Messages.MandatoryServerPropertySection_ConnectionCountLabel);
		connectionCountLbl.setLayoutData(LayoutUtil.labelLayoutData(connectionCountText));
		connectionCountLbl.setAlignment(SWT.RIGHT);
	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, ServerDataStoreEntryImpl serverDataStoreEntry) {
		IObservableValue observeTextAshostTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, gwhostText);
		IObservableValue destinationAshostObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__GWHOST)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextAshostTextObserveWidget, destinationAshostObserveValue, null, null);
		//
		IObservableValue observeTextSysnrTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, gwservText);
		IObservableValue destinationSysnrObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__GWSERV)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextSysnrTextObserveWidget, destinationSysnrObserveValue, null, null);
		//
		IObservableValue observeTextClientTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, progidText);
		IObservableValue destinationClientObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__PROGID)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeTextClientTextObserveWidget, destinationClientObserveValue, null, null);
		//
		IObservableValue observeRepositoryDestinationTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, repositoryDestinationText);
		IObservableValue serverRepositoryDestinationObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__REPOSITORY_DESTINATION)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeRepositoryDestinationTextObserveWidget, serverRepositoryDestinationObserveValue, null, null);
		//
		IObservableValue observeTextLanguageTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, connectionCountText);
		IObservableValue destinationLangObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__CONNECTION_COUNT)).observe(serverDataStoreEntry);
		UpdateValueStrategy connectionCountStrategy = new UpdateValueStrategy();
		connectionCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.MandatoryServerPropertySection_ConnectionCountValidator));
		Binding connectionCountBinding = bindingContext.bindValue(observeTextLanguageTextObserveWidget, destinationLangObserveValue, connectionCountStrategy, null);

		ControlDecorationSupport.create(connectionCountBinding, SWT.TOP | SWT.LEFT);
	}

}

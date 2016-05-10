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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.SapRouterStringValidator;
import org.fusesource.ide.sap.ui.validator.SystemNumberValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class ConnectionDestinationDataUICreator implements IDestinationDataUICreator {

	private Text sysnrText2;
	private Text saprouterText;
	private Text ashostText2;
	private Text mshostText;
	private Text msservText;
	private Text gwhostText;
	private Text gwservText;
	private Text r3nameText;
	private Text groupText;

	/**
	 * @param connectionContainer
	 * @param widgetFactory
	 */
	public void createControls(Composite connectionContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		sysnrText2 = widgetFactory.createText(connectionContainer, null);
		sysnrText2.setToolTipText(Messages.ConnectionPropertySection_SysnrToolTip);
		sysnrText2.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel sysnrLbl2 = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_SysnrLabel);
		sysnrLbl2.setLayoutData(LayoutUtil.labelLayoutData(sysnrText2));
		sysnrLbl2.setAlignment(SWT.RIGHT);

		saprouterText = widgetFactory.createText(connectionContainer, null);
		saprouterText.setToolTipText(Messages.ConnectionPropertySection_SaprouterToolTip);
		saprouterText.setLayoutData(LayoutUtil.entryLayoutData(sysnrText2));

		CLabel saprouterLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_SaprouterLabel);
		saprouterLbl.setLayoutData(LayoutUtil.labelLayoutData(saprouterText));
		saprouterLbl.setAlignment(SWT.RIGHT);

		ashostText2 = widgetFactory.createText(connectionContainer, null);
		ashostText2.setToolTipText(Messages.ConnectionPropertySection_AshostToolTip);
		ashostText2.setLayoutData(LayoutUtil.entryLayoutData(saprouterText));

		CLabel ashostLbl2 = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_AshostLabel);
		ashostLbl2.setLayoutData(LayoutUtil.labelLayoutData(ashostText2));
		ashostLbl2.setAlignment(SWT.RIGHT);

		mshostText = widgetFactory.createText(connectionContainer, null);
		mshostText.setToolTipText(Messages.ConnectionPropertySection_MshostToolTip);
		mshostText.setLayoutData(LayoutUtil.entryLayoutData(ashostText2));

		CLabel mshostLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_MshostLabel);
		mshostLbl.setLayoutData(LayoutUtil.labelLayoutData(mshostText));
		mshostLbl.setAlignment(SWT.RIGHT);

		msservText = widgetFactory.createText(connectionContainer, null);
		msservText.setToolTipText(Messages.ConnectionPropertySection_MsservToolTip);
		msservText.setLayoutData(LayoutUtil.entryLayoutData(mshostText));

		CLabel msgservLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_MsservLabel);
		msgservLbl.setLayoutData(LayoutUtil.labelLayoutData(msservText));
		msgservLbl.setAlignment(SWT.RIGHT);

		gwhostText = widgetFactory.createText(connectionContainer, null);
		gwhostText.setToolTipText(Messages.ConnectionPropertySection_GwhostToolTip);
		gwhostText.setLayoutData(LayoutUtil.entryLayoutData(msservText));

		CLabel gwhostLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_GwhostLabel);
		gwhostLbl.setLayoutData(LayoutUtil.labelLayoutData(gwhostText));
		gwhostLbl.setAlignment(SWT.RIGHT);

		gwservText = widgetFactory.createText(connectionContainer, null);
		gwservText.setToolTipText(Messages.ConnectionPropertySection_GwservToolTip);
		gwservText.setLayoutData(LayoutUtil.entryLayoutData(gwhostText));

		CLabel gwservLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_GwservLabel);
		gwservLbl.setLayoutData(LayoutUtil.labelLayoutData(gwservText));
		gwservLbl.setAlignment(SWT.RIGHT);

		r3nameText = widgetFactory.createText(connectionContainer, null);
		r3nameText.setToolTipText(Messages.ConnectionPropertySection_R3nameToolTip);
		r3nameText.setLayoutData(LayoutUtil.entryLayoutData(gwservText));

		CLabel r3nameLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_R3nameLabel);
		r3nameLbl.setLayoutData(LayoutUtil.labelLayoutData(r3nameText));
		r3nameLbl.setAlignment(SWT.RIGHT);

		groupText = widgetFactory.createText(connectionContainer, null);
		groupText.setToolTipText(Messages.ConnectionPropertySection_GroupToolTip);
		groupText.setLayoutData(LayoutUtil.entryLayoutData(r3nameText));

		CLabel groupLbl = widgetFactory.createCLabel(connectionContainer, Messages.ConnectionPropertySection_GroupLabel);
		groupLbl.setLayoutData(LayoutUtil.labelLayoutData(groupText));
		groupLbl.setAlignment(SWT.RIGHT);

	}

	/**
	 * @param bindingContext
	 * @param editingDomain
	 * @param destinationDataStoreEntry
	 */
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, DestinationDataStoreEntryImpl destinationDataStoreEntry) {
		//
		IObservableValue observeTextSysnrText2ObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, sysnrText2);
		IObservableValue managedConnectionFactorySysnrObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SYSNR)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setBeforeSetValidator(new SystemNumberValidator());
		Binding sysnrBinding2 = bindingContext.bindValue(observeTextSysnrText2ObserveWidget, managedConnectionFactorySysnrObserveValue, strategy_2, null);
		//
		IObservableValue observeTextSaprouterTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, saprouterText);
		IObservableValue managedConnectionFactorySaprouterObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SAPROUTER)).observe(destinationDataStoreEntry);
		UpdateValueStrategy saprouterStrategy = new UpdateValueStrategy();
		saprouterStrategy.setBeforeSetValidator(new SapRouterStringValidator());
		Binding saprouterBinding = bindingContext.bindValue(observeTextSaprouterTextObserveWidget, managedConnectionFactorySaprouterObserveValue, saprouterStrategy, null);
		//
		IObservableValue observeTextAshostText2ObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, ashostText2);
		IObservableValue managedConnectionFactoryAshostObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ASHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextAshostText2ObserveWidget, managedConnectionFactoryAshostObserveValue);
		//
		IObservableValue observeTextMshostTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, mshostText);
		IObservableValue managedConnectionFactoryMshostObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MSHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextMshostTextObserveWidget, managedConnectionFactoryMshostObserveValue);
		//
		IObservableValue observeTextMsservTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, msservText);
		IObservableValue managedConnectionFactoryMsservObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MSSERV)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextMsservTextObserveWidget, managedConnectionFactoryMsservObserveValue);
		//
		IObservableValue observeTextGwhostTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, gwhostText);
		IObservableValue managedConnectionFactoryGwhostObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GWHOST)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextGwhostTextObserveWidget, managedConnectionFactoryGwhostObserveValue);
		//
		IObservableValue observeTextGwservTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, gwservText);
		IObservableValue managedConnectionFactoryGwservObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GWSERV)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextGwservTextObserveWidget, managedConnectionFactoryGwservObserveValue);
		//
		IObservableValue observeTextR3nameTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, r3nameText);
		IObservableValue managedConnectionFactoryR3nameObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__R3NAME)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextR3nameTextObserveWidget, managedConnectionFactoryR3nameObserveValue);
		//
		IObservableValue observeTextGroupTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, groupText);
		IObservableValue managedConnectionFactoryGroupObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GROUP)).observe(destinationDataStoreEntry);
		bindingContext.bindValue(observeTextGroupTextObserveWidget, managedConnectionFactoryGroupObserveValue);

		ControlDecorationSupport.create(saprouterBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(sysnrBinding2, SWT.TOP | SWT.LEFT);

	}

}

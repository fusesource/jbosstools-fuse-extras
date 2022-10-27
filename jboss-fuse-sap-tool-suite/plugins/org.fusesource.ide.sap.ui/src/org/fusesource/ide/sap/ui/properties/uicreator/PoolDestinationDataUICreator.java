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
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class PoolDestinationDataUICreator implements IDestinationDataUICreator {

	private Text peakLimitText;
	private Text poolCapacityText;
	private Text expirationTimeText;
	private Text expirationCheckPeriodText;
	private Text maxGetClientTimeText;

	@Override
	public void createControls(Composite poolContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		peakLimitText = widgetFactory.createText(poolContainer, null);
		HelpDecorator.createHelpDecoration(Messages.PoolPropertySection_PeakLimitToolTip, peakLimitText);
		peakLimitText.setLayoutData(LayoutUtil.firstEntryLayoutData());

		CLabel peakLimitLbl = widgetFactory.createCLabel(poolContainer, Messages.PoolPropertySection_PeakLimitLabel);
		peakLimitLbl.setLayoutData(LayoutUtil.labelLayoutData(peakLimitText));
		peakLimitLbl.setAlignment(SWT.RIGHT);

		poolCapacityText = widgetFactory.createText(poolContainer, null);
		HelpDecorator.createHelpDecoration(Messages.PoolPropertySection_PoolCapacityToolTip, poolCapacityText);
		poolCapacityText.setLayoutData(LayoutUtil.entryLayoutData(peakLimitText));

		CLabel poolCapacityLbl = widgetFactory.createCLabel(poolContainer, Messages.PoolPropertySection_PoolCapacityLabel);
		poolCapacityLbl.setLayoutData(LayoutUtil.labelLayoutData(poolCapacityText));
		poolCapacityLbl.setAlignment(SWT.RIGHT);

		expirationTimeText = widgetFactory.createText(poolContainer, null);
		HelpDecorator.createHelpDecoration(Messages.PoolPropertySection_ExpirationTimeToolTip, expirationTimeText);
		expirationTimeText.setLayoutData(LayoutUtil.entryLayoutData(poolCapacityText));

		CLabel expirationTimeLbl = widgetFactory.createCLabel(poolContainer, Messages.PoolPropertySection_ExpirationTimeLabel);
		expirationTimeLbl.setLayoutData(LayoutUtil.labelLayoutData(expirationTimeText));
		expirationTimeLbl.setAlignment(SWT.RIGHT);

		expirationCheckPeriodText = widgetFactory.createText(poolContainer, null);
		HelpDecorator.createHelpDecoration(Messages.PoolPropertySection_ExpirationCheckPeriodToolTip, expirationCheckPeriodText);
		expirationCheckPeriodText.setLayoutData(LayoutUtil.entryLayoutData(expirationTimeText));

		CLabel expirationCheckPeriodLbl = widgetFactory.createCLabel(poolContainer, Messages.PoolPropertySection_ExpirationCheckPeriodLabel);
		expirationCheckPeriodLbl.setLayoutData(LayoutUtil.labelLayoutData(expirationCheckPeriodText));
		expirationCheckPeriodLbl.setAlignment(SWT.RIGHT);

		maxGetClientTimeText = widgetFactory.createText(poolContainer, null);
		HelpDecorator.createHelpDecoration(Messages.PoolPropertySection_MaxGetClientTimeToolTip, maxGetClientTimeText);
		maxGetClientTimeText.setLayoutData(LayoutUtil.entryLayoutData(expirationCheckPeriodText));

		CLabel maxGetClientTimeLbl = widgetFactory.createCLabel(poolContainer, Messages.PoolPropertySection_MaxGetClientTimeLabel);
		maxGetClientTimeLbl.setLayoutData(LayoutUtil.labelLayoutData(maxGetClientTimeText));
		maxGetClientTimeLbl.setAlignment(SWT.RIGHT);
	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, DestinationDataStoreEntryImpl destinationDataStoreEntry) {
		//
		IObservableValue observeTextPeakLimitTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, peakLimitText);
		IObservableValue managedConnectionFactoryPeakLimitObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PEAK_LIMIT)).observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_PeakLimitValidator));
		Binding peakLimitBinding = bindingContext.bindValue(observeTextPeakLimitTextObserveWidget, managedConnectionFactoryPeakLimitObserveValue, strategy_8, null);
		//
		IObservableValue observeTextPoolCapacityTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, poolCapacityText);
		IObservableValue managedConnectionFactoryPoolCapacityObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__POOL_CAPACITY))
				.observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_PoolCapacityValidator));
		Binding poolCapacityBinding = bindingContext.bindValue(observeTextPoolCapacityTextObserveWidget, managedConnectionFactoryPoolCapacityObserveValue, strategy_9, null);
		//
		IObservableValue observeTextExpirationTimeTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, expirationTimeText);
		IObservableValue managedConnectionFactoryExpirationTimeObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__EXPIRATION_TIME))
				.observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_ExpirationTimeValidator));
		Binding expirationTimeBinding = bindingContext.bindValue(observeTextExpirationTimeTextObserveWidget, managedConnectionFactoryExpirationTimeObserveValue, strategy_10, null);
		//
		IObservableValue observeTextExpirationCheckPeriodTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, expirationCheckPeriodText);
		IObservableValue managedConnectionFactoryExpirationPeriodObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__EXPIRATION_PERIOD))
				.observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_ExpirationCheckPeriodValidator));
		Binding expirationPeriodBinding = bindingContext.bindValue(observeTextExpirationCheckPeriodTextObserveWidget, managedConnectionFactoryExpirationPeriodObserveValue,
				strategy_11, null);
		//
		IObservableValue observeTextMaxGetClientTimeTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, maxGetClientTimeText);
		IObservableValue managedConnectionFactoryMaxGetTimeObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MAX_GET_TIME))
				.observe(destinationDataStoreEntry);
		UpdateValueStrategy strategy_12 = new UpdateValueStrategy();
		strategy_12.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_MaxGetClientTimeValidator));
		Binding maxGetTimeBinding = bindingContext.bindValue(observeTextMaxGetClientTimeTextObserveWidget, managedConnectionFactoryMaxGetTimeObserveValue, strategy_12, null);

		ControlDecorationSupport.create(peakLimitBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(poolCapacityBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(expirationTimeBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(expirationPeriodBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(maxGetTimeBinding, SWT.TOP | SWT.LEFT);
	}

}

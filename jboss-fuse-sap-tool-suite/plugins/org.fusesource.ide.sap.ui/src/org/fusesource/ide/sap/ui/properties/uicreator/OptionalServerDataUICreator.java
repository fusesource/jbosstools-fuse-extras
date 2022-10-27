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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.util.LayoutUtil;
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;
import org.fusesource.ide.sap.ui.validator.SapRouterStringValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class OptionalServerDataUICreator implements IServerDataUICreator {

	private Button traceBtn;
	private Text saprouterText;
	private Text workerThreadCountText;
	private Text workerThreadMinCountText;
	private Text maxStartupDelayText;
	private Text repositoryMapText;

	@Override
	public void createControls(Composite optionalContainer, TabbedPropertySheetWidgetFactory widgetFactory) {
		traceBtn = widgetFactory.createButton(optionalContainer, Messages.OptionalServerPropertySection_TraceLabel, SWT.CHECK);
		traceBtn.setToolTipText(Messages.OptionalServerPropertySection_TraceToolTip);
		traceBtn.setLayoutData(LayoutUtil.firstEntryLayoutData());

		saprouterText = widgetFactory.createText(optionalContainer, null);
		HelpDecorator.createHelpDecoration(Messages.OptionalServerPropertySection_SaprouterToolTip, saprouterText);
		saprouterText.setLayoutData(LayoutUtil.entryLayoutData(traceBtn));

		CLabel saprouterLbl = widgetFactory.createCLabel(optionalContainer, Messages.OptionalServerPropertySection_SaprouterLabel);
		saprouterLbl.setLayoutData(LayoutUtil.labelLayoutData(saprouterText));
		saprouterLbl.setAlignment(SWT.RIGHT);

		workerThreadCountText = widgetFactory.createText(optionalContainer, null);
		HelpDecorator.createHelpDecoration(Messages.OptionalServerPropertySection_WorkerThreadCountToolTip, workerThreadCountText);
		workerThreadCountText.setLayoutData(LayoutUtil.entryLayoutData(saprouterText));

		CLabel workerThreadCountLbl = widgetFactory.createCLabel(optionalContainer, Messages.OptionalServerPropertySection_WorkerThreadCountLabel);
		workerThreadCountLbl.setLayoutData(LayoutUtil.labelLayoutData(workerThreadCountText));
		workerThreadCountLbl.setAlignment(SWT.RIGHT);

		workerThreadMinCountText = widgetFactory.createText(optionalContainer, null);
		HelpDecorator.createHelpDecoration(Messages.OptionalServerPropertySection_WorkerThreadMinCountToolTip, workerThreadMinCountText);
		workerThreadMinCountText.setLayoutData(LayoutUtil.entryLayoutData(workerThreadCountText));

		CLabel workerThreadMinCountLbl = widgetFactory.createCLabel(optionalContainer, Messages.OptionalServerPropertySection_WorkerThreadMinCountLabel);
		workerThreadMinCountLbl.setLayoutData(LayoutUtil.labelLayoutData(workerThreadMinCountText));
		workerThreadMinCountLbl.setAlignment(SWT.RIGHT);

		maxStartupDelayText = widgetFactory.createText(optionalContainer, null);
		HelpDecorator.createHelpDecoration(Messages.OptionalServerPropertySection_MaxStartupDelayToolTip, maxStartupDelayText);
		maxStartupDelayText.setLayoutData(LayoutUtil.entryLayoutData(workerThreadMinCountText));

		CLabel maxStartupDelayLbl = widgetFactory.createCLabel(optionalContainer, Messages.OptionalServerPropertySection_MaxStartupDelayLabel);
		maxStartupDelayLbl.setLayoutData(LayoutUtil.labelLayoutData(maxStartupDelayText));
		maxStartupDelayLbl.setAlignment(SWT.RIGHT);

		repositoryMapText = widgetFactory.createText(optionalContainer, null);
		HelpDecorator.createHelpDecoration(Messages.OptionalServerPropertySection_RepoistoryMapToolTip, repositoryMapText);
		repositoryMapText.setLayoutData(LayoutUtil.entryLayoutData(maxStartupDelayText));

		CLabel repositoryMapLbl = widgetFactory.createCLabel(optionalContainer, Messages.OptionalServerPropertySection_RepoistoryMapLabel);
		repositoryMapLbl.setLayoutData(LayoutUtil.labelLayoutData(repositoryMapText));
		repositoryMapLbl.setAlignment(SWT.RIGHT);
	}

	@Override
	public void initDataBindings(DataBindingContext bindingContext, EditingDomain editingDomain, ServerDataStoreEntryImpl serverDataStoreEntry) {
		//
		IObservableValue observeSelectionTraceBtnObserveWidget = WidgetProperties.buttonSelection().observe(traceBtn);
		IObservableValue managedConnectionFactoryTraceObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__TRACE)).observe(serverDataStoreEntry);
		UpdateValueStrategy traceStrategy = new UpdateValueStrategy();
		traceStrategy.setConverter(new Boolean2StringConverter());
		UpdateValueStrategy traceModelStrategy = new UpdateValueStrategy();
		traceModelStrategy.setConverter(new String2BooleanConverter());
		bindingContext.bindValue(observeSelectionTraceBtnObserveWidget, managedConnectionFactoryTraceObserveValue, traceStrategy, traceModelStrategy);
		//
		IObservableValue observeTextSapRouterTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, saprouterText);
		IObservableValue serverDataSapRouterObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SAPROUTER)).observe(serverDataStoreEntry);
		UpdateValueStrategy sapRouterStrategy = new UpdateValueStrategy();
		sapRouterStrategy.setBeforeSetValidator(new SapRouterStringValidator());
		Binding saprouterBinding = bindingContext.bindValue(observeTextSapRouterTextObserveWidget, serverDataSapRouterObserveValue, sapRouterStrategy, null);
		//
		IObservableValue observeTextWorkerThreadCountTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, workerThreadCountText);
		IObservableValue serverDataWorkerThreadCountObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__WORKER_THREAD_COUNT)).observe(serverDataStoreEntry);
		UpdateValueStrategy workerThreadCountStrategy = new UpdateValueStrategy();
		workerThreadCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_WorkerThreadCountValidator));
		Binding workerThreadCountBinding = bindingContext.bindValue(observeTextWorkerThreadCountTextObserveWidget, serverDataWorkerThreadCountObserveValue,
				workerThreadCountStrategy, null);
		//
		IObservableValue observeTextWorkerThreadMinCountTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, workerThreadMinCountText);
		IObservableValue serverDataWorkerThreadMinCountObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__WORKER_THREAD_MIN_COUNT)).observe(serverDataStoreEntry);
		UpdateValueStrategy workerThreadMinCountStrategy = new UpdateValueStrategy();
		workerThreadMinCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_WorkerThreadMinCountValidator));
		Binding workerThreadMinCountBinding = bindingContext.bindValue(observeTextWorkerThreadMinCountTextObserveWidget, serverDataWorkerThreadMinCountObserveValue,
				workerThreadMinCountStrategy, null);
		//
		IObservableValue observeTextMaxStartupDelayTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, maxStartupDelayText);
		IObservableValue serverDataMaxStartupDelayObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__MAX_START_UP_DELAY)).observe(serverDataStoreEntry);
		UpdateValueStrategy maxStartupDelayStrategy = new UpdateValueStrategy();
		maxStartupDelayStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_MaxStartupDelayValidator));
		Binding maxStartupDelayBinding = bindingContext.bindValue(observeTextMaxStartupDelayTextObserveWidget, serverDataMaxStartupDelayObserveValue, maxStartupDelayStrategy,
				null);
		//
		IObservableValue observeRepositoryMapTextObserveWidget = WidgetProperties.text(SWT.Modify).observeDelayed(300, repositoryMapText);
		IObservableValue serverRepositoryMapObserveValue = EMFEditProperties
				.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__REPOSITORY_MAP)).observe(serverDataStoreEntry);
		bindingContext.bindValue(observeRepositoryMapTextObserveWidget, serverRepositoryMapObserveValue);

		ControlDecorationSupport.create(saprouterBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(workerThreadCountBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(workerThreadMinCountBinding, SWT.TOP | SWT.LEFT);
		ControlDecorationSupport.create(maxStartupDelayBinding, SWT.TOP | SWT.LEFT);

	}

}

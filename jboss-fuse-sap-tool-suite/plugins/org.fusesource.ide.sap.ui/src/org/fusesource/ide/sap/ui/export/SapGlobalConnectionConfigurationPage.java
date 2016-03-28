/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/
package org.fusesource.ide.sap.ui.export;

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage.Literals;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.Boolean2StringConverter;
import org.fusesource.ide.sap.ui.converter.SncQos2SncQosComboSelectionConverter;
import org.fusesource.ide.sap.ui.converter.SncQosComboSelection2SncQosConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.dialog.DestinationDialog;
import org.fusesource.ide.sap.ui.dialog.ServerDialog;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;
import org.fusesource.ide.sap.ui.edit.idoc.IdocItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.edit.rfc.RfcItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;
import org.fusesource.ide.sap.ui.validator.SapRouterStringValidator;

/**
 * SAP Global Connection Configuration Page for editing SAP Global Configuration
 * 
 * @author William Collins <punkhornsw@gmail.com>
 *
 */
public class SapGlobalConnectionConfigurationPage extends WizardPage implements ISelectionChangedListener {
	

	private class NewDestinationAction extends Action {
		
		public NewDestinationAction() {
			super(Messages.SapGlobalConnectionConfigurationPage_NewDestination,Activator.getDefault().getImageRegistry().getDescriptor(Activator.DESTINATION_DATA_STORE_ENTRY_IMAGE));
		}
		
		@Override
		public void run() {
			Command createDestinationDataStoreEntryCommand = null;
			Command createDestinationDataCommand = null;
			CompoundCommand command = null;
			DestinationDataStore destinationDataStore;
			DestinationDataStoreEntryImpl destinationDataStoreEntry = null;
			DestinationData destinationData = null;

			if (selection.size() == 1) {
				Object obj = selection.getFirstElement();
				if (obj instanceof DestinationDataStore) {
					destinationDataStore = (DestinationDataStore) obj;
					if (editingDomain != null) {
						Collection<?> descriptors = editingDomain.getNewChildDescriptors(destinationDataStore, null);
						for (Object descriptor : descriptors) {
							CommandParameter parameter = (CommandParameter) descriptor;
							if (parameter.getFeature() == RfcPackage.Literals.DESTINATION_DATA_STORE__ENTRIES) {
								destinationDataStoreEntry = (DestinationDataStoreEntryImpl) parameter.getValue();
								createDestinationDataStoreEntryCommand = CreateChildCommand.create(editingDomain,
										destinationDataStore, descriptor,
										Collections.singletonList(destinationDataStore));
								continue;
							} else if (parameter
									.getFeature() == RfcPackage.Literals.DESTINATION_DATA_STORE__DESTINATION_DATA) {
								destinationData = (DestinationData) parameter.getValue();
								createDestinationDataCommand = CreateChildCommand.create(editingDomain,
										destinationDataStore, descriptor,
										Collections.singletonList(destinationDataStore));
								continue;
							}

						}
						command = new CompoundCommand();
						command.append(createDestinationDataCommand);
						command.append(SetCommand.create(editingDomain, destinationDataStoreEntry,
								RfcPackage.Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, destinationData));
						command.append(createDestinationDataStoreEntryCommand);
						((TransactionalCommandStack)editingDomain.getCommandStack()).begin();
						DestinationDialog newNameDialog = new DestinationDialog(getShell(), DestinationDialog.Type.CREATE, editingDomain, destinationDataStore, destinationDataStoreEntry);
						int status = newNameDialog.open();
						if (status != Window.OK) {
					    	((TransactionalCommandStack)editingDomain.getCommandStack()).rollback();
					    	return;
						}
						editingDomain.getCommandStack().execute(command);
					    ((TransactionalCommandStack)editingDomain.getCommandStack()).commit();
					}
				}
			}
		}

	}

	private class NewServerAction extends Action {
		public NewServerAction() {
			super(Messages.SapGlobalConnectionConfigurationPage_NewServer,Activator.getDefault().getImageRegistry().getDescriptor(Activator.SERVER_DATA_STORE_ENTRY_IMAGE));
		}
		
		@Override
		public void run() {
			Command createServerDataStoreEntryCommand = null;
			Command createServerDataCommand = null;
			CompoundCommand command = null;
			ServerDataStore serverDataStore;
			ServerDataStoreEntryImpl serverDataStoreEntry = null;
			ServerData serverData = null;

			if (selection.size() == 1) {
				Object obj = selection.getFirstElement();
				if (obj instanceof ServerDataStore) {
					serverDataStore = (ServerDataStore) obj;
					if (editingDomain != null) {
						Collection<?> descriptors = editingDomain.getNewChildDescriptors(serverDataStore, null);
						for (Object descriptor : descriptors) {
							CommandParameter parameter = (CommandParameter) descriptor;
							if (parameter.getFeature() == RfcPackage.Literals.SERVER_DATA_STORE__ENTRIES) {
								serverDataStoreEntry = (ServerDataStoreEntryImpl) parameter.getValue();
								createServerDataStoreEntryCommand = CreateChildCommand.create(editingDomain,
										serverDataStore, descriptor,
										Collections.singletonList(serverDataStore));
								continue;
							} else if (parameter
									.getFeature() == RfcPackage.Literals.SERVER_DATA_STORE__SERVER_DATA) {
								serverData = (ServerData) parameter.getValue();
								createServerDataCommand = CreateChildCommand.create(editingDomain,
										serverDataStore, descriptor,
										Collections.singletonList(serverDataStore));
								continue;
							}

						}
						command = new CompoundCommand();
						command.append(createServerDataCommand);
						command.append(SetCommand.create(editingDomain, serverDataStoreEntry,
								RfcPackage.Literals.SERVER_DATA_STORE_ENTRY__VALUE, serverData));
						command.append(createServerDataStoreEntryCommand);
						((TransactionalCommandStack)editingDomain.getCommandStack()).begin();
						ServerDialog newNameDialog = new ServerDialog(getShell(), ServerDialog.Type.CREATE, editingDomain, serverDataStore, serverDataStoreEntry);
						int status = newNameDialog.open();
						if (status != Window.OK) {
					    	((TransactionalCommandStack)editingDomain.getCommandStack()).rollback();
					    	return;
						}
						editingDomain.getCommandStack().execute(command);
					    ((TransactionalCommandStack)editingDomain.getCommandStack()).commit();
					}
				}
			}
		}
		
	}
	
	private class DeleteAction extends Action {
		public DeleteAction() {
			super(Messages.SapGlobalConnectionConfigurationPage_Delete, sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		}
		
		@Override
		public void run() {
			if (selection.size() == 1) {
				Object obj = selection.getFirstElement();
				if (obj instanceof EObject) {
					EObject eObject = (EObject) obj;
					if (editingDomain != null) {
						CompoundCommand command;
						Command removeValueCommand = null;
						Command deleteEntryCommand = DeleteCommand.create(editingDomain, Collections.singletonList(eObject));
						if (eObject instanceof DestinationDataStoreEntryImpl) {
							DestinationData destinationData = ((DestinationDataStoreEntryImpl)eObject).getValue();
							removeValueCommand = RemoveCommand.create(editingDomain, destinationData.eContainer(), RfcPackage.Literals.DESTINATION_DATA_STORE__DESTINATION_DATA, Collections.singletonList(destinationData));
						} else if (eObject instanceof ServerDataStoreEntryImpl) {
							ServerData serverData = ((ServerDataStoreEntryImpl)eObject).getValue();
							removeValueCommand = RemoveCommand.create(editingDomain, serverData.eContainer(), RfcPackage.Literals.SERVER_DATA_STORE__SERVER_DATA, Collections.singletonList(serverData));
						}
						command = new CompoundCommand();
						command.append(deleteEntryCommand);
						command.append(removeValueCommand);
						editingDomain.getCommandStack().execute(command);
					}
				}
			}
		}
	}
	
	private class ServerDataProperties {
		
		protected ServerDataStoreEntryImpl serverDataStoreEntry;
		protected EditingDomain editingDomain;

		private Text gwhostText;
		private Text gwservText;
		private Text progidText;
		private Text repositoryDestinationText;
		private Text connectionCountText;
		private Button traceBtn;
		private Text saprouterText;
		private Text workerThreadCountText;
		private Text workerThreadMinCountText;
		private Text maxStartupDelayText;
		private Text repositoryMapText;
		private Button sncModeBtn;
		private CCombo sncQopCombo;
		private Text sncMynameText;
		private Text sncLibraryText;

		private DataBindingContext bindingContext;

		private Binding connectionCountBinding;
		private Binding workerThreadCountBinding;
		private Binding workerThreadMinCountBinding;
		private Binding maxStartupDelayBinding;
		private Binding saprouterBinding;

		public void createControl() {

			serverDataTabFolder = new CTabFolder(properties, SWT.BORDER);
			serverDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			
			////
			//  Server Data Mandatory Properties Tab
			//
			
			CTabItem serverDataMandatoryItem = new CTabItem(serverDataTabFolder, SWT.NONE);
			serverDataMandatoryItem.setText(Messages.SapGlobalConnectionConfigurationPage_ServerDataMandatoryItemTitle);
			serverDataTabFolder.setSelection(serverDataMandatoryItem);
			
			Composite mandatoryContainer = new Composite(serverDataTabFolder, SWT.NONE);
			serverDataMandatoryItem.setControl(mandatoryContainer);
			mandatoryContainer.setLayout(compositeFormLayout());
			
			gwhostText = new Text(mandatoryContainer, SWT.BORDER);
			gwhostText.setLayoutData(firstEntryLayoutData());
			gwhostText.setToolTipText(Messages.MandatoryServerPropertySection_GwhostToolTip);
			
			CLabel gwhostLbl = new CLabel(mandatoryContainer, SWT.NONE);
			gwhostLbl.setLayoutData(labelLayoutData(gwhostText));
			gwhostLbl.setText(Messages.MandatoryServerPropertySection_GwhostLabel);
			gwhostLbl.setAlignment(SWT.RIGHT);
			
			gwservText = new Text(mandatoryContainer, SWT.BORDER);
			gwservText.setToolTipText(Messages.MandatoryServerPropertySection_GwservToolTip);
			gwservText.setLayoutData(entryLayoutData(gwhostText));
			
			CLabel gwservLbl = new CLabel(mandatoryContainer, SWT.NONE);
			gwservLbl.setText(Messages.MandatoryServerPropertySection_GwservLabel);
			gwservLbl.setLayoutData(labelLayoutData(gwservText));
			gwservLbl.setAlignment(SWT.RIGHT);
			
			progidText = new Text(mandatoryContainer, SWT.BORDER);
			progidText.setToolTipText(Messages.MandatoryServerPropertySection_ProgidToolTip);
			progidText.setLayoutData(entryLayoutData(gwservText));
			
			CLabel progidLbl = new CLabel(mandatoryContainer, SWT.NONE);
			progidLbl.setText(Messages.MandatoryServerPropertySection_ProgidLabel);
			progidLbl.setLayoutData(labelLayoutData(progidText));
			progidLbl.setAlignment(SWT.RIGHT);
			
			repositoryDestinationText = new Text(mandatoryContainer, SWT.BORDER);
			repositoryDestinationText.setToolTipText(Messages.OptionalServerPropertySection_RepositoryDestinationToolTip);
			repositoryDestinationText.setLayoutData(entryLayoutData(progidText));

			CLabel repositoryDestinationLbl = new CLabel(mandatoryContainer, SWT.NONE);
			repositoryDestinationLbl.setText(Messages.OptionalServerPropertySection_RepositoryDestinationLabel);
			repositoryDestinationLbl.setLayoutData(labelLayoutData(repositoryDestinationText));
			repositoryDestinationLbl.setAlignment(SWT.RIGHT);
			
			connectionCountText = new Text(mandatoryContainer, SWT.BORDER);
			connectionCountText.setToolTipText(Messages.MandatoryServerPropertySection_ConnectionCountToolTip);
			connectionCountText.setLayoutData(entryLayoutData(repositoryDestinationText));
			new Label(mandatoryContainer, SWT.NONE);
			new Label(mandatoryContainer, SWT.NONE);
		
			CLabel connectionCountLbl = new CLabel(mandatoryContainer, SWT.NONE);
			connectionCountLbl.setText(Messages.MandatoryServerPropertySection_ConnectionCountLabel);
			connectionCountLbl.setLayoutData(labelLayoutData(connectionCountText));
			connectionCountLbl.setAlignment(SWT.RIGHT);
			
			//
			////
			
			////
			//  Server Data Optional Properties Tab
			//
			
			CTabItem serverDataOptionalItem = new CTabItem(serverDataTabFolder, SWT.NONE);
			serverDataOptionalItem.setText(Messages.SapGlobalConnectionConfigurationPage_ServerDataOptionalItemTitle);

			Composite optionalContainer = new Composite(serverDataTabFolder, SWT.NONE);
			serverDataOptionalItem.setControl(optionalContainer);
			optionalContainer.setLayout(compositeFormLayout());
			
			traceBtn = new Button(optionalContainer, SWT.FLAT | SWT.CHECK);
			traceBtn.setText(Messages.OptionalServerPropertySection_TraceLabel);
			traceBtn.setToolTipText(Messages.OptionalServerPropertySection_TraceToolTip);
			traceBtn.setLayoutData(firstEntryLayoutData());

			saprouterText = new Text(optionalContainer, SWT.BORDER);
			saprouterText.setToolTipText(Messages.OptionalServerPropertySection_SaprouterToolTip);
			saprouterText.setLayoutData(entryLayoutData(traceBtn));

			CLabel saprouterLbl = new CLabel(optionalContainer, SWT.NONE);
			saprouterLbl.setText(Messages.OptionalServerPropertySection_SaprouterLabel);
			saprouterLbl.setLayoutData(labelLayoutData(saprouterText));
			saprouterLbl.setAlignment(SWT.RIGHT);
			
			workerThreadCountText = new Text(optionalContainer, SWT.BORDER);
			workerThreadCountText.setToolTipText(Messages.OptionalServerPropertySection_WorkerThreadCountToolTip);
			workerThreadCountText.setLayoutData(entryLayoutData(saprouterText));

			CLabel workerThreadCountLbl = new CLabel(optionalContainer, SWT.NONE);
			workerThreadCountLbl.setText(Messages.OptionalServerPropertySection_WorkerThreadCountLabel);
			workerThreadCountLbl.setLayoutData(labelLayoutData(workerThreadCountText));
			workerThreadCountLbl.setAlignment(SWT.RIGHT);
			
			workerThreadMinCountText = new Text(optionalContainer, SWT.BORDER);
			workerThreadMinCountText.setToolTipText(Messages.OptionalServerPropertySection_WorkerThreadMinCountToolTip);
			workerThreadMinCountText.setLayoutData(entryLayoutData(workerThreadCountText));

			CLabel workerThreadMinCountLbl = new CLabel(optionalContainer, SWT.NONE);
			workerThreadMinCountLbl.setText(Messages.OptionalServerPropertySection_WorkerThreadMinCountLabel);
			workerThreadMinCountLbl.setLayoutData(labelLayoutData(workerThreadMinCountText));
			workerThreadMinCountLbl.setAlignment(SWT.RIGHT);
			
			maxStartupDelayText = new Text(optionalContainer, SWT.BORDER);
			maxStartupDelayText.setToolTipText(Messages.OptionalServerPropertySection_MaxStartupDelayToolTip);
			maxStartupDelayText.setLayoutData(entryLayoutData(workerThreadMinCountText));

			CLabel maxStartupDelayLbl =  new CLabel(optionalContainer, SWT.NONE);
			maxStartupDelayLbl.setText(Messages.OptionalServerPropertySection_MaxStartupDelayLabel);
			maxStartupDelayLbl.setLayoutData(labelLayoutData(maxStartupDelayText));
			maxStartupDelayLbl.setAlignment(SWT.RIGHT);
			
			repositoryMapText = new Text(optionalContainer, SWT.BORDER);
			repositoryMapText.setToolTipText(Messages.OptionalServerPropertySection_RepoistoryMapToolTip);
			repositoryMapText.setLayoutData(entryLayoutData(maxStartupDelayText));

			CLabel repositoryMapLbl =  new CLabel(optionalContainer, SWT.NONE);
			repositoryMapLbl.setText(Messages.OptionalServerPropertySection_RepoistoryMapLabel);
			repositoryMapLbl.setLayoutData(labelLayoutData(repositoryMapText));
			repositoryMapLbl.setAlignment(SWT.RIGHT);
			
			//
			////

			////
			//  Server Data SNC Properties Tab
			//
			
			CTabItem serverDataSncItem = new CTabItem(serverDataTabFolder, SWT.NONE);
			serverDataSncItem.setText(Messages.SapGlobalConnectionConfigurationPage_ServerDataSncItemTitle);
			
			Composite sncContainer = new Composite(serverDataTabFolder, SWT.NONE);
			serverDataSncItem.setControl(sncContainer);
			sncContainer.setLayout(compositeFormLayout());

			sncModeBtn = new Button(sncContainer, SWT.FLAT | SWT.CHECK);
			sncModeBtn.setText(Messages.SncServerPropertySection_SncModeLabel);
			sncModeBtn.setToolTipText(Messages.SncServerPropertySection_SncModeToolTip);
			sncModeBtn.setLayoutData(firstEntryLayoutData());
			
			sncQopCombo = new CCombo(sncContainer, SWT.READ_ONLY | SWT.BORDER);
			sncQopCombo.setToolTipText(Messages.SncServerPropertySection_SncQopToolTip);
			sncQopCombo.setItems(new String[] {"", Messages.SncServerPropertySection_SncSecurityLevel1Label, Messages.SncServerPropertySection_SncSecurityLevel2Label, Messages.SncServerPropertySection_SncSecurityLevel3Label, Messages.SncServerPropertySection_SncSecurityLevel8Label, Messages.SncServerPropertySection_SncSecurityLevel9Label}); //$NON-NLS-1$
			sncQopCombo.setLayoutData(entryLayoutData(sncModeBtn));
			sncQopCombo.select(0);
			
			CLabel sncQopLbl = new CLabel(sncContainer, SWT.NONE);
			sncQopLbl.setText(Messages.SncServerPropertySection_SncQopLabel);
			sncQopLbl.setLayoutData(labelLayoutData(sncQopCombo));
			sncQopLbl.setAlignment(SWT.RIGHT);

			sncMynameText = new Text(sncContainer, SWT.BORDER);
			sncMynameText.setToolTipText(Messages.SncServerPropertySection_SncMynameToolTip);
			sncMynameText.setLayoutData(entryLayoutData(sncQopCombo));
			
			CLabel sncMynameLbl = new CLabel(sncContainer, SWT.NONE);
			sncMynameLbl.setText(Messages.SncServerPropertySection_SncMynameLabel);
			sncMynameLbl.setLayoutData(labelLayoutData(sncMynameText));
			sncMynameLbl.setAlignment(SWT.RIGHT);

			sncLibraryText = new Text(sncContainer, SWT.BORDER);
			sncLibraryText.setToolTipText(Messages.SncServerPropertySection_SncLibraryToolTip);
			sncLibraryText.setLayoutData(entryLayoutData(sncMynameText));

			CLabel sncLibraryLbl = new CLabel(sncContainer, SWT.NONE);
			sncLibraryLbl.setText(Messages.SncServerPropertySection_SncLibraryLabel);
			sncLibraryLbl.setLayoutData(labelLayoutData(sncLibraryText));
			sncLibraryLbl.setAlignment(SWT.RIGHT);

			//
			////
			
		}
		
		public void setInput(ISelection selection) {
			Assert.isTrue(selection instanceof IStructuredSelection);
			Object input = ((IStructuredSelection)selection).getFirstElement();
			Assert.isTrue(input instanceof ServerDataStoreEntryImpl);
			serverDataStoreEntry = (ServerDataStoreEntryImpl) input;
			editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(serverDataStoreEntry);
			initDataBindings();
		}
		
		protected DataBindingContext initDataBindings() {
			if (bindingContext != null) {
				bindingContext.dispose();
				bindingContext = null;
			}
			bindingContext = new DataBindingContext();
			
			////
			//  Server Data Mandatory Properties Tab
			//
			//
			IObservableValue observeTextAshostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwhostText);
			IObservableValue destinationAshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__GWHOST)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeTextAshostTextObserveWidget, destinationAshostObserveValue, null, null);
			//
			IObservableValue observeTextSysnrTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwservText);
			IObservableValue destinationSysnrObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__GWSERV)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeTextSysnrTextObserveWidget, destinationSysnrObserveValue, null, null);
			//
			IObservableValue observeTextClientTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(progidText);
			IObservableValue destinationClientObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__PROGID)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeTextClientTextObserveWidget, destinationClientObserveValue, null, null);
			//
			IObservableValue observeRepositoryDestinationTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryDestinationText);
			IObservableValue serverRepositoryDestinationObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__REPOSITORY_DESTINATION)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeRepositoryDestinationTextObserveWidget, serverRepositoryDestinationObserveValue, null, null);
			//
			IObservableValue observeTextLanguageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(connectionCountText);
			IObservableValue destinationLangObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__CONNECTION_COUNT)).observe(serverDataStoreEntry);
			UpdateValueStrategy connectionCountStrategy = new UpdateValueStrategy();
			connectionCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.MandatoryServerPropertySection_ConnectionCountValidator));
			connectionCountBinding = bindingContext.bindValue(observeTextLanguageTextObserveWidget, destinationLangObserveValue, connectionCountStrategy, null);
			
			ControlDecorationSupport.create(connectionCountBinding, SWT.TOP | SWT.LEFT);
			//
			////

			////
			//  Server Data Optional Properties Tab
			//
			//
			IObservableValue observeSelectionTraceBtnObserveWidget = WidgetProperties.selection().observe(traceBtn);
			IObservableValue managedConnectionFactoryTraceObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__TRACE)).observe(serverDataStoreEntry);
			UpdateValueStrategy traceStrategy = new UpdateValueStrategy();
			traceStrategy.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy traceModelStrategy = new UpdateValueStrategy();
			traceModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionTraceBtnObserveWidget, managedConnectionFactoryTraceObserveValue, traceStrategy, traceModelStrategy);
			//
			IObservableValue observeTextSapRouterTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(saprouterText);
			IObservableValue serverDataSapRouterObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SAPROUTER)).observe(serverDataStoreEntry);
			UpdateValueStrategy sapRouterStrategy = new UpdateValueStrategy();
			sapRouterStrategy.setBeforeSetValidator(new SapRouterStringValidator());
			saprouterBinding = bindingContext.bindValue(observeTextSapRouterTextObserveWidget, serverDataSapRouterObserveValue, sapRouterStrategy, null);
			//
			IObservableValue observeTextWorkerThreadCountTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(workerThreadCountText);
			IObservableValue serverDataWorkerThreadCountObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__WORKER_THREAD_COUNT)).observe(serverDataStoreEntry);
			UpdateValueStrategy workerThreadCountStrategy = new UpdateValueStrategy();
			workerThreadCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_WorkerThreadCountValidator));
			workerThreadCountBinding = bindingContext.bindValue(observeTextWorkerThreadCountTextObserveWidget, serverDataWorkerThreadCountObserveValue, workerThreadCountStrategy, null);
			//
			IObservableValue observeTextWorkerThreadMinCountTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(workerThreadMinCountText);
			IObservableValue serverDataWorkerThreadMinCountObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__WORKER_THREAD_MIN_COUNT)).observe(serverDataStoreEntry);
			UpdateValueStrategy workerThreadMinCountStrategy = new UpdateValueStrategy();
			workerThreadMinCountStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_WorkerThreadMinCountValidator));
			workerThreadMinCountBinding = bindingContext.bindValue(observeTextWorkerThreadMinCountTextObserveWidget, serverDataWorkerThreadMinCountObserveValue, workerThreadMinCountStrategy, null);
			//
			IObservableValue observeTextMaxStartupDelayTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(maxStartupDelayText);
			IObservableValue serverDataMaxStartupDelayObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__MAX_START_UP_DELAY)).observe(serverDataStoreEntry);
			UpdateValueStrategy maxStartupDelayStrategy = new UpdateValueStrategy();
			maxStartupDelayStrategy.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.OptionalServerPropertySection_MaxStartupDelayValidator));
			maxStartupDelayBinding = bindingContext.bindValue(observeTextMaxStartupDelayTextObserveWidget, serverDataMaxStartupDelayObserveValue, maxStartupDelayStrategy, null);
			//
			IObservableValue observeRepositoryMapTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryMapText);
			IObservableValue serverRepositoryMapObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__REPOSITORY_MAP)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeRepositoryMapTextObserveWidget, serverRepositoryMapObserveValue, null, null);

			ControlDecorationSupport.create(saprouterBinding, SWT.TOP | SWT.LEFT);		
			ControlDecorationSupport.create(workerThreadCountBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(workerThreadMinCountBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(maxStartupDelayBinding, SWT.TOP | SWT.LEFT);
			//
			////
			
			////
			//  Server Data SNC Properties Tab
			//
			//
			IObservableValue observeSelectionSncModeBtnObserveWidget = WidgetProperties.selection().observe(sncModeBtn);
			IObservableValue managedConnectionFactorySncModeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_MODE)).observe(serverDataStoreEntry);
			UpdateValueStrategy strategy_13 = new UpdateValueStrategy();
			strategy_13.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy sncModeModelStrategy = new UpdateValueStrategy();
			sncModeModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionSncModeBtnObserveWidget, managedConnectionFactorySncModeObserveValue, strategy_13, sncModeModelStrategy);
			//
			IObservableValue observeSingleSelectionIndexSncQopComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(sncQopCombo);
			IObservableValue managedConnectionFactorySncQopObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_QOP)).observe(serverDataStoreEntry);
			UpdateValueStrategy sncQopStategy = new UpdateValueStrategy();
			sncQopStategy.setConverter(new SncQosComboSelection2SncQosConverter());
			UpdateValueStrategy sncQopModelStrategy = new UpdateValueStrategy();
			sncQopModelStrategy.setConverter(new SncQos2SncQosComboSelectionConverter());
			bindingContext.bindValue(observeSingleSelectionIndexSncQopComboObserveWidget, managedConnectionFactorySncQopObserveValue, sncQopStategy, sncQopModelStrategy);
			//
			IObservableValue observeTextSncMynameTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncMynameText);
			IObservableValue managedConnectionFactorySncMynameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_MYNAME)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeTextSncMynameTextObserveWidget, managedConnectionFactorySncMynameObserveValue, null, null);
			//
			IObservableValue observeTextSncLibraryTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncLibraryText);
			IObservableValue managedConnectionFactorySncLibraryObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.SERVER_DATA_STORE_ENTRY__VALUE, Literals.SERVER_DATA__SNC_LIB)).observe(serverDataStoreEntry);
			bindingContext.bindValue(observeTextSncLibraryTextObserveWidget, managedConnectionFactorySncLibraryObserveValue, null, null);
			//
			////
			
			return bindingContext; 
		}
	}

	/**
	 * This keeps track of the editing domain that is used to track all changes
	 * to the model.
	 */
	protected AdapterFactoryEditingDomain editingDomain;

	/**
	 * This is the one adapter factory used for providing views of the model.
	 */
	protected ComposedAdapterFactory adapterFactory;

	/**
	 * The configuration object containing connection configurations to SAP
	 */
	private SapConnectionConfiguration sapConnectionConfiguration;

	/**
	 * This listens to to viewer.
	 */
	protected ISelectionChangedListener selectionChangedListener;

	/**
	 * The viewer for view.
	 */
	private TreeViewer viewer;

	/**
	 * This is the action used to implement delete.
	 */
	protected DeleteAction deleteAction;

	/**
	 * This is the action to create new destination configurations
	 */
	protected NewDestinationAction newDestinationAction;

	/**
	 * This is the action to create new server configurations
	 */
	protected NewServerAction newServerAction;

	/**
	 * The current Selection in the viewer.
	 */
	protected IStructuredSelection selection;
	
	protected DataBindingContext context;
	
	protected ISharedImages sharedImages;

	private Composite properties;

	private StackLayout stackLayout;

	private CTabFolder sapConnectionConfigurationTabFolder;

	private CTabFolder destinationDataStoreTabFolder;

	private CTabFolder serverDataStoreTabFolder;

	private CTabFolder destinationDataTabFolder;

	private CTabFolder serverDataTabFolder;

	private ServerDataProperties serverDataProperties;

	public SapGlobalConnectionConfigurationPage(DataBindingContext context,
			SapConnectionConfiguration sapConnectionConfiguration) {
		super(Messages.SapGlobalConnectionConfigurationPage_EditSapConnectionConfigurations,
				Messages.SapGlobalConnectionConfigurationPage_EditSapDestinationAndServerDataStores,
				Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAP_TOOL_SUITE_48_IMAGE));
		setDescription(Messages.SapGlobalConnectionConfigurationPage_CreateEditDeleteDestinationAndServerConnectionConfigurations);
		this.context = context;
		this.sapConnectionConfiguration = sapConnectionConfiguration;
		setPageComplete(false);

		initializeEditingDomain();
	}

	@Override
	public void createControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridData topData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		top.setLayoutData(topData);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(top);

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(top);
		top.setLayout(new GridLayout(1, false));
		
		SashForm sashForm = new SashForm(top, SWT.HORIZONTAL);
		sashForm.setSashWidth(50);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		viewer = new TreeViewer(sashForm, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
			        	
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.setInput(sapConnectionConfiguration.eResource());
		viewer.addSelectionChangedListener(this);
		viewer.expandAll();

		ScrolledComposite sc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
		
		properties = new Composite(sc, SWT.NONE);
		stackLayout = new StackLayout();
		properties.setLayout(stackLayout);
		
		sapConnectionConfigurationTabFolder = new CTabFolder(properties, SWT.BORDER);
		sapConnectionConfigurationTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem sapConnectionConfigurationDescriptionItem = new CTabItem(sapConnectionConfigurationTabFolder, SWT.NONE);
		sapConnectionConfigurationDescriptionItem.setText(Messages.SapGlobalConnectionConfigurationPage_tbtmNewItem_text);
		sapConnectionConfigurationTabFolder.setSelection(sapConnectionConfigurationDescriptionItem);
		
		Label lblContainsStoresFor = new Label(sapConnectionConfigurationTabFolder, SWT.WRAP);
		sapConnectionConfigurationDescriptionItem.setControl(lblContainsStoresFor);
		lblContainsStoresFor.setText(Messages.SapGlobalConnectionConfigurationPage_lblContainsStoresFor_text);
		
		destinationDataStoreTabFolder = new CTabFolder(properties, SWT.BORDER);
		
		CTabItem destinationDataStoreDescriptionItem = new CTabItem(destinationDataStoreTabFolder, SWT.NONE);
		destinationDataStoreDescriptionItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataStoreDescritionItemTitle);
		destinationDataStoreTabFolder.setSelection(destinationDataStoreDescriptionItem);
		
		Label lblContainsSapConnection_1 = new Label(destinationDataStoreTabFolder, SWT.WRAP);
		lblContainsSapConnection_1.setText(Messages.SapGlobalConnectionConfigurationPage_lblContainsSapConnection_1_text);
		destinationDataStoreDescriptionItem.setControl(lblContainsSapConnection_1);
		
		serverDataStoreTabFolder = new CTabFolder(properties, SWT.BORDER);
		
		CTabItem tabItem_1 = new CTabItem(serverDataStoreTabFolder, SWT.NONE);
		tabItem_1.setText(Messages.SapGlobalConnectionConfigurationPage_ServerDataStoreDescriptionItemTitle);
		serverDataStoreTabFolder.setSelection(tabItem_1);
		
		Label serverDataStoreDescriptionItem = new Label(serverDataStoreTabFolder, SWT.WRAP);
		serverDataStoreDescriptionItem.setText(Messages.SapGlobalConnectionConfigurationPage_lblContainsSapConnection_text_1);
		tabItem_1.setControl(serverDataStoreDescriptionItem);
		
		destinationDataTabFolder = new CTabFolder(properties, SWT.BORDER);
		destinationDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem destinationDataBasicItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataBasicItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataBasicItemTitle);
		destinationDataTabFolder.setSelection(destinationDataBasicItem);
		
		CTabItem destinationDataConnectionItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataConnectionItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataConnectionItemTitle);
		
		CTabItem destinationDataAuthenticationItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataAuthenticationItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataAuthenticationItemTitle);
		
		CTabItem destinationDataSpecialItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataSpecialItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataSpecialItemTitle);
		
		CTabItem destinationDataPoolItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataPoolItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataPoolItemTitle);
		
		CTabItem destinationDataSncItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataSncItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataSncItemTitle);
		
		CTabItem destinationDataRepositoryItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
		destinationDataRepositoryItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataRepositoryItemTitle);
		
		serverDataProperties = new ServerDataProperties();
		serverDataProperties.createControl();

		sc.setContent(properties);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(properties.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		initActions();
		
		// Create Context Menu
		hookContextMenu();
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SapGlobalConnectionConfigurationPage.this.fillContextMenu(manager);
			}
		});
		
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
	}

	private void fillContextMenu(IMenuManager menuManager) {
		
		Object obj = selection.getFirstElement();
		if (obj instanceof DestinationDataStore){
			menuManager.add(newDestinationAction);
		} else if (obj instanceof ServerDataStore){
			menuManager.add(newServerAction);
		} else if (obj instanceof DestinationDataStoreEntryImpl) {
			menuManager.add(deleteAction);
		} else if (obj instanceof ServerDataStoreEntryImpl) {
			menuManager.add(deleteAction);
		}


		menuManager.add(new Separator("edit")); //$NON-NLS-1$
		
		menuManager.add(new Separator("additions")); //$NON-NLS-1$
		menuManager.add(new Separator("additions-end")); //$NON-NLS-1$

	}
	
	public void setSelectionToViewer(Collection<?> collection) {
		final Collection<?> theSelection = collection;

		if (theSelection != null && !theSelection.isEmpty()) {
			Runnable runnable = new Runnable() {
				public void run() {
					viewer.setSelection(new StructuredSelection(theSelection.toArray()), true);
				}
			};
			getShell().getDisplay().asyncExec(runnable);
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selection = event.getSelection() instanceof IStructuredSelection ? (IStructuredSelection) event.getSelection() : StructuredSelection.EMPTY;
		updateProperties();
	}
	
	private void updateProperties() {
		Object obj = selection.getFirstElement();
		if (obj instanceof SapConnectionConfiguration) {
			stackLayout.topControl = sapConnectionConfigurationTabFolder;
		} else if (obj instanceof DestinationDataStore) {
			stackLayout.topControl = destinationDataStoreTabFolder;
		} else if (obj instanceof ServerDataStore) {
			stackLayout.topControl = serverDataStoreTabFolder;
		} else if (obj instanceof DestinationDataStoreEntryImpl) {
			stackLayout.topControl = destinationDataTabFolder;
		} else if (obj instanceof ServerDataStoreEntryImpl) {
			stackLayout.topControl = serverDataTabFolder;
			serverDataProperties.setInput(selection);
		}
		properties.layout();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected void initializeEditingDomain() {
		// Create an adapter factory that yields item providers.
		//
		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new RfcItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new IdocItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		// Create the command stack that will notify this page as commands are
		// executed.
		//
		BasicCommandStack commandStack = new TransactionalCommandStack();
		commandStack.addCommandStackListener(new CommandStackListener() {

			@Override
			public void commandStackChanged(final EventObject event) {
				getShell().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						Command mostRecentCommand = ((CommandStack) event.getSource()).getMostRecentCommand();
						if (mostRecentCommand != null) {
							setSelectionToViewer(mostRecentCommand.getAffectedObjects());
						}
					}
				});

			}
		});

		// Create the editing domain with a special command stack.
		//
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
	}

	protected void initActions() {
	    sharedImages = PlatformUI.getWorkbench().getSharedImages();

	    deleteAction = new DeleteAction();
	    
	    newDestinationAction = new NewDestinationAction();
	    
	    newServerAction = new NewServerAction();
	    
	}
	
	private FormLayout compositeFormLayout() {
		FormLayout layout = new FormLayout();
		layout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
		layout.marginHeight = ITabbedPropertyConstants.VSPACE;
        layout.spacing = ITabbedPropertyConstants.VMARGIN + 1;
		return layout;
	}
	
	private FormData descriptionLayoutData() {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		return data;
	}
	
	private FormData firstEntryLayoutData() {
		FormData data = new FormData();
		data.left = new FormAttachment(0, (int) 3 * AbstractPropertySection.STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		return data;
	}
	
	private FormData labelLayoutData(Control referenceControl) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(referenceControl, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(referenceControl, 0, SWT.CENTER);
		return data;
	}
	
	private FormData entryLayoutData(Control referenceControl) {
		FormData data = new FormData();
		data = new FormData();
		data.left = new FormAttachment(0, (int) 3 * AbstractPropertySection.STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(referenceControl, 2 * ITabbedPropertyConstants.VSPACE);
		return data;
	}
}

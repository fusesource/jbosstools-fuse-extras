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
import org.fusesource.ide.sap.ui.converter.CpicTraceComboSelection2TraceLevelConverter;
import org.fusesource.ide.sap.ui.converter.SncQos2SncQosComboSelectionConverter;
import org.fusesource.ide.sap.ui.converter.SncQosComboSelection2SncQosConverter;
import org.fusesource.ide.sap.ui.converter.String2BooleanConverter;
import org.fusesource.ide.sap.ui.converter.TraceLevel2CpicTraceComboSelectionConverter;
import org.fusesource.ide.sap.ui.dialog.DestinationDialog;
import org.fusesource.ide.sap.ui.dialog.ServerDialog;
import org.fusesource.ide.sap.ui.dialog.TestDestinationDialog;
import org.fusesource.ide.sap.ui.dialog.TestServerDialog;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;
import org.fusesource.ide.sap.ui.edit.idoc.IdocItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.edit.rfc.RfcItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.validator.ClientNumberValidator;
import org.fusesource.ide.sap.ui.validator.LanguageValidator;
import org.fusesource.ide.sap.ui.validator.NonNegativeIntegerValidator;
import org.fusesource.ide.sap.ui.validator.SapRouterStringValidator;
import org.fusesource.ide.sap.ui.validator.SystemNumberValidator;

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
	
	private class TestAction extends Action {
		public TestAction() {
			super(Messages.SapGlobalConnectionConfigurationPage_Run, Activator.getDefault().getImageRegistry().getDescriptor(Activator.TEST_IMAGE));
		}
		
		@Override
		public void run() {
			if (selection.size() == 1) {
				Object obj = selection.getFirstElement();
				if (obj instanceof DestinationDataStoreEntryImpl) {
					String name = ((DestinationDataStoreEntryImpl) obj).getKey();
					TestDestinationDialog testDestinationDialog = new TestDestinationDialog(getShell(), name);
					testDestinationDialog.open();
				} else if (obj instanceof ServerDataStoreEntryImpl) {
					String name = ((ServerDataStoreEntryImpl) obj).getKey();
					TestServerDialog testServerDialog = new TestServerDialog(getShell(), name);
					testServerDialog.open();
				}
			}
		}
	}
	
	private class DestinationDataProperties {
		
		protected DestinationDataStoreEntryImpl destinationDataStoreEntry;
		protected EditingDomain editingDomain;
		
		private Text ashostText;
		private Text sysnrText;
		private Text clientText;
		private Text passwordText;
		private Text languageText;
		private Text userText;
		private Text sysnrText2;
		private Text saprouterText;
		private Text ashostText2;
		private Text mshostText;
		private Text msservText;
		private Text gwhostText;
		private Text gwservText;
		private Text r3nameText;
		private Text groupText;
		private CCombo authTypeCombo;
		private Text clientText2;
		private Text userText2;
		private Text userAlias;
		private Text passwordText2;
		private Text mysapsso2Text;
		private Text x509certText;
		private Text languageText2;
		private Button traceBtn;
		private CCombo cpicTraceCombo;
		private Button lcheckBtn;
		private Text codepageText;
		private Button getsso2Btn;
		private Button denyInitialPasswordBtn;
		private Text peakLimitText;
		private Text poolCapacityText;
		private Text expirationTimeText;
		private Text expirationCheckPeriodText;
		private Text maxGetClientTimeText;
		private Button sncModeBtn;
		private Text sncPartnernameText;
		private CCombo sncQopCombo;
		private Text sncMynameText;
		private Text sncLibraryText;
		private Text repositoryDestinationText;
		private Text repositoryUserText;
		private Text repositoryPasswordText;
		private Button respositorySncBtn;
		private Button repositoryRoundtripOptimizationBtn;

		private Binding sysnrBinding;
		private Binding clientBinding;
		private Binding langBinding;
		private Binding saprouterBinding;
		private Binding sysnrBinding2;
		private Binding clientBinding2;
		private Binding langBinding2;
		private Binding peakLimitBinding;
		private Binding poolCapacityBinding;
		private Binding expirationTimeBinding;
		private Binding expirationPeriodBinding;
		private Binding maxGetTimeBinding;

		private DataBindingContext bindingContext;
		
		public void createControl() {
			destinationDataTabFolder = new CTabFolder(properties, SWT.BORDER);
			destinationDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

			////
			//  Destination Data Basic Properties Tab
			//
			CTabItem destinationDataBasicItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataBasicItem.setText(Messages.SapGlobalConnectionConfigurationPage_Basic);
			destinationDataTabFolder.setSelection(destinationDataBasicItem);
			
			Composite basicContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataBasicItem.setControl(basicContainer);
			basicContainer.setLayout(compositeFormLayout());

			ashostText = new Text(basicContainer, SWT.BORDER);
			ashostText.setToolTipText(Messages.BasicPropertySection_AshostToolTip);
			ashostText.setLayoutData(firstEntryLayoutData());
			
			CLabel ashostLbl = new CLabel(basicContainer, SWT.NONE);
			ashostLbl.setText(Messages.BasicPropertySection_AshostLabel);
			ashostLbl.setLayoutData(labelLayoutData(ashostText));
			ashostLbl.setAlignment(SWT.RIGHT);

			sysnrText = new Text(basicContainer, SWT.BORDER);
			sysnrText.setToolTipText(Messages.BasicPropertySection_SysnrToolTip);
			sysnrText.setLayoutData(entryLayoutData(ashostText));
			
			CLabel systemNumberLbl = new CLabel(basicContainer, SWT.NONE);
			systemNumberLbl.setText(Messages.BasicPropertySection_SysnrLabel);
			systemNumberLbl.setLayoutData(labelLayoutData(sysnrText));
			systemNumberLbl.setAlignment(SWT.RIGHT);
					
			clientText = new Text(basicContainer, SWT.BORDER);
			clientText.setToolTipText(Messages.BasicPropertySection_ClientToolTip);
			clientText.setLayoutData(entryLayoutData(sysnrText));
			
			CLabel clientLbl = new CLabel(basicContainer, SWT.NONE);
			clientLbl.setText(Messages.BasicPropertySection_ClientLabel);
			clientLbl.setLayoutData(labelLayoutData(clientText));
			clientLbl.setAlignment(SWT.RIGHT);
			
			userText = new Text(basicContainer, SWT.BORDER);
			userText.setToolTipText(Messages.BasicPropertySection_UserToolTip);
			userText.setLayoutData(entryLayoutData(clientText));
			
			CLabel userLbl = new CLabel(basicContainer, SWT.NONE);
			userLbl.setText(Messages.BasicPropertySection_UserLabel);
			userLbl.setLayoutData(labelLayoutData(userText));
			userLbl.setAlignment(SWT.RIGHT);
			
			passwordText = new Text(basicContainer, SWT.BORDER | SWT.PASSWORD);
			passwordText.setToolTipText(Messages.BasicPropertySection_PasswordToolTip);
			passwordText.setLayoutData(entryLayoutData(userText));
			
			CLabel passwordLbl = new CLabel(basicContainer, SWT.NONE);
			passwordLbl.setText(Messages.BasicPropertySection_PasswordLabel);
			passwordLbl.setLayoutData(labelLayoutData(passwordText));
			passwordLbl.setAlignment(SWT.RIGHT);
			
			languageText = new Text(basicContainer, SWT.BORDER);
			languageText.setToolTipText(Messages.BasicPropertySection_LanguageToolTip);
			languageText.setLayoutData(entryLayoutData(passwordText));
			new Label(basicContainer, SWT.NONE);
			new Label(basicContainer, SWT.NONE);
			
			
			CLabel languageLbl = new CLabel(basicContainer, SWT.NONE);
			languageLbl.setText(Messages.BasicPropertySection_LanguageLabel);
			languageLbl.setLayoutData(labelLayoutData(languageText));
			languageLbl.setAlignment(SWT.RIGHT);
			//
			////
			
			////
			//  Destination Data Connection Properties Tab
			//
			CTabItem destinationDataConnectionItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataConnectionItem.setText(Messages.SapGlobalConnectionConfigurationPage_Connection);
			destinationDataTabFolder.setSelection(destinationDataConnectionItem);
			
			Composite connectionContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataConnectionItem.setControl(connectionContainer);
			connectionContainer.setLayout(compositeFormLayout());

			sysnrText2 = new Text(connectionContainer, SWT.BORDER);
			sysnrText2.setToolTipText(Messages.ConnectionPropertySection_SysnrToolTip);
			sysnrText2.setLayoutData(firstEntryLayoutData());

			CLabel sysnrLbl2 = new CLabel(connectionContainer, SWT.NONE);
			sysnrLbl2.setText(Messages.ConnectionPropertySection_SysnrLabel);
			sysnrLbl2.setLayoutData(labelLayoutData(sysnrText2));
			sysnrLbl2.setAlignment(SWT.RIGHT);

			saprouterText = new Text(connectionContainer, SWT.BORDER);
			saprouterText.setToolTipText(Messages.ConnectionPropertySection_SaprouterToolTip);
			saprouterText.setLayoutData(entryLayoutData(sysnrText2));

			CLabel saprouterLbl = new CLabel(connectionContainer, SWT.NONE);
			saprouterLbl.setText(Messages.ConnectionPropertySection_SaprouterLabel);
			saprouterLbl.setLayoutData(labelLayoutData(saprouterText));
			saprouterLbl.setAlignment(SWT.RIGHT);

			ashostText2 = new Text(connectionContainer, SWT.BORDER);
			ashostText2.setToolTipText(Messages.ConnectionPropertySection_AshostToolTip);
			ashostText2.setLayoutData(entryLayoutData(saprouterText));

			CLabel ashostLbl2 = new CLabel(connectionContainer, SWT.NONE);
			ashostLbl2.setText(Messages.ConnectionPropertySection_AshostLabel);
			ashostLbl2.setLayoutData(labelLayoutData(ashostText2));
			ashostLbl2.setAlignment(SWT.RIGHT);

			mshostText = new Text(connectionContainer, SWT.BORDER);
			mshostText.setToolTipText(Messages.ConnectionPropertySection_MshostToolTip);
			mshostText.setLayoutData(entryLayoutData(ashostText2));

			CLabel mshostLbl = new CLabel(connectionContainer, SWT.NONE);
			mshostLbl.setText(Messages.ConnectionPropertySection_MshostLabel);
			mshostLbl.setLayoutData(labelLayoutData(mshostText));
			mshostLbl.setAlignment(SWT.RIGHT);

			msservText = new Text(connectionContainer, SWT.BORDER);
			msservText.setToolTipText(Messages.ConnectionPropertySection_MsservToolTip);
			msservText.setLayoutData(entryLayoutData(mshostText));

			CLabel msgservLbl = new CLabel(connectionContainer, SWT.NONE);
			msgservLbl.setText(Messages.ConnectionPropertySection_MsservLabel);
			msgservLbl.setLayoutData(labelLayoutData(msservText));
			msgservLbl.setAlignment(SWT.RIGHT);

			gwhostText = new Text(connectionContainer, SWT.BORDER);
			gwhostText.setToolTipText(Messages.ConnectionPropertySection_GwhostToolTip);
			gwhostText.setLayoutData(entryLayoutData(msservText));

			CLabel gwhostLbl = new CLabel(connectionContainer, SWT.NONE);
			gwhostLbl.setText(Messages.ConnectionPropertySection_GwhostLabel);
			gwhostLbl.setLayoutData(labelLayoutData(gwhostText));
			gwhostLbl.setAlignment(SWT.RIGHT);

			gwservText = new Text(connectionContainer, SWT.BORDER);
			gwservText.setToolTipText(Messages.ConnectionPropertySection_GwservToolTip);
			gwservText.setLayoutData(entryLayoutData(gwhostText));

			CLabel gwservLbl = new CLabel(connectionContainer, SWT.NONE);
			gwservLbl.setText(Messages.ConnectionPropertySection_GwservLabel);
			gwservLbl.setLayoutData(labelLayoutData(gwservText));
			gwservLbl.setAlignment(SWT.RIGHT);

			r3nameText = new Text(connectionContainer, SWT.BORDER);
			r3nameText.setToolTipText(Messages.ConnectionPropertySection_R3nameToolTip);
			r3nameText.setLayoutData(entryLayoutData(gwservText));

			CLabel r3nameLbl = new CLabel(connectionContainer, SWT.NONE);
			r3nameLbl.setText(Messages.ConnectionPropertySection_R3nameLabel);
			r3nameLbl.setLayoutData(labelLayoutData(r3nameText));
			r3nameLbl.setAlignment(SWT.RIGHT);

			groupText = new Text(connectionContainer, SWT.BORDER);
			groupText.setToolTipText(Messages.ConnectionPropertySection_GroupToolTip);
			groupText.setLayoutData(entryLayoutData(r3nameText));

			CLabel groupLbl = new CLabel(connectionContainer, SWT.NONE);
			groupLbl.setText(Messages.ConnectionPropertySection_GroupLabel);
			groupLbl.setLayoutData(labelLayoutData(groupText));
			groupLbl.setAlignment(SWT.RIGHT);
			//
			////
			
			////
			//  Destination Data Authentication Properties Tab
			//
			CTabItem destinationDataAuthenticationItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataAuthenticationItem.setText(Messages.SapGlobalConnectionConfigurationPage_Authentication);
			destinationDataTabFolder.setSelection(destinationDataAuthenticationItem);
			
			Composite authenticationContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataAuthenticationItem.setControl(authenticationContainer);
			authenticationContainer.setLayout(compositeFormLayout());

			authTypeCombo = new CCombo(authenticationContainer, SWT.READ_ONLY | SWT.BORDER);
			authTypeCombo.setToolTipText(Messages.AuthenticationPropertySection_AuthTypeToolTip);
			authTypeCombo.setItems(new String[] {"CONFIGURED_USER", "CURRENT_USER"}); //$NON-NLS-1$ //$NON-NLS-2$
			authTypeCombo.select(0);
			authTypeCombo.setLayoutData(firstEntryLayoutData());
			
			CLabel authTypeLbl = new CLabel(authenticationContainer, SWT.NONE); 
			authTypeLbl.setText(Messages.AuthenticationPropertySection_AuthTypeLable);
			authTypeLbl.setLayoutData(labelLayoutData(authTypeCombo));
			authTypeLbl.setAlignment(SWT.RIGHT);
		
			clientText2 = new Text(authenticationContainer, SWT.BORDER);
			clientText2.setToolTipText(Messages.AuthenticationPropertySection_ClientToolTip);
			clientText2.setLayoutData(entryLayoutData(authTypeCombo));
			
			CLabel clientLbl2 = new CLabel(authenticationContainer, SWT.NONE); 
			clientLbl2.setText(Messages.AuthenticationPropertySection_ClientLable);
			clientLbl2.setLayoutData(labelLayoutData(clientText2));
			clientLbl2.setAlignment(SWT.RIGHT);
			
			
			userText2 = new Text(authenticationContainer, SWT.BORDER);
			userText2.setToolTipText(Messages.AuthenticationPropertySection_UserToolTip);
			userText2.setLayoutData(entryLayoutData(clientText2));
			
			CLabel userLbl2 = new CLabel(authenticationContainer, SWT.NONE); 
			userLbl2.setText(Messages.AuthenticationPropertySection_UserLabel);
			userLbl2.setLayoutData(labelLayoutData(userText2));
			userLbl2.setAlignment(SWT.RIGHT);
			
			userAlias = new Text(authenticationContainer, SWT.BORDER);
			userAlias.setToolTipText(Messages.AuthenticationPropertySection_UserAliasToolTip);
			userAlias.setLayoutData(entryLayoutData(userText2));
			
			CLabel userAliasLbl = new CLabel(authenticationContainer, SWT.NONE);
			userAliasLbl.setText(Messages.AuthenticationPropertySection_UserAliasLabel);
			userAliasLbl.setLayoutData(labelLayoutData(userAlias));
			userAliasLbl.setAlignment(SWT.RIGHT);

			passwordText2 = new Text(authenticationContainer, SWT.BORDER | SWT.PASSWORD);
			passwordText2.setToolTipText(Messages.AuthenticationPropertySection_PasswordToolTip);
			passwordText2.setLayoutData(entryLayoutData(userAlias));
			
			CLabel passwordLbl2 = new CLabel(authenticationContainer, SWT.NONE); 
			passwordLbl2.setText(Messages.AuthenticationPropertySection_PasswordLabel);
			passwordLbl2.setLayoutData(labelLayoutData(passwordText2));
			passwordLbl2.setAlignment(SWT.RIGHT);
			
			mysapsso2Text = new Text(authenticationContainer, SWT.BORDER);
			mysapsso2Text.setToolTipText(Messages.AuthenticationPropertySection_Mysapsso2ToolTip);
			mysapsso2Text.setLayoutData(entryLayoutData(passwordText2));
			
			CLabel mysapsso2Lbl = new CLabel(authenticationContainer, SWT.NONE);
			mysapsso2Lbl.setText(Messages.AuthenticationPropertySection_Mysapsso2Label);
			mysapsso2Lbl.setLayoutData(labelLayoutData(mysapsso2Text));
			mysapsso2Lbl.setAlignment(SWT.RIGHT);
			
			x509certText = new Text(authenticationContainer, SWT.BORDER);
			x509certText.setToolTipText(Messages.AuthenticationPropertySection_X509certToolTip);
			x509certText.setLayoutData(entryLayoutData(mysapsso2Text));
			
			CLabel x509certLbl = new CLabel(authenticationContainer, SWT.NONE); 
			x509certLbl.setText(Messages.AuthenticationPropertySection_X509certLabel);
			x509certLbl.setLayoutData(labelLayoutData(x509certText));
			x509certLbl.setAlignment(SWT.RIGHT);
			
			languageText2 = new Text(authenticationContainer, SWT.BORDER);
			languageText2.setToolTipText(Messages.AuthenticationPropertySection_LanguageToolTip);
			languageText2.setLayoutData(entryLayoutData(x509certText));
			
			CLabel languageLbl2 = new CLabel(authenticationContainer, SWT.NONE); 
			languageLbl2.setText(Messages.AuthenticationPropertySection_LanguageLabel);
			languageLbl2.setLayoutData(labelLayoutData(languageText2));
			languageLbl2.setAlignment(SWT.RIGHT);
			//
			////

			
			////
			//  Destination Data Special Properties Tab
			//
			CTabItem destinationDataSpecialItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataSpecialItem.setText(Messages.SapGlobalConnectionConfigurationPage_Special);
			destinationDataTabFolder.setSelection(destinationDataSpecialItem);
			
			Composite specialContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataSpecialItem.setControl(specialContainer);
			specialContainer.setLayout(compositeFormLayout());
			
			traceBtn = new Button(specialContainer, SWT.FLAT | SWT.CHECK);
			traceBtn.setText(Messages.SpecialPropertySection_TraceLabel); 
			traceBtn.setToolTipText(Messages.SpecialPropertySection_TraceToolTip);
			traceBtn.setLayoutData(firstEntryLayoutData());

			cpicTraceCombo = new CCombo(specialContainer, SWT.READ_ONLY | SWT.BORDER);
			cpicTraceCombo.setItems(new String[] {"", Messages.SpecialPropertySection_CpicTraceLevel0Label, Messages.SpecialPropertySection_CpicTraceLevel1Label, Messages.SpecialPropertySection_CpicTraceLevel2Label, Messages.SpecialPropertySection_CpicTraceLevel3Label}); //$NON-NLS-1$
			cpicTraceCombo.setToolTipText(Messages.SpecialPropertySection_CpicTraceToolTip);
			cpicTraceCombo.setLayoutData(entryLayoutData(traceBtn));
			cpicTraceCombo.select(0);

			CLabel sysnrLbl3 = new CLabel(specialContainer, SWT.NONE);  
			sysnrLbl3.setText(Messages.SpecialPropertySection_SysnrLabel);
			sysnrLbl3.setLayoutData(labelLayoutData(cpicTraceCombo));
			sysnrLbl3.setAlignment(SWT.RIGHT);
			
			lcheckBtn = new Button(specialContainer, SWT.FLAT | SWT.CHECK);
			lcheckBtn.setText(Messages.SpecialPropertySection_LcheckLabel);
			lcheckBtn.setToolTipText(Messages.SpecialPropertySection_LcheckToolTip);
			lcheckBtn.setLayoutData(entryLayoutData(cpicTraceCombo));
			lcheckBtn.setText(Messages.SpecialPropertySection_LcheckLabel);
			
			codepageText = new Text(specialContainer, SWT.BORDER);
			codepageText.setToolTipText(Messages.SpecialPropertySection_CodepageToolTip);
			codepageText.setLayoutData(entryLayoutData(lcheckBtn));
			
			CLabel codepageLbl = new CLabel(specialContainer, SWT.NONE); 
			codepageLbl.setText(Messages.SpecialPropertySection_CodepageLabel);
			codepageLbl.setLayoutData(labelLayoutData(codepageText));
			codepageLbl.setAlignment(SWT.RIGHT);
			
			getsso2Btn = new Button(specialContainer, SWT.FLAT | SWT.CHECK);
			getsso2Btn.setText(Messages.SpecialPropertySection_Getsso2Label);
			getsso2Btn.setToolTipText(Messages.SpecialPropertySection_Getsso2ToolTip);
			getsso2Btn.setLayoutData(entryLayoutData(codepageText));
			
			denyInitialPasswordBtn = new Button(specialContainer, SWT.FLAT | SWT.CHECK);
			denyInitialPasswordBtn.setText(Messages.SpecialPropertySection_DenyInitialPasswordLabel);
			denyInitialPasswordBtn.setToolTipText(Messages.SpecialPropertySection_DenyInitialPasswordToolTip);
			denyInitialPasswordBtn.setLayoutData(entryLayoutData(getsso2Btn));
			//
			////
			
			////
			//  Destination Data Pool Properties Tab
			//
			CTabItem destinationDataPoolItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataPoolItem.setText(Messages.SapGlobalConnectionConfigurationPage_Pool);
			destinationDataTabFolder.setSelection(destinationDataPoolItem);
			
			Composite poolContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataPoolItem.setControl(poolContainer);
			poolContainer.setLayout(compositeFormLayout());
			
			peakLimitText = new Text(poolContainer, SWT.BORDER);
			peakLimitText.setToolTipText(Messages.PoolPropertySection_PeakLimitToolTip);
			peakLimitText.setLayoutData(firstEntryLayoutData());

			CLabel peakLimitLbl = new CLabel(poolContainer, SWT.NONE); 
			peakLimitLbl.setText(Messages.PoolPropertySection_PeakLimitLabel);
			peakLimitLbl.setLayoutData(labelLayoutData(peakLimitText));
			peakLimitLbl.setAlignment(SWT.RIGHT);
			
			poolCapacityText = new Text(poolContainer, SWT.BORDER);
			poolCapacityText.setToolTipText(Messages.PoolPropertySection_PoolCapacityToolTip);
			poolCapacityText.setLayoutData(entryLayoutData(peakLimitText));

			CLabel poolCapacityLbl = new CLabel(poolContainer, SWT.NONE); 
			poolCapacityLbl.setText(Messages.PoolPropertySection_PoolCapacityLabel);
			poolCapacityLbl.setLayoutData(labelLayoutData(poolCapacityText));
			poolCapacityLbl.setAlignment(SWT.RIGHT);
			
			expirationTimeText = new Text(poolContainer, SWT.BORDER);
			expirationTimeText.setToolTipText(Messages.PoolPropertySection_ExpirationTimeToolTip);
			expirationTimeText.setLayoutData(entryLayoutData(poolCapacityText));

			CLabel expirationTimeLbl = new CLabel(poolContainer, SWT.NONE); 
			expirationTimeLbl.setText(Messages.PoolPropertySection_ExpirationTimeLabel);
			expirationTimeLbl.setLayoutData(labelLayoutData(expirationTimeText));
			expirationTimeLbl.setAlignment(SWT.RIGHT);
			
			expirationCheckPeriodText = new Text(poolContainer, SWT.BORDER);
			expirationCheckPeriodText.setToolTipText(Messages.PoolPropertySection_ExpirationCheckPeriodToolTip);
			expirationCheckPeriodText.setLayoutData(entryLayoutData(expirationTimeText));

			CLabel expirationCheckPeriodLbl = new CLabel(poolContainer, SWT.WRAP); 
			expirationCheckPeriodLbl.setText(Messages.PoolPropertySection_ExpirationCheckPeriodLabel);
			expirationCheckPeriodLbl.setLayoutData(labelLayoutData(expirationCheckPeriodText));
			expirationCheckPeriodLbl.setAlignment(SWT.RIGHT);
			
			maxGetClientTimeText = new Text(poolContainer, SWT.BORDER);
			maxGetClientTimeText.setToolTipText(Messages.PoolPropertySection_MaxGetClientTimeToolTip);
			maxGetClientTimeText.setLayoutData(entryLayoutData(expirationCheckPeriodText));

			CLabel maxGetClientTimeLbl = new CLabel(poolContainer, SWT.NONE); 
			maxGetClientTimeLbl.setText(Messages.PoolPropertySection_MaxGetClientTimeLabel);
			maxGetClientTimeLbl.setLayoutData(labelLayoutData(maxGetClientTimeText));
			maxGetClientTimeLbl.setAlignment(SWT.RIGHT);
			//
			////

			////
			//  Destination Data SNC Properties Tab
			//
			CTabItem destinationDataSncItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataSncItem.setText(Messages.SapGlobalConnectionConfigurationPage_SNC);
			destinationDataTabFolder.setSelection(destinationDataSncItem);
			
			Composite sncContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataSncItem.setControl(sncContainer);
			sncContainer.setLayout(compositeFormLayout());
			
			sncModeBtn = new Button(sncContainer, SWT.FLAT | SWT.CHECK);
			sncModeBtn.setText(Messages.SncPropertySection_SncModeLabel);
			sncModeBtn.setToolTipText(Messages.SncPropertySection_SncModelToolTip);
			sncModeBtn.setLayoutData(firstEntryLayoutData());
			
			sncPartnernameText = new Text(sncContainer, SWT.BORDER);
			sncPartnernameText.setToolTipText(Messages.SncPropertySection_SncPartnernameToolTip);
			sncPartnernameText.setLayoutData(entryLayoutData(sncModeBtn));
			
			CLabel sncPartnernameLbl = new CLabel(sncContainer, SWT.NONE); 
			sncPartnernameLbl.setText(Messages.SncPropertySection_SncPartnernameLabel);
			sncPartnernameLbl.setLayoutData(labelLayoutData(sncPartnernameText));
			sncPartnernameLbl.setAlignment(SWT.RIGHT);

			sncQopCombo = new CCombo(sncContainer, SWT.READ_ONLY | SWT.BORDER);
			sncQopCombo.setToolTipText(Messages.SncPropertySection_SncQopToolTip);
			sncQopCombo.setItems(new String[] {"", Messages.SncPropertySection_SncSecurityLevel1Label, Messages.SncPropertySection_SncSecurityLevel2Label, Messages.SncPropertySection_SncSecurityLevel3Label, Messages.SncPropertySection_SncSecurityLevel8Label, Messages.SncPropertySection_SncSecurityLevel9Label}); //$NON-NLS-1$
			sncQopCombo.setLayoutData(entryLayoutData(sncPartnernameText));
			sncQopCombo.select(0);
			
			CLabel sncQopLbl = new CLabel(sncContainer, SWT.NONE); 
			sncQopLbl.setText(Messages.SncPropertySection_SncQopLabel);
			sncQopLbl.setLayoutData(labelLayoutData(sncQopCombo));
			sncQopLbl.setAlignment(SWT.RIGHT);

			sncMynameText = new Text(sncContainer, SWT.BORDER);
			sncMynameText.setToolTipText(Messages.SncPropertySection_SncMynameToolTip);
			sncMynameText.setLayoutData(entryLayoutData(sncQopCombo));
			
			CLabel sncMynameLbl = new CLabel(sncContainer, SWT.NONE);  
			sncMynameLbl.setText(Messages.SncPropertySection_SncMynameLabel);
			sncMynameLbl.setLayoutData(labelLayoutData(sncMynameText));
			sncMynameLbl.setAlignment(SWT.RIGHT);

			sncLibraryText = new Text(sncContainer, SWT.BORDER);
			sncLibraryText.setToolTipText(Messages.SncPropertySection_SncLibraryToolTip);
			sncLibraryText.setLayoutData(entryLayoutData(sncMynameText));

			CLabel sncLibraryLbl = new CLabel(sncContainer, SWT.NONE); 
			sncLibraryLbl.setText(Messages.SncPropertySection_SncLibraryLabel);
			sncLibraryLbl.setLayoutData(labelLayoutData(sncLibraryText));
			sncLibraryLbl.setAlignment(SWT.RIGHT);
			//
			////
			
			////
			//  Destination Data SNC Properties Tab
			//
			CTabItem destinationDataRepositoryItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataRepositoryItem.setText(Messages.SapGlobalConnectionConfigurationPage_Repository);
			destinationDataTabFolder.setSelection(destinationDataRepositoryItem);
			
			Composite repositoryContainer = new Composite(destinationDataTabFolder, SWT.NONE);
			destinationDataRepositoryItem.setControl(repositoryContainer);
			repositoryContainer.setLayout(compositeFormLayout());
			
			repositoryDestinationText = new Text(repositoryContainer, SWT.BORDER);
			repositoryDestinationText.setToolTipText(Messages.RepositoryPropertySection_RepositoryDestinationToolTip);
			repositoryDestinationText.setLayoutData(firstEntryLayoutData());
			
			CLabel repositoryDestinationLbl = new CLabel(repositoryContainer, SWT.NONE); 
			repositoryDestinationLbl.setText(Messages.RepositoryPropertySection_RepositoryDestinationLabel);
			repositoryDestinationLbl.setLayoutData(labelLayoutData(repositoryDestinationText));
			repositoryDestinationLbl.setAlignment(SWT.RIGHT);

			repositoryUserText = new Text(repositoryContainer, SWT.BORDER);
			repositoryUserText.setToolTipText(Messages.RepositoryPropertySection_RepositoryUserToolTip);
			repositoryUserText.setLayoutData(entryLayoutData(repositoryDestinationText));
			
			CLabel repositoryUserLbl = new CLabel(repositoryContainer, SWT.NONE); 
			repositoryUserLbl.setText(Messages.RepositoryPropertySection_RepositoryUserLabel);
			repositoryUserLbl.setLayoutData(labelLayoutData(repositoryUserText));
			repositoryUserLbl.setAlignment(SWT.RIGHT);

			repositoryPasswordText = new Text(repositoryContainer, SWT.BORDER | SWT.PASSWORD);
			repositoryPasswordText.setToolTipText(Messages.RepositoryPropertySection_RepositoryPasswordToolTip);
			repositoryPasswordText.setLayoutData(entryLayoutData(repositoryUserText));
			
			CLabel repositoryPasswordLbl = new CLabel(repositoryContainer, SWT.NONE); 
			repositoryPasswordLbl.setText(Messages.RepositoryPropertySection_RepositoryPasswordLabel);
			repositoryPasswordLbl.setLayoutData(labelLayoutData(repositoryPasswordText));
			repositoryPasswordLbl.setAlignment(SWT.RIGHT);

			respositorySncBtn = new Button(repositoryContainer, SWT.FLAT | SWT.CHECK);
			respositorySncBtn.setText(Messages.RepositoryPropertySection_RepositorySncLabel);
			respositorySncBtn.setToolTipText(Messages.RepositoryPropertySection_RepositorySncToolTip);
			respositorySncBtn.setLayoutData(entryLayoutData(repositoryPasswordText));
			
			repositoryRoundtripOptimizationBtn = new Button(repositoryContainer, SWT.FLAT | SWT.CHECK);
			repositoryRoundtripOptimizationBtn.setText(Messages.RepositoryPropertySection_RepositoryRoundtripOptimizationLabel);
			repositoryRoundtripOptimizationBtn.setToolTipText(Messages.RepositoryPropertySection_RepositoryRoundtripOptimizationToolTip);
			repositoryRoundtripOptimizationBtn.setLayoutData(entryLayoutData(respositorySncBtn));
			//
			////
		}	

		public void setInput(ISelection selection) {
			Assert.isTrue(selection instanceof IStructuredSelection);
			Object input = ((IStructuredSelection)selection).getFirstElement();
			Assert.isTrue(input instanceof DestinationDataStoreEntryImpl);
			destinationDataStoreEntry = (DestinationDataStoreEntryImpl) input;
			editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(destinationDataStoreEntry);
			initDataBindings();
		}
		
		protected DataBindingContext initDataBindings() {
			if (bindingContext != null) {
				bindingContext.dispose();
				bindingContext = null;
			}
			bindingContext = new DataBindingContext();
			
			////
			//  Destination Data Basic Properties Tab
			//
			IObservableValue observeTextAshostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(ashostText);
			IObservableValue destinationAshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ASHOST)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextAshostTextObserveWidget, destinationAshostObserveValue, null, null);
			//
			IObservableValue observeTextSysnrTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sysnrText);
			IObservableValue destinationSysnrObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SYSNR)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
			strategy_1.setBeforeSetValidator(new SystemNumberValidator());
			sysnrBinding = bindingContext.bindValue(observeTextSysnrTextObserveWidget, destinationSysnrObserveValue, strategy_1, null);
			//
			IObservableValue observeTextClientTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(clientText);
			IObservableValue destinationClientObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CLIENT)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy = new UpdateValueStrategy();
			strategy.setBeforeSetValidator(new ClientNumberValidator());
			clientBinding = bindingContext.bindValue(observeTextClientTextObserveWidget, destinationClientObserveValue, strategy, null);
			//
			IObservableValue observeTextUserTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userText);
			IObservableValue destinationUserNameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__USER_NAME)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextUserTextObserveWidget, destinationUserNameObserveValue, null, null);
			//
			IObservableValue observeTextPasswordTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(passwordText);
			IObservableValue destinationPasswordObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PASSWORD)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextPasswordTextObserveWidget, destinationPasswordObserveValue, null, null);
			//
			IObservableValue observeTextLanguageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(languageText);
			IObservableValue destinationLangObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LANG)).observe(destinationDataStoreEntry);
			UpdateValueStrategy langStrategy = new UpdateValueStrategy();
			langStrategy.setBeforeSetValidator(new LanguageValidator());
			langBinding = bindingContext.bindValue(observeTextLanguageTextObserveWidget, destinationLangObserveValue, langStrategy, null);
			
			ControlDecorationSupport.create(sysnrBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(clientBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(langBinding, SWT.TOP | SWT.LEFT);
			//
			////
			
			////
			//  Destination Data Connection Properties Tab
			//
			IObservableValue observeTextSysnrText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sysnrText2);
			sysnrBinding2 = bindingContext.bindValue(observeTextSysnrText2ObserveWidget, destinationSysnrObserveValue, strategy_1, null);
			//
			IObservableValue observeTextSaprouterTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(saprouterText);
			IObservableValue managedConnectionFactorySaprouterObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SAPROUTER)).observe(destinationDataStoreEntry);
			UpdateValueStrategy saprouterStrategy = new UpdateValueStrategy();
			saprouterStrategy.setBeforeSetValidator(new SapRouterStringValidator());
			saprouterBinding = bindingContext.bindValue(observeTextSaprouterTextObserveWidget, managedConnectionFactorySaprouterObserveValue, saprouterStrategy, null);
			//
			IObservableValue observeTextAshostText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(ashostText2);
			bindingContext.bindValue(observeTextAshostText2ObserveWidget, destinationAshostObserveValue, null, null);
			//
			IObservableValue observeTextMshostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(mshostText);
			IObservableValue managedConnectionFactoryMshostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MSHOST)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextMshostTextObserveWidget, managedConnectionFactoryMshostObserveValue, null, null);
			//
			IObservableValue observeTextMsservTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(msservText);
			IObservableValue managedConnectionFactoryMsservObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MSSERV)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextMsservTextObserveWidget, managedConnectionFactoryMsservObserveValue, null, null);
			//
			IObservableValue observeTextGwhostTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwhostText);
			IObservableValue managedConnectionFactoryGwhostObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GWHOST)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextGwhostTextObserveWidget, managedConnectionFactoryGwhostObserveValue, null, null);
			//
			IObservableValue observeTextGwservTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(gwservText);
			IObservableValue managedConnectionFactoryGwservObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GWSERV)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextGwservTextObserveWidget, managedConnectionFactoryGwservObserveValue, null, null);
			//
			IObservableValue observeTextR3nameTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(r3nameText);
			IObservableValue managedConnectionFactoryR3nameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__R3NAME)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextR3nameTextObserveWidget, managedConnectionFactoryR3nameObserveValue, null, null);
			//
			IObservableValue observeTextGroupTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(groupText);
			IObservableValue managedConnectionFactoryGroupObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GROUP)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextGroupTextObserveWidget, managedConnectionFactoryGroupObserveValue, null, null);

			ControlDecorationSupport.create(saprouterBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(sysnrBinding2, SWT.TOP | SWT.LEFT);
			//
			////

			////
			//  Destination Data Authentication Properties Tab
			//
			IObservableValue observeTextAuthTypeComboObserveWidget = WidgetProperties.text().observe(authTypeCombo);
			IObservableValue managedConnectionFactoryAuthTypeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__AUTH_TYPE)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextAuthTypeComboObserveWidget, managedConnectionFactoryAuthTypeObserveValue, null, null);
			//
			IObservableValue observeTextClientText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(clientText2);
			clientBinding2 = bindingContext.bindValue(observeTextClientText2ObserveWidget, observeTextClientTextObserveWidget, strategy, null);
			//
			IObservableValue observeTextUserText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userText2);
			bindingContext.bindValue(observeTextUserText2ObserveWidget, observeTextUserTextObserveWidget, null, null);
			//
			IObservableValue observeTextUserAliasObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(userAlias);
			IObservableValue managedConnectionFactoryAliasUserObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__ALIAS_USER)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextUserAliasObserveWidget, managedConnectionFactoryAliasUserObserveValue, null, null);
			//
			IObservableValue observeTextPasswordText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(passwordText2);
			bindingContext.bindValue(observeTextPasswordText2ObserveWidget, destinationPasswordObserveValue, null, null);
			//
			IObservableValue observeTextMysapsso2TextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(mysapsso2Text);
			IObservableValue managedConnectionFactoryMysapsso2ObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MYSAPSSO2)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextMysapsso2TextObserveWidget, managedConnectionFactoryMysapsso2ObserveValue, null, null);
			//
			IObservableValue observeTextX509certTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(x509certText);
			IObservableValue managedConnectionFactoryX509certObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__X509CERT)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextX509certTextObserveWidget, managedConnectionFactoryX509certObserveValue, null, null);
			//
			IObservableValue observeTextLanguageText2ObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(languageText2);
			langBinding2 = bindingContext.bindValue(observeTextLanguageText2ObserveWidget, destinationLangObserveValue, langStrategy, null);

			ControlDecorationSupport.create(clientBinding2, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(langBinding2, SWT.TOP | SWT.LEFT);
			//
			////

			////
			//  Destination Data Special Properties Tab
			//
			IObservableValue observeSelectionTraceBtnObserveWidget = WidgetProperties.selection().observe(traceBtn);
			IObservableValue managedConnectionFactoryTraceObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__TRACE)).observe(destinationDataStoreEntry);
			UpdateValueStrategy traceStrategy = new UpdateValueStrategy();
			traceStrategy.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy traceModelStrategy = new UpdateValueStrategy();
			traceModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionTraceBtnObserveWidget, managedConnectionFactoryTraceObserveValue, traceStrategy, traceModelStrategy);
			//
			IObservableValue observeSelectionCpicTraceComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(cpicTraceCombo);
			IObservableValue managedConnectionFactoryCpicTraceObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CPIC_TRACE)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
			strategy_4.setConverter(new CpicTraceComboSelection2TraceLevelConverter());
			UpdateValueStrategy cpicTraceStrategy = new UpdateValueStrategy();
			cpicTraceStrategy.setConverter(new TraceLevel2CpicTraceComboSelectionConverter());
			bindingContext.bindValue(observeSelectionCpicTraceComboObserveWidget, managedConnectionFactoryCpicTraceObserveValue, strategy_4, cpicTraceStrategy);
			//
			IObservableValue observeSelectionLcheckBtnObserveWidget = WidgetProperties.selection().observe(lcheckBtn);
			IObservableValue managedConnectionFactoryLcheckObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__LCHECK)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
			strategy_5.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy lcheckModelStrategy = new UpdateValueStrategy();
			lcheckModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionLcheckBtnObserveWidget, managedConnectionFactoryLcheckObserveValue, strategy_5, lcheckModelStrategy);
			//
			IObservableValue observeTextCodepageTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(codepageText);
			IObservableValue managedConnectionFactoryCodepageObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__CODEPAGE)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextCodepageTextObserveWidget, managedConnectionFactoryCodepageObserveValue, null, null);
			//
			IObservableValue observeSelectionGetsso2BtnObserveWidget = WidgetProperties.selection().observe(getsso2Btn);
			IObservableValue managedConnectionFactoryGetsso2ObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__GETSSO2)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
			strategy_6.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy getssoModelStrategy = new UpdateValueStrategy();
			getssoModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionGetsso2BtnObserveWidget, managedConnectionFactoryGetsso2ObserveValue, strategy_6, getssoModelStrategy);
			//
			IObservableValue observeSelectionDenyInitialPasswordBtnObserveWidget = WidgetProperties.selection().observe(denyInitialPasswordBtn);
			IObservableValue managedConnectionFactoryDenyInitialPasswordObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__DENY_INITIAL_PASSWORD)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
			strategy_7.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy denyInitialPasswordModelStrategy = new UpdateValueStrategy();
			denyInitialPasswordModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionDenyInitialPasswordBtnObserveWidget, managedConnectionFactoryDenyInitialPasswordObserveValue, strategy_7, denyInitialPasswordModelStrategy);
			//
			////

			////
			//  Destination Data Pool Properties Tab
			//
			IObservableValue observeTextPeakLimitTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(peakLimitText);
			IObservableValue managedConnectionFactoryPeakLimitObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__PEAK_LIMIT)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
			strategy_8.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_PeakLimitValidator));
			peakLimitBinding = bindingContext.bindValue(observeTextPeakLimitTextObserveWidget, managedConnectionFactoryPeakLimitObserveValue, strategy_8, null);
			//
			IObservableValue observeTextPoolCapacityTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(poolCapacityText);
			IObservableValue managedConnectionFactoryPoolCapacityObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__POOL_CAPACITY)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
			strategy_9.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_PoolCapacityValidator));
			poolCapacityBinding = bindingContext.bindValue(observeTextPoolCapacityTextObserveWidget, managedConnectionFactoryPoolCapacityObserveValue, strategy_9, null);
			//
			IObservableValue observeTextExpirationTimeTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(expirationTimeText);
			IObservableValue managedConnectionFactoryExpirationTimeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__EXPIRATION_TIME)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
			strategy_10.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_ExpirationTimeValidator));
			expirationTimeBinding = bindingContext.bindValue(observeTextExpirationTimeTextObserveWidget, managedConnectionFactoryExpirationTimeObserveValue, strategy_10, null);
			//
			IObservableValue observeTextExpirationCheckPeriodTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(expirationCheckPeriodText);
			IObservableValue managedConnectionFactoryExpirationPeriodObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__EXPIRATION_PERIOD)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
			strategy_11.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_ExpirationCheckPeriodValidator));
			expirationPeriodBinding = bindingContext.bindValue(observeTextExpirationCheckPeriodTextObserveWidget, managedConnectionFactoryExpirationPeriodObserveValue, strategy_11, null);
			//
			IObservableValue observeTextMaxGetClientTimeTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(maxGetClientTimeText);
			IObservableValue managedConnectionFactoryMaxGetTimeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__MAX_GET_TIME)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_12 = new UpdateValueStrategy();
			strategy_12.setBeforeSetValidator(new NonNegativeIntegerValidator(Messages.PoolPropertySection_MaxGetClientTimeValidator));
			maxGetTimeBinding = bindingContext.bindValue(observeTextMaxGetClientTimeTextObserveWidget, managedConnectionFactoryMaxGetTimeObserveValue, strategy_12, null);
			
			ControlDecorationSupport.create(peakLimitBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(poolCapacityBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(expirationTimeBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(expirationPeriodBinding, SWT.TOP | SWT.LEFT);
			ControlDecorationSupport.create(maxGetTimeBinding, SWT.TOP | SWT.LEFT);
			//
			////

			////
			//  Destination Data SNC Properties Tab
			//
			IObservableValue observeSelectionSncModeBtnObserveWidget = WidgetProperties.selection().observe(sncModeBtn);
			IObservableValue managedConnectionFactorySncModeObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SNC_MODE)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_13 = new UpdateValueStrategy();
			strategy_13.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy sncModeModelStrategy = new UpdateValueStrategy();
			sncModeModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionSncModeBtnObserveWidget, managedConnectionFactorySncModeObserveValue, strategy_13, sncModeModelStrategy);
			//
			IObservableValue observeTextSncPartnernameTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncPartnernameText);
			IObservableValue managedConnectionFactorySncPartnernameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SNC_PARTNERNAME)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextSncPartnernameTextObserveWidget, managedConnectionFactorySncPartnernameObserveValue, null, null);
			//
			IObservableValue observeSingleSelectionIndexSncQopComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(sncQopCombo);
			IObservableValue managedConnectionFactorySncQopObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SNC_QOP)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_14 = new UpdateValueStrategy();
			strategy_14.setConverter(new SncQosComboSelection2SncQosConverter());
			UpdateValueStrategy sncQopModelStrategy = new UpdateValueStrategy();
			sncQopModelStrategy.setConverter(new SncQos2SncQosComboSelectionConverter());
			bindingContext.bindValue(observeSingleSelectionIndexSncQopComboObserveWidget, managedConnectionFactorySncQopObserveValue, strategy_14, sncQopModelStrategy);
			//
			IObservableValue observeTextSncMynameTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncMynameText);
			IObservableValue managedConnectionFactorySncMynameObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SNC_MYNAME)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextSncMynameTextObserveWidget, managedConnectionFactorySncMynameObserveValue, null, null);
			//
			IObservableValue observeTextSncLibraryTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(sncLibraryText);
			IObservableValue managedConnectionFactorySncLibraryObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__SNC_LIBRARY)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextSncLibraryTextObserveWidget, managedConnectionFactorySncLibraryObserveValue, null, null);
			//
			////

			////
			//  Destination Data Repository Properties Tab
			//
			IObservableValue observeTextRepositoryDestinationTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryDestinationText);
			IObservableValue managedConnectionFactoryRepositoryDestObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_DEST)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextRepositoryDestinationTextObserveWidget, managedConnectionFactoryRepositoryDestObserveValue, null, null);
			//
			IObservableValue observeTextRepositoryUserTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryUserText);
			IObservableValue managedConnectionFactoryRepositoryUserObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_USER)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextRepositoryUserTextObserveWidget, managedConnectionFactoryRepositoryUserObserveValue, null, null);
			//
			IObservableValue observeTextRepositoryPasswordTextObserveWidget = WidgetProperties.text(SWT.FocusOut).observe(repositoryPasswordText);
			IObservableValue managedConnectionFactoryRepositoryPasswdObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_PASSWD)).observe(destinationDataStoreEntry);
			bindingContext.bindValue(observeTextRepositoryPasswordTextObserveWidget, managedConnectionFactoryRepositoryPasswdObserveValue, null, null);
			//
			IObservableValue observeSelectionRespositorySncBtnObserveWidget = WidgetProperties.selection().observe(respositorySncBtn);
			IObservableValue managedConnectionFactoryRepositorySncObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_SNC)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_15 = new UpdateValueStrategy();
			strategy_15.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy repositorySncModelStrategy = new UpdateValueStrategy();
			repositorySncModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionRespositorySncBtnObserveWidget, managedConnectionFactoryRepositorySncObserveValue, strategy_15, repositorySncModelStrategy);
			//
			IObservableValue observeSelectionRepositoryRoundtripOptimizationBtnObserveWidget = WidgetProperties.selection().observe(repositoryRoundtripOptimizationBtn);
			IObservableValue managedConnectionFactoryRepositoryRoundtripOptimizationObserveValue = EMFEditProperties.value(editingDomain, FeaturePath.fromList(Literals.DESTINATION_DATA_STORE_ENTRY__VALUE, Literals.DESTINATION_DATA__REPOSITORY_ROUNDTRIP_OPTIMIZATION)).observe(destinationDataStoreEntry);
			UpdateValueStrategy strategy_16 = new UpdateValueStrategy();
			strategy_16.setConverter(new Boolean2StringConverter());
			UpdateValueStrategy repositoryRoundtripOptimizationModelStrategy = new UpdateValueStrategy();
			repositoryRoundtripOptimizationModelStrategy.setConverter(new String2BooleanConverter());
			bindingContext.bindValue(observeSelectionRepositoryRoundtripOptimizationBtnObserveWidget, managedConnectionFactoryRepositoryRoundtripOptimizationObserveValue, strategy_16, repositoryRoundtripOptimizationModelStrategy);
			//
			////

			return bindingContext;
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
	 * This is the action used to implement delete.
	 */
	protected TestAction testAction;

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

	private DestinationDataProperties destinationDataProperties;
	
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
		
//		destinationDataTabFolder = new CTabFolder(properties, SWT.BORDER);
//		destinationDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
//		
//		CTabItem destinationDataBasicItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataBasicItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataBasicItemTitle);
//		destinationDataTabFolder.setSelection(destinationDataBasicItem);
//		
//		CTabItem destinationDataConnectionItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataConnectionItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataConnectionItemTitle);
//		
//		CTabItem destinationDataAuthenticationItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataAuthenticationItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataAuthenticationItemTitle);
//		
//		CTabItem destinationDataSpecialItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataSpecialItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataSpecialItemTitle);
//		
//		CTabItem destinationDataPoolItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataPoolItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataPoolItemTitle);
//		
//		CTabItem destinationDataSncItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataSncItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataSncItemTitle);
//		
//		CTabItem destinationDataRepositoryItem = new CTabItem(destinationDataTabFolder, SWT.NONE);
//		destinationDataRepositoryItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataRepositoryItemTitle);
		
		destinationDataProperties = new DestinationDataProperties();
		destinationDataProperties.createControl();
		
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
			menuManager.add(testAction);
		} else if (obj instanceof ServerDataStoreEntryImpl) {
			menuManager.add(deleteAction);
			menuManager.add(testAction);
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
			destinationDataProperties.setInput(selection);
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
	    
	    testAction = new TestAction();
	    
	}
	
	private FormLayout compositeFormLayout() {
		FormLayout layout = new FormLayout();
		layout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
		layout.marginHeight = ITabbedPropertyConstants.VSPACE;
        layout.spacing = ITabbedPropertyConstants.VMARGIN + 1;
		return layout;
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

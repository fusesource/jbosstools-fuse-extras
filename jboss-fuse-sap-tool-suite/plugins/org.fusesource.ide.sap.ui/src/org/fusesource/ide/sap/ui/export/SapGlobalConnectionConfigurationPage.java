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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.command.CompoundCommand;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.dialog.DestinationDialog;
import org.fusesource.ide.sap.ui.dialog.ServerDialog;
import org.fusesource.ide.sap.ui.dialog.TestDestinationDialog;
import org.fusesource.ide.sap.ui.dialog.TestServerDialog;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;
import org.fusesource.ide.sap.ui.edit.idoc.IdocItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.edit.rfc.RfcItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.properties.uicreator.AuthenticationDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.BasicDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.ConnectionDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.IDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.IServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.MandatoryServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.OptionalServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.PoolDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.RepositoryDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SncDestinationDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SncServerDataUICreator;
import org.fusesource.ide.sap.ui.properties.uicreator.SpecialDestinationDataUICreator;

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
		private DataBindingContext bindingContext;
		
		private IDestinationDataUICreator basicDestinationDataUICreator = new BasicDestinationDataUICreator();
		private IDestinationDataUICreator connectionDestinationDataUICreator = new ConnectionDestinationDataUICreator();
		private IDestinationDataUICreator authenticationDestinationDataUICreator = new AuthenticationDestinationDataUICreator();
		private IDestinationDataUICreator specialDestinationDataUICreator = new SpecialDestinationDataUICreator();
		private IDestinationDataUICreator poolDestinationDataUICreator = new PoolDestinationDataUICreator();
		private IDestinationDataUICreator sncDestinationDataUICreator = new SncDestinationDataUICreator();
		private IDestinationDataUICreator repositoryPoolDestinationDataUICreator = new RepositoryDestinationDataUICreator();
		
		public void createControl() {
			TabbedPropertySheetWidgetFactory widgetFactory = new TabbedPropertySheetWidgetFactory();

			destinationDataTabFolder = widgetFactory.createTabFolder(properties, SWT.BORDER);
			destinationDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_Basic, basicDestinationDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_Connection, connectionDestinationDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_Authentication, authenticationDestinationDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_Special, specialDestinationDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_Pool, poolDestinationDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_SNC, sncDestinationDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_Repository, repositoryPoolDestinationDataUICreator);
		}

		/**
		 * @param widgetFactory
		 * @param tabItemName
		 * @param uiCreator
		 */
		private void createTabItem(TabbedPropertySheetWidgetFactory widgetFactory, final String tabItemName, final IDestinationDataUICreator uiCreator) {
			CTabItem destinationDataBasicItem = widgetFactory.createTabItem(destinationDataTabFolder, SWT.NONE);
			destinationDataBasicItem.setText(tabItemName);
			destinationDataTabFolder.setSelection(destinationDataBasicItem);

			Composite basicContainer = widgetFactory.createFlatFormComposite(destinationDataTabFolder);
			destinationDataBasicItem.setControl(basicContainer);
			basicContainer.setLayout(compositeFormLayout());

			uiCreator.createControls(basicContainer, new TabbedPropertySheetWidgetFactory());
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
			
			basicDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
			connectionDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
			authenticationDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
			specialDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
			poolDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
			sncDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);
			repositoryPoolDestinationDataUICreator.initDataBindings(bindingContext, editingDomain, destinationDataStoreEntry);

			return bindingContext;
		}		
	}
	
	private class ServerDataProperties {
		
		protected ServerDataStoreEntryImpl serverDataStoreEntry;
		protected EditingDomain editingDomain;
		private DataBindingContext bindingContext;

		private IServerDataUICreator mandatoryServerDataUICreator = new MandatoryServerDataUICreator();
		private IServerDataUICreator optionalServerDataUICreator = new OptionalServerDataUICreator();
		private IServerDataUICreator sncServerDataUICreator = new SncServerDataUICreator();

		public void createControl() {
			TabbedPropertySheetWidgetFactory widgetFactory = new TabbedPropertySheetWidgetFactory();
			serverDataTabFolder = widgetFactory.createTabFolder(properties, SWT.BORDER);
			serverDataTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_ServerDataMandatoryItemTitle, mandatoryServerDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_ServerDataOptionalItemTitle, optionalServerDataUICreator);
			createTabItem(widgetFactory, Messages.SapGlobalConnectionConfigurationPage_ServerDataSncItemTitle, sncServerDataUICreator);
		}
		
		/**
		 * @param widgetFactory
		 * @param tabItemName
		 * @param uiCreator
		 */
		private void createTabItem(TabbedPropertySheetWidgetFactory widgetFactory, final String tabItemName, final IServerDataUICreator uiCreator) {
			CTabItem destinationDataBasicItem = widgetFactory.createTabItem(serverDataTabFolder, SWT.NONE);
			destinationDataBasicItem.setText(tabItemName);
			serverDataTabFolder.setSelection(destinationDataBasicItem);

			Composite basicContainer = widgetFactory.createFlatFormComposite(serverDataTabFolder);
			destinationDataBasicItem.setControl(basicContainer);
			basicContainer.setLayout(compositeFormLayout());

			uiCreator.createControls(basicContainer, new TabbedPropertySheetWidgetFactory());
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
			
			mandatoryServerDataUICreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);
			optionalServerDataUICreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);
			sncServerDataUICreator.initDataBindings(bindingContext, editingDomain, serverDataStoreEntry);

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

	public SapGlobalConnectionConfigurationPage(DataBindingContext context, SapConnectionConfiguration sapConnectionConfiguration) {
		super(Messages.SapGlobalConnectionConfigurationPage_EditSapConnectionConfigurations,
				Messages.SapGlobalConnectionConfigurationPage_EditSapDestinationAndServerDataStores,
				Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAP_TOOL_SUITE_48_IMAGE));
		setDescription(Messages.SapGlobalConnectionConfigurationPage_CreateEditDeleteDestinationAndServerConnectionConfigurations);
		this.context = context;
		this.sapConnectionConfiguration = sapConnectionConfiguration;

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
		
		viewer = new TreeViewer(sashForm, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			        	
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.setInput(sapConnectionConfiguration.eResource());
		viewer.addSelectionChangedListener(this);
		viewer.expandAll();

		ScrolledComposite sc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
		
		properties = new Composite(sc, SWT.NONE);
		stackLayout = new StackLayout();
		properties.setLayout(stackLayout);
		
		createSapConnectionConfigurationTabFolder();
		createDestinationDataStoreTabFolder();
		createServerDataStoreTabFolder();

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

	private void createSapConnectionConfigurationTabFolder() {
		sapConnectionConfigurationTabFolder = new CTabFolder(properties, SWT.BORDER);
		sapConnectionConfigurationTabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem sapConnectionConfigurationDescriptionItem = new CTabItem(sapConnectionConfigurationTabFolder, SWT.NONE);
		sapConnectionConfigurationDescriptionItem.setText(Messages.SapGlobalConnectionConfigurationPage_tbtmNewItem_text);
		sapConnectionConfigurationTabFolder.setSelection(sapConnectionConfigurationDescriptionItem);

		Label lblContainsStoresFor = new Label(sapConnectionConfigurationTabFolder, SWT.WRAP);
		sapConnectionConfigurationDescriptionItem.setControl(lblContainsStoresFor);
		lblContainsStoresFor.setText(Messages.SapGlobalConnectionConfigurationPage_lblContainsStoresFor_text);
	}

	private void createDestinationDataStoreTabFolder() {
		destinationDataStoreTabFolder = new CTabFolder(properties, SWT.BORDER);
		
		CTabItem destinationDataStoreDescriptionItem = new CTabItem(destinationDataStoreTabFolder, SWT.NONE);
		destinationDataStoreDescriptionItem.setText(Messages.SapGlobalConnectionConfigurationPage_DestinationDataStoreDescritionItemTitle);
		destinationDataStoreTabFolder.setSelection(destinationDataStoreDescriptionItem);
		
		Label lblContainsSapConnection_1 = new Label(destinationDataStoreTabFolder, SWT.WRAP);
		lblContainsSapConnection_1.setText(Messages.SapGlobalConnectionConfigurationPage_lblContainsSapConnection_1_text);
		destinationDataStoreDescriptionItem.setControl(lblContainsSapConnection_1);
	}

	/**
	 * 
	 */
	private void createServerDataStoreTabFolder() {
		serverDataStoreTabFolder = new CTabFolder(properties, SWT.BORDER);
		
		CTabItem tabItem_1 = new CTabItem(serverDataStoreTabFolder, SWT.NONE);
		tabItem_1.setText(Messages.SapGlobalConnectionConfigurationPage_ServerDataStoreDescriptionItemTitle);
		serverDataStoreTabFolder.setSelection(tabItem_1);
		
		Label serverDataStoreDescriptionItem = new Label(serverDataStoreTabFolder, SWT.WRAP);
		serverDataStoreDescriptionItem.setText(Messages.SapGlobalConnectionConfigurationPage_lblContainsSapConnection_text_1);
		tabItem_1.setControl(serverDataStoreDescriptionItem);
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

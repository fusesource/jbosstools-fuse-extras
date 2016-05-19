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
import java.util.HashMap;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;
import org.fusesource.ide.sap.ui.edit.idoc.IdocItemProviderAdapterFactory;
import org.fusesource.ide.sap.ui.edit.rfc.RfcItemProviderAdapterFactory;

/**
 * SAP Global Connection Configuration Page for editing SAP Global Configuration
 * 
 * @author William Collins <punkhornsw@gmail.com>
 *
 */
public class SapGlobalConnectionConfigurationPage extends WizardPage implements ISelectionChangedListener {
	

	/**
	 * This keeps track of the editing domain that is used to track all changes
	 * to the model.
	 */
	private AdapterFactoryEditingDomain editingDomain;

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
	CTabFolder destinationDataTabFolder;
	CTabFolder serverDataTabFolder;

	private DestinationDataProperties destinationDataProperties;
	private ServerDataProperties serverDataProperties;

	private Button testButton;

	private Button deleteButton;

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
		
		createLeftPanel(sashForm);

		ScrolledComposite sc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
		
		properties = new Composite(sc, SWT.NONE);
		stackLayout = new StackLayout();
		properties.setLayout(stackLayout);
		
		createSapConnectionConfigurationTabFolder();
		createDestinationDataStoreTabFolder();
		createServerDataStoreTabFolder();

		destinationDataProperties = new DestinationDataProperties();
		destinationDataTabFolder = destinationDataProperties.createControl(properties);
		
		serverDataProperties = new ServerDataProperties();
		serverDataTabFolder = serverDataProperties.createControl(properties);

		sc.setContent(properties);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(properties.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		sashForm.setWeights(new int[] { 3, 5 });

		initActions();
		
		// Create Context Menu
		hookContextMenu();

		viewer.getTree().setFocus();
	}

	private void createLeftPanel(SashForm sashForm) {
		Composite leftPanelComposite = new Composite(sashForm, SWT.NONE);
		leftPanelComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		createLeftPanelTreeViewer(leftPanelComposite);
		createLeftPanelButtons(leftPanelComposite);
	}

	private void createLeftPanelButtons(Composite leftPanelComposite) {
		Composite buttonsComposite = new Composite(leftPanelComposite, SWT.NONE);
		buttonsComposite.setLayout(new FillLayout(SWT.VERTICAL));
		buttonsComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.TOP).create());

		createAddDestinationButton(buttonsComposite);
		createAddServerButton(buttonsComposite);
		createDeleteButton(buttonsComposite);
		createTestButton(buttonsComposite);
	}

	private void createTestButton(Composite buttonsComposite) {
		testButton = new Button(buttonsComposite, SWT.PUSH);
		testButton.setText(Messages.SapGlobalConnectionConfigurationPage_buttonTest);
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				new TestAction(SapGlobalConnectionConfigurationPage.this).run();
			}
		});
	}

	private void createDeleteButton(Composite buttonsComposite) {
		deleteButton = new Button(buttonsComposite, SWT.PUSH);
		deleteButton.setText(Messages.SapGlobalConnectionConfigurationPage_buttonDelete);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				new DeleteAction(SapGlobalConnectionConfigurationPage.this, editingDomain).run();
			}
		});
	}

	private void createAddServerButton(Composite buttonsComposite) {
		Button addServerButton = new Button(buttonsComposite, SWT.PUSH);
		addServerButton.setText(Messages.SapGlobalConnectionConfigurationPage_buttonAddServer);
		addServerButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				final NewServerAction newLocalServerAction = new NewServerAction(SapGlobalConnectionConfigurationPage.this, editingDomain);
				newLocalServerAction.setParentTarget(sapConnectionConfiguration.getServerDataStore());
				newLocalServerAction.run();
			}
		});
	}

	private void createAddDestinationButton(Composite buttonsComposite) {
		Button addDestinationButton = new Button(buttonsComposite, SWT.PUSH);
		addDestinationButton.setText(Messages.SapGlobalConnectionConfigurationPage_buttonAddDestination);
		addDestinationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				final NewDestinationAction newLocalDestinationAction = new NewDestinationAction(SapGlobalConnectionConfigurationPage.this, editingDomain);
				newLocalDestinationAction.setParentTarget(sapConnectionConfiguration.getDestinationDataStore());
				newLocalDestinationAction.run();
			}
		});
	}

	private void createLeftPanelTreeViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
			        	
		viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.setInput(sapConnectionConfiguration.eResource());
		viewer.addSelectionChangedListener(this);
		viewer.expandAll();
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
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(collection.toArray()), true);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selection = event.getSelection() instanceof IStructuredSelection ? (IStructuredSelection) event.getSelection() : StructuredSelection.EMPTY;
		updateProperties();
		updateButtons();
	}
	
	private void updateButtons() {
		Object obj = selection.getFirstElement();
		final boolean isDataStoreEntry = obj instanceof ServerDataStoreEntryImpl || obj instanceof DestinationDataStoreEntryImpl;
		testButton.setEnabled(isDataStoreEntry);
		deleteButton.setEnabled(isDataStoreEntry);
	}

	private void updateProperties() {
		Object obj = selection.getFirstElement();
		if (obj == null) {
			stackLayout.topControl = sapConnectionConfigurationTabFolder;
		} else if (obj instanceof SapConnectionConfiguration) {
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

	protected void initializeEditingDomain() {
		// Create an adapter factory that yields item providers.
		adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new RfcItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new IdocItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, new TransactionalCommandStack(), new HashMap<Resource, Boolean>());
	}

	protected void initActions() {
	    sharedImages = PlatformUI.getWorkbench().getSharedImages();

		deleteAction = new DeleteAction(this, editingDomain);
		newDestinationAction = new NewDestinationAction(this, editingDomain);
		newServerAction = new NewServerAction(this, editingDomain);
	    testAction = new TestAction(this);
	}
	
	public void setSelectionToTopElement() {
		setSelectionToViewer(Collections.singleton(sapConnectionConfiguration));
	}
	
}

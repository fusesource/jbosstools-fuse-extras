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
package org.fusesource.ide.sap.ui;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	// GAV of SAP Camel components
	public static final String CAMEL_SAP_GROUP_ID = "org.fusesource";
	public static final String CAMEL_SAP_ARTIFACT_ID = "camel-sap";
	public static final String CAMEL_SAP_VERSION = "6.2.0";

	// Image locations
	public static final String DESTINATION_DATA_STORE_IMAGE = "icons/full/obj16/DestinationDataStore.gif"; //$NON-NLS-1$
	public static final String DESTINATION_DATA_STORE_ENTRY_IMAGE = "icons/full/obj16/DestinationDataStoreEntry.gif"; //$NON-NLS-1$
	public static final String SAP_CONNECTION_CONFIGURATION = "icons/full/obj16/SapConnectionConfiguration.gif"; //$NON-NLS-1$
	public static final String SERVER_DATA_STORE_IMAGE = "icons/full/obj16/ServerDataStore.gif"; //$NON-NLS-1$
	public static final String SERVER_DATA_STORE_ENTRY_IMAGE = "icons/full/obj16/ServerDataStoreEntry.gif"; //$NON-NLS-1$
	public static final String SAP_TOOL_SUITE_16_IMAGE = "icons/sap16.png"; //$NON-NLS-1$
	public static final String SAP_TOOL_SUITE_48_IMAGE = "icons/sap.png"; //$NON-NLS-1$
	public static final String TEST_IMAGE = "icons/full/etool16/test.gif"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.sap.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static void logWarning(String message, Exception e) {
		IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, message, e);
		getDefault().getLog().log(status);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		ImageDescriptor image;
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(DESTINATION_DATA_STORE_IMAGE), null));
		getImageRegistry().put(DESTINATION_DATA_STORE_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(DESTINATION_DATA_STORE_ENTRY_IMAGE), null));
		getImageRegistry().put(DESTINATION_DATA_STORE_ENTRY_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SAP_CONNECTION_CONFIGURATION), null));
		getImageRegistry().put(SAP_CONNECTION_CONFIGURATION, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SERVER_DATA_STORE_IMAGE), null));
		getImageRegistry().put(SERVER_DATA_STORE_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SERVER_DATA_STORE_ENTRY_IMAGE), null));
		getImageRegistry().put(SERVER_DATA_STORE_ENTRY_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SAP_TOOL_SUITE_16_IMAGE), null));
		getImageRegistry().put(SAP_TOOL_SUITE_16_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SAP_TOOL_SUITE_48_IMAGE), null));
		getImageRegistry().put(SAP_TOOL_SUITE_48_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(TEST_IMAGE), null));
		getImageRegistry().put(TEST_IMAGE, image);
	}

}

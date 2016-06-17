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
package org.fusesource.ide.sap.ui.dialog;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

public class TestDestinationDialog extends TitleAreaDialog {
	
	private static final int TEST_ID = IDialogConstants.CLIENT_ID + 1;
	
	private static final int CLEAR_ID = IDialogConstants.CLIENT_ID + 2;

	private String destinationName;

	private Text text;

	private JCoException jcoException;
	private boolean isCancelled;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TestDestinationDialog(Shell parentShell, String destinationName) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		this.destinationName = destinationName;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(Activator.getDefault().getImageRegistry().get(Activator.SAP_TOOL_SUITE_16_IMAGE));
		super.configureShell(newShell);
		newShell.setText(Messages.TestDestinationDialog_shellTitle);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle(NLS.bind(Messages.TestDestinationDialog_dialogTitle, destinationName));
		setMessage(""); //$NON-NLS-1$
		setTitleImage(Activator.getDefault().getImageRegistry().get(Activator.SAP_TOOL_SUITE_48_IMAGE));

		return contents;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		text = new Text(container, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, TEST_ID, Messages.TestDestinationDialog_Test, false);
		createButton(parent, CLEAR_ID, Messages.TestDestinationDialog_Clear, false);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == TEST_ID) {
			testPressed();
		} else if (buttonId == CLEAR_ID) {
			clearConsole();
		} else if (buttonId == IDialogConstants.CLOSE_ID) {
			closePressed();
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		Point minimumSize = getShell().computeSize(550, 335, true);
		getShell().setMinimumSize(minimumSize);
		return minimumSize;
	}
	
	private void closePressed() {
		setReturnCode(OK);
		close();
	}
	
	private void testPressed() {
		testDestination();
	}
	
	private void testDestination() {

		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
			
			private int worked;
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				// Run these calls in separate thread to make them cancellable:
				// these calls can block for a significant amount of time when destination configuration is invalid!
				Thread pingDestinationThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							JCoDestination jcoDestination = JCoDestinationManager.getDestination(destinationName);
							jcoDestination.ping();
						} catch (JCoException e) {
							jcoException = e;
						}
					}
				});
				pingDestinationThread.setName(Messages.TestDestinationDialog_PingJCoDestinationThread);
				pingDestinationThread.setDaemon(true);
				pingDestinationThread.start();
				monitor.beginTask(Messages.TestDestinationDialog_PingJCoDestination, 100);
				while (pingDestinationThread.isAlive()) {
					try {
						pingDestinationThread.join(1000);
						if (monitor.isCanceled()) {
							isCancelled = true;
							return;
						}
						worked += 10;
						if (worked > 100) {
							monitor.beginTask(Messages.TestDestinationDialog_PingJCoDestination, 100);
							worked = 0;
						} else {
							monitor.worked(10);
						}
					} catch (InterruptedException e) {
						isCancelled = true;
						return;
					}
				}
			}
		};
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		isCancelled = false;
		jcoException = null;
		try {
			dialog.run(true, true, runnableWithProgress);

			if (isCancelled) {
				return;
			}
			
			if (jcoException != null) {
				append2Console(jcoException.getMessage()); // $NON-NLS-1$
				return;
			}
			append2Console(NLS.bind(Messages.TestDestinationDialog_7, destinationName));
		} catch (Exception e) {
			append2Console(e.getMessage()); // $NON-NLS-1$
		}
	}
	
	private void clearConsole() {
		text.setText(""); //$NON-NLS-1$
	}
	
	private void append2Console(String str) {
		text.append(System.lineSeparator() + str);
	}

}

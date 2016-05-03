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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
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

import com.sap.conn.jco.AbapClassException;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerFunctionHandlerFactory;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;

public class TestServerDialog extends TitleAreaDialog {
	
	private static final int START_ID = IDialogConstants.CLIENT_ID + 1;
	
	private static final int CLEAR_ID = IDialogConstants.CLIENT_ID + 2;
	
	public class CreateJCoServerJob extends Job {

		JCoServer jcoServer;
		JCoException jcoException;
		
		public CreateJCoServerJob() {
			super(Messages.TestServerDialog_CreateJCoServer);
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(Messages.TestServerDialog_CreatingJCoServer, 100);
			try {
				// Run call to JCoServerFactory.getServer() in separate thread to make cancellable:
				// this call can block for a significant amount of time when server configuration is invalid!
				Thread getServerThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							jcoServer = JCoServerFactory.getServer(serverName);
						} catch (JCoException e) {
							jcoException = e;
						}
					}
				});
				getServerThread.setName(Messages.TestServerDialog_GetJCoServerThread);
				getServerThread.setDaemon(true);
				getServerThread.start();
				while (getServerThread.isAlive()) {
					try {
						getServerThread.join(1000);
						if (monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
					} catch (InterruptedException e) {
						return Status.CANCEL_STATUS;
					}
				}
				if (jcoException != null) {
					return new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.TestServerDialog_FailedToCreateJCoServer, jcoException);
				}
				
				ServerErrorAndExceptionListener serverErrorAndExceptionListener = new ServerErrorAndExceptionListener();
				ServerStateChangedListener serverStateChangedListener = new ServerStateChangedListener();
				jcoServer.setCallHandlerFactory(new FunctionHandlerFactory());
				jcoServer.addServerErrorListener(serverErrorAndExceptionListener);
				jcoServer.addServerExceptionListener(serverErrorAndExceptionListener);
				jcoServer.addServerStateChangedListener(serverStateChangedListener);

				if (monitor.isCanceled()) {
					jcoServer.stop();
					jcoServer.release();
					isServerInitialize = false;
					return Status.CANCEL_STATUS;
				}
				
				isServerInitialize = true;
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
		
	}
	
	public class FunctionHandlerFactory implements JCoServerFunctionHandlerFactory {
		
		public class FunctionHandler implements JCoServerFunctionHandler {

			@Override
			public void handleRequest(JCoServerContext arg0, JCoFunction arg1) throws AbapException, AbapClassException {
				
			}
			
		}
		
		FunctionHandler functionHandler = new FunctionHandler();

		@Override
		public void sessionClosed(JCoServerContext context, String message, boolean error) {
		}

		@Override
		public JCoServerFunctionHandler getCallHandler(JCoServerContext context, String functionName) {
			return functionHandler;
		}
		
	}
	
	public class ServerErrorAndExceptionListener implements JCoServerErrorListener, JCoServerExceptionListener {

		@Override
		public void serverExceptionOccurred(JCoServer jcoServer, String connectionId,
				JCoServerContextInfo serverContext, Exception exception) {
			append2Console(NLS.bind(Messages.TestServerDialog_ExceptionOccuredOnConnection, new Object[] { jcoServer.getProgramID(), connectionId, exception.getMessage() }));
		}

		@Override
		public void serverErrorOccurred(JCoServer jcoServer, String connectionId,
				JCoServerContextInfo serverContext, Error error) {
			append2Console(NLS.bind(Messages.TestServerDialog_ErrorOccuredOnConnection, new Object[] { jcoServer.getProgramID(), connectionId, error.getMessage() }));
		}
		
	}

	public class ServerStateChangedListener implements JCoServerStateChangedListener {

		@Override
		public void serverStateChangeOccurred(JCoServer jcoServer,
				JCoServerState oldState, JCoServerState newState) {
			append2Console(NLS.bind(Messages.TestServerDialog_ServerStateChanged, new Object[] {oldState.toString(), newState.toString(), jcoServer.getProgramID() }));
		}

	}
	
	private void startServerStatePoller() {
		if (serverStatePoller == null) {
			serverStatePoller = new ServerStatePoller();
			new Thread(serverStatePoller).start();
		}
	}
	
	private void stopServerStatePoller() {
		if (serverStatePoller != null) {
			serverStatePoller.stop();
			serverStatePoller = null;
		}		
	}
	
	public class ServerStatePoller implements Runnable {
		
		private boolean isStopped;
		
		public void stop() {
			isStopped = true;
		}
		
		@Override
		public void run() {
			while(!isStopped) {
				checkState();
				pause(1000);
			}
			// One more time with feeling!
			pause(2000);
			checkState();
		}
		
	}
	
	private String serverName;

	private Text text;
	
	private ServerStatePoller serverStatePoller;

	private boolean isServerInitialize;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TestServerDialog(Shell parentShell, String serverName) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		this.serverName = serverName;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(Activator.getDefault().getImageRegistry().get(Activator.SAP_TOOL_SUITE_16_IMAGE));
		super.configureShell(newShell);
		newShell.setText(Messages.TestServerDialog_TestServerConnection);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle(NLS.bind(Messages.TestServerDialog_TestServerXConnection, serverName));
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
		
//		createJCoServer();
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, START_ID, Messages.TestServerDialog_Start, false);
		createButton(parent, IDialogConstants.STOP_ID, IDialogConstants.STOP_LABEL, false);
		createButton(parent, CLEAR_ID, Messages.TestServerDialog_Clear, false);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == START_ID) {
			startServer();
		} else if (buttonId == IDialogConstants.STOP_ID) {
			stopServer();
		} else if (buttonId == CLEAR_ID) {
			clearConsole();
		} else if (buttonId == IDialogConstants.CLOSE_ID) {
			closePressed();
		}
	}
	
	@Override
	public boolean close() {
		stopServer();
		return super.close();
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
		
	private boolean createJCoServer() {
		IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {

			private int worked;
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask(Messages.TestServerDialog_CreatingJCoServer, 100);
				try {
					CreateJCoServerJob job = new CreateJCoServerJob();
					job.schedule();
					while (!monitor.isCanceled()) {
						try {
							if (job.join(1000, monitor)) {
								return;
							}
							worked += 10;
							if (worked > 100) {
								monitor.beginTask(Messages.TestServerDialog_CreatingJCoServer, 100);
								worked = 0;
							} else {
								monitor.worked(10);
							}
						} catch (OperationCanceledException e) {
							job.cancel();
							return;
						}
					}
				} finally {
					monitor.done();
				}
			}

		};
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, runnableWithProgress);
			if (dialog.getReturnCode() == Window.CANCEL) {
				return false;
			}
		} catch (Exception e) {
			append2Console(NLS.bind(Messages.TestServerDialog_FailedToCreateServer, e.getMessage()));
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private JCoServer getJCoServer() {
		try {
			if (!isServerInitialize) {
				if (!createJCoServer()) {
					return null;
				}
			}
			JCoServer jcoServer = JCoServerFactory.getServer(serverName);
			jcoServer.setCallHandlerFactory(new FunctionHandlerFactory());
			return jcoServer;
		} catch (Exception e) {
			append2Console(NLS.bind(Messages.TestServerDialog_FailedToGetServer, e.getMessage()));
		}
		return null;
	}
	
	private void checkState() {
		final JCoServer jcoServer = getJCoServer();
		if (jcoServer != null) {
			try {
				getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						append2Console(NLS.bind(Messages.TestServerDialog_ServerState, jcoServer.getState()));
					}
				});
			} catch (Exception e) {
				// Ignore;
			}
		}
	}
	
	private void pause(int pause) {
		try {
			Thread.sleep(pause);
		} catch (InterruptedException e) {
		}
	}
	
	private void startServer() {
		JCoServer jcoServer = getJCoServer();
		if (jcoServer != null) {
			try {
				jcoServer.start();
				startServerStatePoller();
			} catch (Exception e) {
				append2Console(NLS.bind(Messages.TestServerDialog_ErrorStartingServer, e.getMessage()));
			}
		}
	}
	
	private void stopServer() {
		if (!isServerInitialize) {
			return;
		}

		JCoServer jcoServer = getJCoServer();
		if (jcoServer != null) {
			try {
				jcoServer.stop();
				stopServerStatePoller();
			} catch (Exception e) {
				append2Console(NLS.bind(Messages.TestServerDialog_ErrorStoppingServer, e.getMessage()));
			}
		}
	}
		
	private void append2Console(final String str) {
		getShell().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				String log = text.getText();
				log += System.lineSeparator() + str;
				text.setText(log);
			}
		});
	}

	private void clearConsole() {
		text.setText(""); //$NON-NLS-1$
	}

}

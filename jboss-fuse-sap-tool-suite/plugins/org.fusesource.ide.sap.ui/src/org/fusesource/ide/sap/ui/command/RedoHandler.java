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
package org.fusesource.ide.sap.ui.command;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.fusesource.ide.sap.ui.view.SapConnectionsView;

public class RedoHandler extends AbstractHandler implements IHandler, IElementUpdater {

	private EditingDomain editingDomain;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		editingDomain.getCommandStack().redo();
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		Object obj = HandlerUtil.getVariable(evaluationContext, ISources.ACTIVE_PART_NAME);
		if (obj instanceof SapConnectionsView) {
			SapConnectionsView view = (SapConnectionsView) obj;
			editingDomain = view.getEditingDomain();
			if (editingDomain.getCommandStack().canRedo()) {
				setBaseEnabled(true);
				return;
			}
		}
		setBaseEnabled(false);
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		Command redoCommand = editingDomain.getCommandStack().getRedoCommand();
		if (redoCommand != null && redoCommand.getLabel() != null) {
			element.setText("&Redo " + redoCommand.getLabel());
		} else {
			element.setText("&Redo");
		}

	}

}

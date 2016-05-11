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
package org.fusesource.ide.sap.ui.tests.integration.util;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.rules.ExternalResource;

/**
 * @author Aurelien Pupier
 *
 */
public class FuseProject extends ExternalResource {

	private IProject project = null;
	private String projectName;

	public FuseProject(String projectName) {
		this.projectName = projectName;
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		project = ws.getRoot().getProject(projectName);
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		// Create a fake pom.xml
		IFile pom = project.getFile("pom.xml");
		pom.create(new ByteArrayInputStream("".getBytes()), true, new NullProgressMonitor());
	}

	@Override
	protected void after() {
		super.after();
		if (project != null && project.exists()) {
			try {
				project.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public IProject getProject() {
		return project;
	}

}

/*******************************************************************************
* Copyright (c) 2016 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/
package org.fusesource.ide.sap.ui.util;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLUtils {

	public static Element getFirstChildElement(Element parent) {
		if (parent == null) {
			return null;
		}
		
		for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) n;
			}
		}
		
		return null;
	}
	
	public static Element getFirstChildElementWithName(Element elem, String name) {
		if (elem == null) {
			return null;
		}
		
		for (Element e = getFirstChildElement(elem); e != null; e = getNextSiblingElement(e)) {
			if (e.getTagName().equals(name)) {
				return e;
			}
		}
		
		return null;
	}

	public static Element getNextSiblingElement(Element elem) {
		if (elem == null) {
			return null;
		}
		
		for (Node n = elem.getNextSibling(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) n;
			}
		}
		
		return null;
	}

	public static Element getNextSiblingElementWithName(Element elem, String name) {
		if (elem == null) {
			return null;
		}

		for (Element e = getNextSiblingElement(elem); e != null; e = getNextSiblingElement(e)) {
			if (e.getTagName().equals(name)) {
				return (Element) e;
			}
		}
		
		return null;
	}
	
	public static Element getPreviousSiblingElement(Element elem) {
		if (elem == null) {
			return null;
		}
		
		for (Node n = elem.getPreviousSibling(); n != null; n = n.getPreviousSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) n;
			}
		}
		
		return null;
	}

	public static Element getLastChildElement(Element elem) {
		if (elem == null) {
			return null;
		}
		
		for (Node n = elem.getLastChild(); n != null; n = n.getPreviousSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) n;
			}
		}
		
		return null;
	}

	public static Element lastChildElementWithName(Element elem, String name) {
		if (elem == null) {
			return null;
		}
		
		for (Element e = getLastChildElement(elem); e != null; e = getPreviousSiblingElement(e)) {
			if (e.getTagName().equals(name)) {
				return (Element) e;
			}
		}
		
		return null;
	}
	
	public static void removeChildNodes(Node parent) {
		while(parent.hasChildNodes()) {
			parent.removeChild(parent.getFirstChild());
		}
	}
	
	public static String getAttributeValue(Node node, String attributeName) {
		if (node == null || attributeName == null) {
			return null;
		}
		
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null) {
			return null;
		}
		
		Node attribute = attributes.getNamedItem(attributeName);
		if (attribute != null) {
			return attribute.getNodeValue();
		}
		return null;
	}

	public static boolean hasAttributeValue(Node node, String attributeName, String attributeValue) {
		if (node == null || attributeName == null) {
			return false;
		}
		
		String value = getAttributeValue(node, attributeName);
		if (value == null) {
			return (attributeValue == null) ? true : false; 
		}
		
		if (value.equals(attributeValue)) {
			return true;
		}
		
		return false;
	}
}

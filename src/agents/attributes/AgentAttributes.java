/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package agents.attributes;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This is a class that defines all the attributes of the agent.
 */
abstract public class AgentAttributes {

	/*
	 * Current version for serialisation of the class.
	 */
	private HashMap<Object, Object> attributes;

	protected AgentAttributes() {
		attributes = new HashMap<Object, Object>();
	}

	public void addAttribute(Object attributeName, Object attributeValue) {
		attributes.put(attributeName, attributeValue);
	}

	public Object getAttribute(Object attributeName) {
		return attributes.get(attributeName);
	}
}

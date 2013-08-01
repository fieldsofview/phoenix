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

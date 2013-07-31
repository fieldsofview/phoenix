package agents.behaviour;

import agents.attributes.AgentAttributes;

/**
 * The behaviour is a single task that can be run. Each task is thus stated as a
 * thread to allow for running multiple behaviours as a group of java threads.
 */
public interface Behaviour {

	/**
	 * Each behaviour is run as a single thread.
	 * 
	 * @param agentAttributes
	 *            the attribute values required to execute the behaviour.
	 */
	public void run(AgentAttributes agentAttributes);

}

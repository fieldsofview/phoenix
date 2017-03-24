/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package examples.helloworldSimulation;

import agents.attributes.AgentAttributes;
import agents.behaviour.Behaviour;

public class TestAgentBehaviour implements Behaviour {

	@Override
	public void run(AgentAttributes agentAttributes) {
		System.out.println("TestAgentBehaviour");
	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package communication.queueManager;

import communication.QueueParameters;
import communication.messages.Message;

public class AgentQueueManagement extends QueueManager {

    @Override
    protected Message addQueueListener(QueueParameters queueParameters) {
        return null;
        // TODO Auto-generated method stub

    }

    @Override
    protected void createConnectionAndChannel() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void exitMessaging() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean send(String host, Message message) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void processMessage(Message receivedMessage) {
        // TODO Auto-generated method stub

    }

}

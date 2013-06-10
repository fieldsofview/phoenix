/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package communication.messages;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * This class represents a message template which is used to create the
 * different types of messages required for exchange during the simulation.
 */

public class Message {

	public Object messageObject;
	/**
	 * The destination host-name for the message
	 */
	public String hostName;
	/**
	 * The time when the message is sent
	 */
	public Timestamp timestamp;

	public Message() {
		createMessage();
	}

	private void createMessage() {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		this.timestamp = new Timestamp(now.getTime());
		this.messageObject = null;
	}
}

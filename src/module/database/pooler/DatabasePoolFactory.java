/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package module.database.pooler;

import java.util.Properties;

public class DatabasePoolFactory {
	public static DatabasePooler getPooler(String poolerClass, Properties p) {
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(poolerClass);
			return ((DatabasePooler) c.newInstance()).initialise(p);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

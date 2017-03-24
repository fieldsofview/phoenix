/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package module.database.pooler;


import java.util.Properties;

public class DatabasePoolFactory {
	public static DatabasePooler getPooler(String poolerClass, Properties p){
		System.out.println("Loading pooler: "+poolerClass);
		if (poolerClass.equals("module.database.pooler.BoneCPPooler")){
			return BoneCPPooler.getInstance(p);
		}
		return null;
	}
}

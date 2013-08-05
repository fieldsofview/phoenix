/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package module.database.pooler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author onkar
 * @version 0.1 This is the interface for database pooling. Classes for database
 *          Pooling need to implement this interface.
 */
public interface DatabasePooler {
	public DatabasePooler initialise(Properties p);

	public Connection getConnection() throws SQLException;
	
        public void shutdown();
        
        public void printStatistics();
        
        @Override
	public String toString();
}

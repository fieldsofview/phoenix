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
        
        @Override
	public String toString();
}
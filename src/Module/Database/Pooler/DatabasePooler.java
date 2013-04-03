package Module.Database.Pooler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author onkar
 * @version 0.1 This is the interface for Database pooling. Classes for Database
 *          Pooling need to implement this interface.
 */
public interface DatabasePooler {
	public DatabasePooler initialise(Properties p);

	public Connection getConnection() throws SQLException;
}

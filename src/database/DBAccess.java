package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//TODO: Put the output to logs
/**
 * Class to access database
 */
public class DBAccess {

	private final String URL;
	private final String DBUsername;
	private final String DBPassword;
	@SuppressWarnings("unused")
	private final String hostName;
	/**
	 * Statement must be set before querying the database
	 */
	public String Query;
	/**
	 * Update statement must be set before using
	 */
	public String Update;
	private Statement stmt;
	private Connection con;
	private ResultSet result_query;
	private Integer result_update;
	@SuppressWarnings("unused")
	private String WWWf;

	/**
	 * Creates a new instance of DBAccess
	 * 
	 * @param host
	 *            host name for the server
	 * @param schema
	 *            The database to be used
	 * @param dbpwd
	 *            Password for the user
	 * @param dbuname
	 *            User name to connect to the database
	 */
	public DBAccess(String host, String schema, String dbuname, String dbpwd) {
		hostName = host;
		URL = "jdbc:postgresql://" + host + "/" + schema;
		DBUsername = dbuname;
		DBPassword = dbpwd;
		stmt = null;
		con = null;

		LoadDriver();
		ConnectToDB();
	}

	/**
	 * Load Class driver
	 */
	private void LoadDriver() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception e) {
			System.err.println("Failed to load Connector/J");
			System.out
					.println("Check PostgreSql Connector/J... Restart server.");
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the database
	 */
	private void ConnectToDB() {
		try {
			con = java.sql.DriverManager.getConnection(URL, DBUsername,
					DBPassword);
			stmt = con.createStatement();
		} catch (Exception e) {
			System.err.println("Problems connecting to " + URL);
			System.out
					.println("Check if PostgreSQL daemon is running... Restart server.");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the result set for the query
	 */
	public ResultSet ExecQuery() {
		if (Query == null) {
			throw new NullPointerException("Query hasn't been initialized");
		}
		try {
			result_query = stmt.executeQuery(Query);
			return result_query;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return the number of elements that are modified
	 */
	public Integer ExecUpdate() {
		if (Update == null) {
			throw new NullPointerException(
					"Update statement hasn't been initialized");
		}
		try {
			result_update = stmt.executeUpdate(Update);
			return result_update;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Close the connection to the DB
	 */
	public void CloseConnection() {
		try {
			con.close();
		} catch (Exception e) {
			System.err.println("Error closing connection to " + URL);
			System.out
					.println("Check if PostgreSQL daemon is running.. Restart server");
		}
	}
}

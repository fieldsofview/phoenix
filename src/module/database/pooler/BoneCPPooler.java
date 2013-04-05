package module.database.pooler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * This class implements DBPooler and is an implementation of BoneCP pooling.
 *
 * @author onkar
 *
 */
public class BoneCPPooler implements DatabasePooler {

    private static BoneCPPooler boneCPPooling = null;
    private BoneCP bone = null;
    int MIN_CONNECTIONS;
    int MAX_CONNECTIONS;
    String username;
    String password;
    String host;
    String database;
    String driver;

    /**
     * Protected constructor for singleton
     */
    protected BoneCPPooler(Properties p) {
        try {
            MIN_CONNECTIONS = new Integer(p.getProperty("min"));
            MAX_CONNECTIONS = new Integer(p.getProperty("max"));
            username = p.getProperty("username").toString();
            password = p.getProperty("password").toString();
            database = p.getProperty("database").toString();
            host = p.getProperty("host").toString();
            driver = p.getProperty("driver").toString();

            // Initialise database driver
            Class.forName("com.mysql.jdbc.Driver");
            BoneCPConfig boneCPConfig = new BoneCPConfig();
            boneCPConfig.setJdbcUrl("jdbc:mysql://" + host + "/" + database);
            boneCPConfig.setUsername(username);
            boneCPConfig.setPassword(password);
            boneCPConfig.setMinConnectionsPerPartition(MIN_CONNECTIONS);
            boneCPConfig.setMaxConnectionsPerPartition(MAX_CONNECTIONS);
            boneCPConfig.setPartitionCount(1);
            bone = new BoneCP(boneCPConfig);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DatabasePooler initialise(Properties p) {
        return getInstance(p);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return bone.getConnection();
    }

    public static DatabasePooler getInstance(Properties p) {
        if (null == boneCPPooling) {
            System.out.println("Instantiating pooler singleton.");
            boneCPPooling = new BoneCPPooler(p);
            return boneCPPooling;
        }
        System.out.println("Pooler present; returning pooler.");
        return boneCPPooling;
    }

    @Override
    public String toString() {
        return "Bone CP Pooler for Phoenix.";
    }

    @Override
    public void shutdown() {
        System.out.println("Shutting down BoneCP pooler.");
        bone.shutdown();
    }

    @Override
    public void printStatistics() {
        System.out.println("Total created connections: "+bone.getTotalCreatedConnections());
        System.out.println("Total free connections: "+bone.getTotalFree());
        System.out.println("Total leased connections: "+bone.getTotalLeased());
    }
}

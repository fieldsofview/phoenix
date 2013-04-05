package module.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import module.Module;
import module.database.pooler.DatabasePoolFactory;
import module.database.pooler.DatabasePooler;

public class DatabaseModule implements Module {

    DatabasePooler pooler = null;
    Properties p = null;
    public String updateQuery;
    public String query;

    public DatabaseModule() {
        //First read properties file
        System.out.println("Booting database module.\nLoading properties file.");
        p = new Properties();
        try {
            p.load(new FileInputStream("config/database.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Next call boot
        boot();
    }

    /**
     * Do boot-time checking of dependencies
     */
    @Override
    public void boot() {
        System.out.println("Running boot and depdendency check.");
        //Dependency check
        try {
            ClassLoader l = ClassLoader.getSystemClassLoader();
            //Class.forName("com.jdbc.mysql.Driver",false,l);
            //Class.forName("com.jolbox.bonecp.BoneCP",false,l);
            Class.forName("org.slf4j.Logger", false, l);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //TODO add error log
            //TODO system.exit
        }
        //After successful boot call initialise
        initialise();
    }

    @Override
    public void initialise() {
        System.out.println("Initialising database module and pooler.");
        try {
            pooler = DatabasePoolFactory.getPooler(p.getProperty("pooler").toString(), p);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public ResultSet execute() {
        try {
            ResultSet rs;
            try (Connection c = this.pooler.getConnection()) {
                Statement s = c.prepareStatement(query);
                rs = s.executeQuery(query);
            }
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer executeUpdate() {
        try {
            Connection c = this.pooler.getConnection();
            return c.prepareStatement(updateQuery).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

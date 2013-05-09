package module.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import module.Module;
import module.database.pooler.DatabasePoolFactory;
import module.database.pooler.DatabasePooler;


public class DatabaseModule implements Module{

	DatabasePooler pooler=null;
	Properties p=null;
	
	public DatabaseModule() {
		//First read properties file
		Properties p=new Properties();
		try {
			p.load(new FileInputStream("database.properties"));
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
		//Dependency check
		try {
			ClassLoader l=ClassLoader.getSystemClassLoader();
			Class.forName("com.jdbc.mysql.Driver",false,l);
			Class.forName("com.jolbox.bonecp.BoneCP",false,l);
			Class.forName("org.slf4j.Logger",false,l);
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
		pooler=DatabasePoolFactory.getPooler(p.getProperty("pooler").toString(),p);
	}

}

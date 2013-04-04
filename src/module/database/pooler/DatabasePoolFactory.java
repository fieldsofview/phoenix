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

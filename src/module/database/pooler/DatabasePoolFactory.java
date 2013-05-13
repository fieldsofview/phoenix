package module.database.pooler;

import java.util.Properties;

public class DatabasePoolFactory {
	public static DatabasePooler getPooler(String poolerClass, Properties p){
		try {
			@SuppressWarnings("rawtypes")
			Class c= Class.forName(poolerClass);
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

package Server.DAL;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DBController {
	public enum DBtype {SQL,MongoDB};
	private static DBController dbController = null;
	private DBServicesInterface dbServicesInterface = null;
	private final String SQL_CONF_FILE = "configSqlDB.xml";
	private final String MONGODB_CONF_FILE = "configMongoDB.xml";
	private ApplicationContext applicationContext;
	
	private DBController() {
	}
	
	public static DBController getInstance() {
		if(dbController == null) {
			dbController = new DBController();
		}
		
		return dbController;
	}

	public DBServicesInterface getDbServicesInterface() {
		if(dbServicesInterface == null) {
			applicationContext = new ClassPathXmlApplicationContext(SQL_CONF_FILE);
			dbServicesInterface =(DBServicesInterface)applicationContext.getBean("SqlDB");
		}
		return dbServicesInterface;
	}

	public void setDbServicesInterface(DBtype choice) {
		switch (choice) {
		case SQL:
			applicationContext = new ClassPathXmlApplicationContext(SQL_CONF_FILE);
			dbServicesInterface =(DBServicesInterface)applicationContext.getBean("SqlDB");
			break;
		case MongoDB:
			applicationContext = new ClassPathXmlApplicationContext(MONGODB_CONF_FILE);
			dbServicesInterface =(DBServicesInterface)applicationContext.getBean("MongoDB");
			break;

		default:
			break;
		}
	}
	
	

}

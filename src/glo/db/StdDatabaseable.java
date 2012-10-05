package glo.db;

import java.util.Hashtable;

public abstract class StdDatabaseable implements Databaseable {
	/**
	 * Inserts this type into the database
	 * @return
	 */
	public boolean insert(){
		boolean result = false;
		Hashtable data = getData();
		String tableName = getTableName();
		result = DbManager.insert(tableName, data);
		return result;
	}
	
	/**
	 * Deletes this type from the database 
	 * @return
	 */
	public boolean delete(){
		boolean result = false;
		Hashtable data = getData();
		String tableName = getTableName();
		result = DbManager.remove(tableName, data);
		return result;
	}
	
	public boolean update(){
		return false;
	}
	
	public boolean existsInDb(){
		return false;
	}
	
	public boolean read(){
		return false;
	}
	
	
}

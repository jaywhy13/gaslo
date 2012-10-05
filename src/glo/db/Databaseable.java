package glo.db;

import java.util.Hashtable;

/**
 * Can be inserted and retrieved from the database using the Db class. 
 * @author JMWright
 *
 */
public interface Databaseable {
	
	/**
	 * A hash table of the values to be inserted into the database with their columns 
	 * @return
	 */
	public Hashtable getData();

	/**
	 * Returns the name of the database table 
	 * @return
	 */
	public String getTableName();
	
	/**
	 * Returns the schema definition for the class
	 * @return
	 */
	public SchemaDefinition getSchema();
	
}


package glo.db;

/**
 * Listens to DB events 
 * @author JMWright
 *
 */
public interface DbManagerListener {
	public void databaseActivity(DbManagerEvent event);
	
}

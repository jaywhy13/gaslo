package glo.db;

public interface SchemaManagerListener {
	public void schemaRegistrationFailed(String className, Exception e);
	
	public void schemaRegistered(SchemaManager sm);
	
	public void schemaReady(SchemaManager sm);

	public void schemaCreated(SchemaManager schemaManager);
	
}

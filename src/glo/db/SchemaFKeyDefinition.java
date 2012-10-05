package glo.db;

public class SchemaFKeyDefinition {
	private SchemaField local;
	
	private SchemaField foreign;
	
	public SchemaFKeyDefinition(SchemaField local, SchemaField foreign){
		this.local = local;
		this.foreign = foreign;
	}
	
	public boolean isValid(){
		return this.local.getSchema() != null && this.foreign.getSchema() != null;
	}

	public String asSQL() {
		String result = "";
		if(isValid()){
			String localColName = local.getName();
			String foreignColName = foreign.getName();
			String foreignSchemaName = foreign.getSchema().getTableName();
			
			result = " FOREIGN KEY (" + localColName + ") REFERENCES " + foreignSchemaName + " (" + foreignColName + ")";
		}
		
		return result;
	}
	
}

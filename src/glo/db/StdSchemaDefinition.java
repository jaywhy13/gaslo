package glo.db;

import java.util.Vector;

public class StdSchemaDefinition extends SchemaDefinition {
	
	protected Vector fields = new Vector();

	private String tableName;
	
	private Vector fKeyDefs = new Vector();

	
	public StdSchemaDefinition(String tableName){
		this.tableName = tableName;
	}
	
	public void addField(SchemaField f) {
		f.setSchema(this);
		fields.addElement(f);
	}

	public Vector getFields() {
		return fields;
	}

	public SchemaField getFieldByIndex(int index) {
		if(fields.size() > index){
			return (SchemaField) fields.elementAt(index);
		}
		return null;
	}

	public SchemaField getFieldByName(String name) {
		SchemaField result = null;
		for(int i = 0; i < fields.size(); i++){
			SchemaField f = getFieldByIndex(i);
			if(f.getName().equals(name)){
				result = f;
				break;
			}
		}
		return result;
	}

	public String getTableName() {
		return tableName;
	}

	public void setFields(Vector fields) {
		this.fields = fields;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void addForeignKey(SchemaFKeyDefinition def) {
		fKeyDefs.addElement(def);
	}
	
	/**
	 * Returns true if the schema has a primary key defined 
	 */
	public boolean hasPrimaryKey() {
		boolean result = false;
		Vector fields = getFields();
		for(int i = 0; i < fields.size(); i++){
			SchemaField field = (SchemaField) fields.elementAt(i);
			if(field.isPrimary()){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public SchemaField getPrimaryKey(){
		Vector fields = getFields();
		for(int i = 0; i < fields.size(); i++){
			SchemaField field = (SchemaField) fields.elementAt(i);
			if(field.isPrimary()){
				return field;
			}
		}
		return null;
	}

	public boolean hasForeignKeys() {
		for (int i = 0; i < fKeyDefs.size(); i++) {
			SchemaFKeyDefinition def = (SchemaFKeyDefinition) fKeyDefs
					.elementAt(i);
			if (def.isValid()) {
				return true;
			}
		}
		return false;
	}

	public Vector getForeignKeySql() {
		Vector result = new Vector();

		if (hasForeignKeys()) {
			for (int i = 0; i < fKeyDefs.size(); i++) {
				SchemaFKeyDefinition def = (SchemaFKeyDefinition) fKeyDefs
						.elementAt(i);
				if (def.isValid()) {
					result.addElement(def.asSQL());
				}
			}
		}

		return result;

	}	
	

}

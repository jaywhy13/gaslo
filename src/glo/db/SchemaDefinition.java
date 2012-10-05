package glo.db;

import java.util.Vector;

public abstract class SchemaDefinition {
	
	public abstract Vector getFields();
	
	public abstract void addField(SchemaField f);
	
	public abstract SchemaField getFieldByName(String name);
	
	public abstract SchemaField getFieldByIndex(int index);
	
	public abstract String getTableName();
	
	
	public abstract void addForeignKey(SchemaFKeyDefinition def);
	
	public abstract boolean hasForeignKeys();
	
	public abstract boolean hasPrimaryKey();
	
	public abstract SchemaField getPrimaryKey();
	
	public abstract Vector getForeignKeySql();
	
	/**
	 * Returns the schema definition as SQL
	 * @return
	 */
	public String asSQL(){
		String result = " CREATE TABLE " + getTableName() + " (";
		
		for(int i = 0; i < getFields().size(); i++){
			SchemaField f = getFieldByIndex(i);
			result += f.asSQL();
			if(i < getFields().size() - 1){
				result += ", ";
			}
		}
		
		if(hasForeignKeys()){
			result += ", ";
			for(int j = 0; j < getForeignKeySql().size(); j++){
				result += getForeignKeySql().elementAt(j).toString();
				if(j < getForeignKeySql().size() - 1){
					result += ", ";
				}
			}
		}
		
		result += " );";
		return result;
		
	} 
	
	
	
}

package glo.db;

import java.util.Vector;

public class SchemaField {

	public static final int TYPE_INTEGER = 1;
	public static final int TYPE_VARCHAR = 2;
	public static final int TYPE_TEXT = 3;
	public static final int TYPE_DATE = 4;
	public static final int TYPE_DECIMAL = 5;
	
	public static final int NOTNULL = 0x00000001;
	public static final int TYPE_BOOLEAN = 6;

	private boolean primary = false;


	private String name;

	private int type;

	private int flags;

	private SchemaDefinition schema;

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public SchemaField(String name, int type) {
		super();
		this.name = name;
		this.type = type;
	}

	public SchemaField(String name, int type, int flags) {
		super();
		this.name = name;
		this.type = type;
		this.flags = flags;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public SchemaDefinition getSchema() {
		return schema;
	}

	public void setSchema(SchemaDefinition schema) {
		this.schema = schema;
	}


	public String asSQL() {
		String result = this.name + " "
				+ SchemaManager.getDatabaseTypeName(getType())
				+ (isPrimary() ? " PRIMARY KEY" : "");
		return result;
	}

}

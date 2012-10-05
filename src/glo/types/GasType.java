package glo.types;

import java.util.Hashtable;
import java.util.Vector;

import glo.db.SchemaDefinition;
import glo.db.SchemaManager;
import glo.db.StdDatabaseable;
import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;
import glo.schema.GLSchemaManager;
import glo.sys.GLSettings;

/**
 * This class represents a gas type with all that is necessary to configure
 * each type to properly represent a type of gas.
 * @author rahibbert
 *
 */
public class GasType extends StdDatabaseable {
	
	/**
	 * The id of the gas type
	 */
	private int id;

	/**
	 * The alias of the gas type
	 */
	private String alias;
	
	/**
	 * The complete name of the gas type
	 */
	private String name;
	
	/**
	 * A description of the gas type
	 */
	private String description;
	
	/**
	 * 
	 */
	public static Vector lastAddedTypes = new Vector();
	
	/**
	 * Constructor function which only requires an id and name
	 * to make an object.
	 * @param id - gas type id
	 * @param name - gas type name
	 */
	public GasType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Returns the gas type alias
	 * @return String
	 */
	public String getAlias() {
		return alias;
	}
	
	/**
	 * Sets the gas type alias
	 * @param alias - gas type alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	/**
	 * Returns the gas type description
	 * @return String
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the gas type description
	 * @param description - gas type description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the gas type id
	 * @return int
	 */
	public int getId() {
		return id;
	}
	
	/**
	 *	Sets the gas type id 
	 * @param id - gas type id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the gas type name
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the gas type name
	 * @param name - gas type name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Parses JSON Data to create GasType Objects and add the necessary
	 * additional information to the objects. All objects are placed in a
	 * static array to which stores all gas types.
	 * @param arr - JSONArray with gas type information
	 */
	public static void fromJSON(JSONArray arr) {
		System.out.println("GL [II] Parsing JSON Gas type information");
		if(arr != null){
			try {
				JSONArray outer = arr;
				if(outer != null){
					int outerLength = outer.length();
					lastAddedTypes.removeAllElements();
					for(int a=0; a<outerLength ; a++){
						JSONObject inner = (JSONObject) outer.get(a);
						if(inner != null) {
							int gasTypeid = inner.getInt(GLSettings.GT_ID);
							String gasTypesAlias = inner.getString(GLSettings.GT_ALIAS);
							String gasTypesName = inner.getString(GLSettings.GT_NAME);
							String gasTypesDescription = inner.getString(GLSettings.GT_DESCRIPTION);
							GasType currentType = new GasType(gasTypeid,gasTypesName);
							if(!gasTypesAlias.equals(null)){
								currentType.setAlias(gasTypesAlias);
							}
							if(!gasTypesDescription.equals(null)){
								currentType.setDescription(gasTypesDescription);
							}
							GasDataManager.getInstance().addGasTypeWithIndex(currentType, gasTypeid);
							lastAddedTypes.addElement(currentType);
						}
					}
					// Used to minimize storage of vector
					GasDataManager.getInstance().getGasTypes().trimToSize();
				}
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Hashtable getData() {
		Hashtable data = new Hashtable();
		data.put("id",new Integer(getId()));
		data.put("name",getName());

		return data;
	}

	public SchemaDefinition getSchema() {
		SchemaManager sm = SchemaManager.getManager();
		if(sm != null){
			return sm.getSchema(GLSchemaManager.SCHEMA_GAS_TYPE);
		} else {
			System.out.println("GL [EE] GasType could not find Schema Manager");
		}
		return null;
	}
	
	public String getTableName() {
		return "GSType";
	}
	
	public String toString() {
		return name + ": " + description;
	}
}

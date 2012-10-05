package glo.json;

import net.rim.device.api.ui.component.TreeField;

public class ParseJSON{
	private TreeField treeField;
	
	public ParseJSON(Object JSONData)
	{
		setTree(JSONData);
	}
	
	void setTree(Object obj)
	{
		int parentNode = 0;
		treeField.deleteAll();
		
		try{
			if(obj instanceof JSONArray)
			{
				parentNode = populateTreeArray(treeField, (JSONArray) obj, parentNode);
			}
			else if(obj instanceof JSONObject)
			{
				parentNode = populateTreeObject(treeField, (JSONObject) obj, parentNode);
			}
		}
		catch(JSONException e)
		{
			System.out.println(e.toString());
		}
	}
	
	// Populate the trees with JSON arrays
	int populateTreeArray(TreeField tree, JSONArray o, int p) throws JSONException
	{
		Object temp;
		
		int newParent = tree.addChildNode(p, "Array" + p);
		
		for(int b=0; b < o.length(); b++)
		{
			temp = o.get(b);
			
			if(temp == null || temp.toString().equalsIgnoreCase("null"))
			{
				continue;
			}
			
			if(temp instanceof JSONArray)
			{
				// Array of arrays
				populateTreeArray(tree, (JSONArray) temp, newParent);
			}
			else if(temp instanceof JSONObject)
			{
				// Array of Objects
				populateTreeObject(tree, (JSONObject) temp, newParent);
			}
			else
			{ // Other values
				newParent = tree.addSiblingNode(newParent, temp.toString());
			}
			
		}	
		return newParent;
	}
	
	// Populate the trees with JSON Objects
	int populateTreeObject(TreeField tree, JSONObject o, int p) throws JSONException
	{
		Object temp;	
		int newParent = tree.addChildNode(p, "Object" + p);
		JSONArray a = o.names();
		
		for(int c=0; c < a.length(); c++)
		{
			temp = o.get(a.getString(c));
			
			if(temp == null || temp.toString().equalsIgnoreCase("null"))
			{
				continue;
			}
			
			if(temp instanceof JSONArray)
			{
				// Array of arrays
				populateTreeArray(tree, (JSONArray) temp, newParent);
			}
			else if(temp instanceof JSONObject)
			{
				// Array of Objects
				populateTreeObject(tree, (JSONObject) temp, newParent);
			}
			else
			{ 
				tree.addSiblingNode(newParent, a.getString(c) + ": " + temp.toString());
			}				
		}	
		return newParent;
	}
	
	public TreeField returnTree(){
		return treeField;
	}
}

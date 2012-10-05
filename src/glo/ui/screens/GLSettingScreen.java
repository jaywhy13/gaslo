package glo.ui.screens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import glo.sys.GLSettings;
import glo.ui.GLScreenShell;

public class GLSettingScreen extends GLScreenShell implements FieldChangeListener {
	public GLSettingScreen() {
		super("Settings",true);
	}
	
	VerticalFieldManager settings = new VerticalFieldManager(Manager.USE_ALL_WIDTH | Manager.USE_ALL_HEIGHT);
	private ObjectChoiceField sortByOptions;
	private ObjectChoiceField howFarOptions;
//	private ObjectChoiceField refreshDistanceOptions;
	
	private static int [] howFarDistances = new int [] {500,1000,2000,3000,5000,10000,20000};
	
	protected void setupContentArea() {
		super.setupContentArea();
		settings = new VerticalFieldManager();
		bodyMgr.add(settings);
		bodyMgr.setPadding(new XYEdges(0, 5, 0, 5));
		
		// Sort by closest or lowest price
		LabelField sortBy = new LabelField("Sort stations by");
		sortByOptions = new ObjectChoiceField("",new String[]{"Closest distance","Lowest price","Both"});
		settings.add(sortBy);
		settings.add(sortByOptions);
		sortByOptions.setSelectedIndex(2);
		settings.add(new SeparatorField());
		sortByOptions.setSelectedIndex(GLSettings.getSortMode());
		sortByOptions.setChangeListener(this);
		
		// Refresh prices every
		LabelField howFar = new LabelField("How far will you drive to save $1?");
		String [] choices = new String[]{"500 m","1 km","2 km","3 km","5 km","10 km","20 km"};
		howFarOptions = new ObjectChoiceField("Distance: ", choices);
		howFarOptions.setChangeListener(this);
		settings.add(howFar);
		settings.add(howFarOptions);
		settings.add(new SeparatorField());
		for(int i = 0; i < choices.length; i++){
			int distance = howFarDistances[i];
			if(distance == GLSettings.getDistanceThreshold()){
				howFarOptions.setSelectedIndex(i);
				break;
			}
		}
		
		// Refresh Distance every
		/*LabelField refreshDistance = new LabelField("Refresh your location every");
		String [] distanceChoices = new String[]{"500M","1KM","3KM","5KM"};
		refreshDistanceOptions = new ObjectChoiceField("", distanceChoices);
		refreshDistanceOptions.setChangeListener(this);
		refreshDistanceOptions.setSelectedIndex(GLSettings.getDistanceUpdate());
		settings.add(refreshDistance);
		settings.add(refreshDistanceOptions);
		settings.add(new SeparatorField());*/
	}

	public void fieldChanged(Field field, int context) {
		if(field == sortByOptions){
			int index = sortByOptions.getSelectedIndex();
			GLSettings.setSortMode(index);
		} else if(field == howFarOptions){
			int distance = howFarDistances[howFarOptions.getSelectedIndex()];
			GLSettings.setDistanceThreshold(distance);
		}	
		/*else if(field == refreshDistanceOptions){
			int dist =  refreshDistanceOptions.getSelectedIndex();
			GLSettings.setDistanceUpdate(dist);
		}*/
	}

}

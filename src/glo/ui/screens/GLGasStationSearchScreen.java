package glo.ui.screens;


import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

import glo.json.JSONException;
import glo.net.ServerDataManager;
import glo.sys.GLSettings;
import glo.ui.GLIcon;
import glo.ui.GLScreenShell;
import glo.ui.screens.GLProcessingScreen;

public class GLGasStationSearchScreen extends GLScreenShell implements FieldChangeListener {
	protected RichTextField searchField; 
	protected Bitmap _mgiLogo = Bitmap.getBitmapResource("glo/icons/mgilogo_32.png");
	protected ButtonField searchButton;
	protected Border searchFieldBorder;
	protected VerticalFieldManager searchMgr;
	protected EditField searchItem;
	protected String defaultFieldText; 
	
	public GLGasStationSearchScreen() {
		this("Search");
	}
	
	public GLGasStationSearchScreen(String headerText) {
		super(headerText,true);
	}
	
	protected void setupContentArea() {		

		defaultFieldText = "Enter location ...";
		searchItem = new EditField(EditField.NO_NEWLINE | EditField.FIELD_HCENTER| EditField.NO_COMPLEX_INPUT);
		searchItem.setText(defaultFieldText);
		
		searchButton = new ButtonField("Search", Field.FIELD_HCENTER);
		searchButton.setPadding(new XYEdges(4, 4, 4, 4));
		
		searchFieldBorder = BorderFactory.createRoundedBorder(new XYEdges(4,4,4,4));
		searchItem.setBorder(searchFieldBorder);
		
		bodyMgr.setPadding(new XYEdges(0, 10, 0, 10));
		bodyMgr.add(new BitmapField(_mgiLogo));
		bodyMgr.add(searchItem);
		bodyMgr.add(searchButton);
		
		searchButton.setChangeListener(this);
		searchItem.setChangeListener(this);
	}

	public void fieldChanged(Field field, int context) {
		if(field == searchButton) {
			int searchLength = searchItem.getText().length();
			if(searchLength == 0) {
				UiApplication.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						Dialog.alert("You must enter a search item");
					}
				});
			}
			else{					
				getSearchResults searcher = new getSearchResults(searchItem.getText());
				GLProcessingScreen.showScreenAndWait(searcher, "Searching");
			}
		}
	}
	
	protected boolean keyChar(char key, int status, int time) {
		  if(searchItem.getText().equals(defaultFieldText)) 
			  searchItem.setText(""); 
		  return super.keyChar(key, status, time);
	}
	
	static class getSearchResults implements Runnable {
		
		private String searchItem = "";
		private int searchLimit = 20;
		final ServerDataManager dataManager = new ServerDataManager();
		boolean connGoAhead = false;
		
		public getSearchResults(String search){
			searchItem = search;
		}
		
		public void run(){
			try {
				dataManager.networkSearch(searchItem, GLSettings.getNumberOfStationsToFetch());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			showResults();
		}
		
		public void showResults() {
			final GLIcon gasIcon = new GLIcon("glo/icons/gas_icon.png","Search Results"){
				public GLScreenShell getContentArea() {
					return new GLGasStationListingScreen("Search Results");
				}
			};
			UiApplication.getUiApplication().invokeLater(new Runnable() {
	            public void run() {
	                UiApplication.getUiApplication().pushScreen(gasIcon.getContentArea());
	            }
	        });
		}
	}
}

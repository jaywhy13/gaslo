package glo.ui;

import glo.gps.GPSHandle;
import glo.gps.UpdateGPSLocations;
import glo.types.GasCompany;
import glo.ui.screens.GLGasStationDetails;
import glo.ui.screens.GLGasStationListingScreen;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontManager;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * UI component to show information about the Gas company and their prices
 * @author JMWright
 *
 */
public class GLPriceInfo extends Field {
	
	protected GasCompany priceInfo; 
	public static GasCompany gsDetails;
	protected Bitmap companyIcon;

	
	protected Border border;
	
	protected Border focusBorder;

	private String descriptionTxt;
	private String priceInfoTxt;
	
	private Font descriptionFont;
	
	private Font priceInfoFont;
	
	protected int height = 60;
	private Font distanceInfoFont;
	private Background highlightBackground = BackgroundFactory.createSolidTransparentBackground(0xe5f0f9,50);
	
	public GLPriceInfo(GasCompany gpi) {
		super();
		this.priceInfo = gpi;
		GLPriceInfo.gsDetails = gpi;
		// Initialize UI components
		companyIcon = Bitmap.getBitmapResource("glo/icons/gas_icon_32.png");
		if(gpi.getCompanyName() != null){
			descriptionTxt = gpi.getSummary();
		}
		else{
			descriptionTxt = "Unavailable";
		}
		priceInfoTxt = gpi.toString();
		
		border = BorderFactory.createSimpleBorder(new XYEdges(0,0,0,0));
//		border = BorderFactory.createSimpleBorder(new XYEdges(1,1,1,1),new XYEdges(0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF),Border.STYLE_SOLID);
//		focusBorder = BorderFactory.createRoundedBorder(new XYEdges(2,0,2,0), Border.STYLE_SOLID);
		focusBorder = BorderFactory.createSimpleBorder(new XYEdges(2,0,2,0), new XYEdges(0x66b9d7,0,0x66b9d7,0), Border.STYLE_SOLID);
		setBorder(border);
	
		// Setup the fonts
		descriptionFont = Font.getDefault().derive(Font.BOLD,16);
		try {
			descriptionFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.BOLD, 20);
		} catch(ClassNotFoundException cnfe){
			
		}
		
		priceInfoFont = Font.getDefault().derive(Font.PLAIN,14);
		try {
			priceInfoFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.BOLD, 14);
		} catch(ClassNotFoundException cnfe){
			
		}
		
		distanceInfoFont = Font.getDefault().derive(Font.BOLD,14);
		try {
			distanceInfoFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.BOLD, 14);
		} catch(ClassNotFoundException cnfe){
			
		}
		
	}
	

	
	
	
	protected boolean navigationClick(int status,int time)
    {
		getFieldAtLocation(status, time);
		final GLIcon gasIcon = new GLIcon("glo/icons/gas_icon.png","Search Results"){
			public GLScreenShell getContentArea() {
//				GLGasStationDetails details =  new GLGasStationDetails(priceInfo);
//				return details.showDetails();
				return new GLGasStationDetails(priceInfo);
			}
		};
		
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				UiApplication.getUiApplication().pushScreen(gasIcon.getContentArea());
//				Dialog.alert("This one works!");
			}
		});
        return true;
    } 
	
	private void getFieldAtLocation(int status, int time) {
		// TODO Auto-generated method stub
		
	}
	

	protected void onFocus(int direction) {
		setBorder(focusBorder);
		setBackground(highlightBackground);
	}
	
	protected void onUnfocus() {
		setBorder(border);
		setBackground(null);
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public boolean isSelectable() {
		return false;
	}
	
	protected void layout(int width, int height) {
		setExtent(width,50);
	}

	protected void paint(Graphics g) {
		// Paint the components
		// Paint the icon
		
//		g.drawBitmap(10, (getHeight() - companyIcon.getHeight()) / 2, companyIcon.getWidth(),companyIcon.getHeight(),companyIcon,0,0);
		int y = (int) (0.2 * height);
		int x = 32;
		
		// Draw the description text
		g.setFont(descriptionFont);
		g.setColor(0x484c49);
		int descriptionWidth = x + descriptionFont.getAdvance(descriptionTxt) + distanceInfoFont.getAdvance(">500 km") + 10;
		if(descriptionWidth > getWidth()){
			while(descriptionWidth > getWidth()){
				descriptionTxt = descriptionTxt.substring(0,descriptionTxt.length()-3); // chop off 2
				descriptionWidth = x + descriptionFont.getAdvance(descriptionTxt + " ...") + distanceInfoFont.getAdvance(">500 km") + 15;
			}
			descriptionTxt += " ...";
		}
		g.drawText(descriptionTxt,x,y);
		
		g.setColor(0x0098dc);
		g.setFont(distanceInfoFont);
		
		String unit = "m";
		String distanceFromUser = GPSHandle.getDistanceAsString(this.priceInfo.getDistanceFromUser());
		int distance = (int) Math.ceil(this.priceInfo.getDistanceFromUser());
		g.drawText(distanceFromUser, getWidth()-distanceInfoFont.getAdvance(distanceFromUser)-10,y+10);
		
		// Draw the price info text
		g.setFont(priceInfoFont);
		g.drawText(priceInfoTxt, x,y + descriptionFont.getHeight() + 4);
		g.drawText(gsDetails.getAveragePrices() + "", 350,56);
	}
	






	public int getPreferredHeight() {
		return height;
	}

}

package glo.ui;

import glo.types.GasCompany;
import glo.types.GasPriceInfo;
import glo.types.GasCompany.Features;

import java.util.Vector;

import javax.microedition.lcdui.Spacer;

import com.rimextra.device.api.ui.container.HorizontalButtonFieldSet;
import com.rimextra.device.api.ui.container.JustifiedHorizontalFieldManager;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class GLGSDetails extends Manager {
	
	protected Bitmap companyIcon;
	private Font generalDetFont;
	private Font priceDetFont;
	private Font featuresDetFont;
	private String summaryDet;
	private Vector priceInfo;
	private Features feature;
	
	
	public GLGSDetails(GasCompany gc) {
		super(Manager.USE_ALL_HEIGHT | Manager.USE_ALL_WIDTH);
		VerticalFieldManager vfm = new VerticalFieldManager(Manager.USE_ALL_WIDTH);

		companyIcon = Bitmap.getBitmapResource("glo/icons/gas_icon_32.png");
		FontifiedLabel name = new FontifiedLabel(gc.getCompanyName(), 20, 0xffffff);
		JustifiedHorizontalFieldManager gsTitle = new JustifiedHorizontalFieldManager(new BitmapField(companyIcon), name, null);
		vfm.add(gsTitle);
		
		
		Vector prices = gc.getPrices();
		for(int i = 0; i < gc.getPrices().size(); i++){
			GasPriceInfo gpi = (GasPriceInfo) gc.getPrices().elementAt(i);
			String label = gpi.getAlias() + " " + gpi.getCost();
			FontifiedLabel gasPrice = new FontifiedLabel(label, 15, 0xffffff);
			vfm.add(gasPrice);
		}
		
		add(vfm);
		
	}
	


	
	public boolean isFocusable() {
		return false;
	}
	
	public boolean isSelectable() {
		return false;
	}




	protected void sublayout(int width, int height) {
		// TODO Auto-generated method stub
		
		
	}
	
	/*protected void paint(Graphics g) {
		g.drawBitmap(10, (getHeight() - companyIcon.getHeight()) / 2, companyIcon.getWidth(),companyIcon.getHeight(),companyIcon,0,0);
		
		// Draw the General Company Details
		g.setFont(generalDetFont);
		g.drawText(summaryDet, 56,10);

		// Draw the price info text
		g.setFont(priceDetFont);
		int priceHeight = 33;
		for(int a=0; a<priceInfo.size(); a++){
			GasPriceInfo currentPrice = (GasPriceInfo) priceInfo.elementAt(a);
			currentPrice.verbose = true;
			priceHeight += 20;
			g.drawText(currentPrice.toString(),76,priceHeight);
		}
		
		String DescribeFeatures = feature.toString();
		if(!DescribeFeatures.equals("")){
			g.drawText("Features Available are: ",56,120);
			g.setFont(featuresDetFont);
			g.drawText(feature.toString(),56,140);
		}
	}*/
	
}

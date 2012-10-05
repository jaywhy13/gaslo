package glo.ui;

import com.rimextra.device.api.ui.container.EqualSpaceToolbar;
import com.rimextra.device.api.ui.container.JustifiedVerticalFieldManager;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

public class GLSplash extends MainScreen {
	
	
	private Background footerBg = BackgroundFactory.createLinearGradientBackground(0x336699, 0x336699, 0x08121a,0x08121a);

	private Background plainBg = BackgroundFactory.createSolidBackground(0x336699);

	private Bitmap _logoBitmap = Bitmap
			.getBitmapResource("glo/icons/gaslo_logo.png");

	private Bitmap _mgiBitmap = Bitmap
			.getBitmapResource("glo/icons/mgilogo_32.png");

	public GLSplash() {
		super(VerticalFieldManager.NO_VERTICAL_SCROLL);
		
		// Now create top logo section
		HorizontalFieldManager topHfm = new HorizontalFieldManager(
				Field.FIELD_HCENTER | HorizontalFieldManager.USE_ALL_HEIGHT);
		VerticalFieldManager logoVfm = new VerticalFieldManager(
				Field.FIELD_VCENTER | VerticalFieldManager.USE_ALL_WIDTH);

		logoVfm.add(new BitmapField(_logoBitmap,Field.FIELD_HCENTER));
		topHfm.add(logoVfm);
		
		// Create the lower section with the powererd by
		VerticalFieldManager footerVfm = new VerticalFieldManager(Field.FIELD_VCENTER | VerticalFieldManager.USE_ALL_WIDTH);
		footerVfm.add(new LabelField("powered by",Field.FIELD_HCENTER));
		footerVfm.add(new BitmapField(_mgiBitmap, Field.FIELD_HCENTER));
		footerVfm.setPadding(new XYEdges(5, 0, 10, 0));
		footerVfm.setBackground(BackgroundFactory.createSolidTransparentBackground(0xBBBBBB,50));
		footerVfm.setBorder(BorderFactory.createSimpleBorder(new XYEdges(2,0,0,0),new XYEdges(0x000000,0x000000,0x000000,0x000), Border.STYLE_SOLID));
		
		JustifiedVerticalFieldManager jvfm = new JustifiedVerticalFieldManager(null,topHfm,footerVfm);
		add(jvfm);
		jvfm.setBackground(plainBg);
		
	}
}

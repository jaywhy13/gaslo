package glo.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

public class GLPriceList extends Manager {
	public static final int FIELD_PADDING = 3;

	public GLPriceList() {
		//super(Manager.USE_ALL_WIDTH);
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR | Manager.USE_ALL_HEIGHT);
	}
	
	protected void sublayout(int width, int height) {
		int numFields = getFieldCount();
		int x = 0;
		int y = FIELD_PADDING;
		
		for(int i = 0; i < numFields; i++){
			Field f = getField(i);
			setPositionChild(f,x,y);
			layoutChild(f, width, height);
			y+= (f.getHeight() + FIELD_PADDING);
		}
		setExtent(width,height);

	}
	
	

}

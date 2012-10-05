package glo.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class CenteredFieldManager extends HorizontalFieldManager {
	
	public CenteredFieldManager(){
		super(HorizontalFieldManager.USE_ALL_WIDTH);
	}

	protected void sublayout(int width, int height) {
		int neededWidth = 0; // full width needed
		int startLeftPos = 0; // start left pos
		int maxHeightOfField = 0;
		for(int i = 0; i < getFieldCount(); i++){
			Field f = getField(i);
			neededWidth += (f.getWidth() + f.getMarginLeft()+ f.getMarginRight() + f.getPaddingLeft() + f.getPaddingRight());
			maxHeightOfField = Math.max(maxHeightOfField, f.getHeight());
		}
		
		startLeftPos = (width - neededWidth) / 2;
		if(startLeftPos < 0) startLeftPos = 0; // reset to zero if needed width is less than zero
		
		int currentPos = startLeftPos;
		for(int j = 0; j < getFieldCount(); j++){
			Field f2 = getField(j);
			layoutChild(f2, width, height);
			setPositionChild(f2, currentPos, 0);
			startLeftPos += (f2.getWidth() + f2.getMarginLeft() + f2.getMarginRight() + f2.getPaddingLeft() + f2.getPaddingRight());
		}
		setExtent(width, maxHeightOfField);
	}
}

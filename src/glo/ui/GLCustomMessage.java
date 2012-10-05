package glo.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;

/**
 * Displays a custom message to the screen
 * @author rahibbert
 *
 */
public class GLCustomMessage extends Field {
	
	private String message = "Gas Lo";
	private Font messageFont;
	private FontFamily ff = null;	
	
	public GLCustomMessage(String mess) {
		super();
		message = mess;
		try{
			ff = FontFamily.forName("BBCasual");
			messageFont = ff.getFont(Font.GEORGIAN_SCRIPT, 24);		
		}
		catch(ClassNotFoundException e){
			messageFont = Font.getDefault().derive(Font.BOLD,24);
		}	
	}

	protected void layout(int width, int height) {
		setExtent(width,50);	
	}

	protected void paint(Graphics g) {
		// Draw the custom message
		g.setFont(messageFont);
		g.drawText(message,56,28);	
	}
	
	public boolean isFocusable() {
		return false;
	}
	
	public boolean isSelectable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int getPreferredHeight() {
		return 86;
	}
	
}

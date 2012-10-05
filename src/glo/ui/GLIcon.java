package glo.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;


public class GLIcon extends BitmapField {

	public Border focusedBorder = BorderFactory
			.createRoundedBorder(new XYEdges(6, 6, 6, 6),0xFFFFFF,Border.STYLE_SOLID);

	public Border unFocusedBorder = BorderFactory
	.createRoundedBorder(new XYEdges(6, 6, 6, 6),0xFFFFFF,Border.STYLE_TRANSPARENT);
	
	protected String caption = null;
	
	protected String description = null;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected GLIconTray tray = null;

	public GLIcon(Bitmap bitmap) {
		super(bitmap);
		setPadding(new XYEdges(5,5,5,5));
		if(isFocus()){
			setBorder(focusedBorder);
		} else {
			setBorder(unFocusedBorder);
		}
	}

	public GLIcon(String src) {
		this(Bitmap.getBitmapResource(src));
	}

	public GLIcon(String src, String caption) {
		this(Bitmap.getBitmapResource(src), caption);
	}
	
	public GLIcon(String src, String caption, String description){
		this(src,caption);
		this.description = description;
	}

	public GLIcon(Bitmap bitmap, String caption) {
		this(bitmap);
		this.caption = caption;
	}

	public boolean isFocusable() {
		return true;
	}

	protected void drawFocus(Graphics graphics, boolean on) {
	}
	
	protected void onFocus(int direction) {
		setBorder(focusedBorder);
		if(tray != null){
			tray.iconFocused(this);
		}
	}
	
	protected void onUnfocus() {
		setBorder(unFocusedBorder);
		
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setTray(GLIconTray tray) {
		this.tray = tray;
	}

	public GLIconTray getTray() {
		return this.tray;
	}
	
	public GLScreenShell getContentArea(){
		return new GLScreenShell(this.caption,true);
	}

	public GLScreenShell getLoadingArea(){
		return new GLScreenShell(this.caption,true);
	}
	
	/**
	 * Notifies the tray if not null that a user has clicked this icon
	 */
	protected boolean navigationClick(int status, int time) {
		if (tray != null) {
			return tray.iconSelected(this);
		}
		return super.navigationClick(status, time);
	}

	/**
	 * Notifies the tray that the user has clicked enter while the icon had focus
	 */
	protected boolean keyChar(char character, int status, int time) {
		if (character == Keypad.KEY_ENTER) {
			if (tray != null) {
				return tray.iconSelected(this);
			}
		}
		return super.keyChar(character, status, time);
	}
}

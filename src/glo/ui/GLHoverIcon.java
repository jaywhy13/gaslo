package glo.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.decor.Border;

public class GLHoverIcon extends GLIcon {

	protected Bitmap hoverIcon;
	
	protected Bitmap unhoverIcon;
	
	

	public GLHoverIcon(Bitmap bitmap) {
		super(bitmap);
		this.unhoverIcon = bitmap;
		// TODO Auto-generated constructor stub
	}
	
	
	public GLHoverIcon(Bitmap unhoverIcon, Bitmap hoverIcon){
		this(unhoverIcon);
		this.hoverIcon = hoverIcon;
	}
	
	public GLHoverIcon(Bitmap unhoverIcon, Bitmap hoverIcon, String caption){
		this(unhoverIcon);
		this.hoverIcon = hoverIcon;
		this.caption = caption;
	}
	
	public GLHoverIcon(String unhoverIcon, String hoverIcon, String caption){
		this(unhoverIcon);
		this.hoverIcon = Bitmap.getBitmapResource(hoverIcon);
		this.caption = caption;
	}
	
	public GLHoverIcon(String unhoverIcon, String hoverIcon, String caption, String description){
		this(unhoverIcon);
		this.hoverIcon = Bitmap.getBitmapResource(hoverIcon);
		this.caption = caption;
		this.description = description;
	}
	
	
	public GLHoverIcon(String src) {
		this(Bitmap.getBitmapResource(src));
	}

	public GLHoverIcon(Bitmap bitmap, String caption) {
		this(bitmap);
		this.caption = caption;
	}

	

	public Bitmap getHoverIcon() {
		return hoverIcon;
	}

	public void setHoverIcon(Bitmap hoverIcon) {
		this.hoverIcon = hoverIcon;
	}

	protected void onFocus(int direction) {
		setBitmap(hoverIcon);
		super.onFocus(direction);
	}
	
	protected void onUnfocus() {
		setBitmap(unhoverIcon);
	}
	
	public void setBorder(Border b){
		
	}


}

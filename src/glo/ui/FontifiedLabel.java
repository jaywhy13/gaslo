package glo.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;

/**
 * This class provides a standard way to create a label and specify font size, color 
 * and style. 
 * @author JMWright
 *
 */
public class FontifiedLabel extends LabelField {
	
	
	protected boolean drawDropShadow = false;
	
	protected int dropShadowXDistance = 0;
	
	protected int dropShadowYDistance = -3;
	
	
	protected Bitmap icon; 
	
	protected int dropShadowColor = 0x000000;
	
	protected int padding = 6;
	

	/**
	 * Font size
	 */
	protected int _fontSize = 10;
	
	/** 
	 * Font color in hex
	 */
	protected int _fontColor = 0x0;
	
	/**
	 * Font style
	 */
	protected long _fontStyle = Font.PLAIN;
	
	protected Font font;
	
	/**
	 * Constructor that takes in the label text 
	 * @param src
	 */
	public FontifiedLabel(String src){
		super(src);
	}
	
	/**
	 * Creates a label given the label text and font size
	 * @param txt
	 * @param fontSize
	 */
	public FontifiedLabel(String txt, int fontSize){
		super(txt);
		this._fontSize = fontSize;
		setPadding(new XYEdges(4, 4, 4, 4));
	}
	
	/**
	 * Creates a label given the label text, font size and color 
	 * @param txt
	 * @param fontSize
	 * @param fontColor
	 */
	public FontifiedLabel(String txt, int fontSize, int fontColor){
		this(txt,fontSize);
		this._fontColor = fontColor;
	}
	
	/**
	 * Creates a label given the label text, font size, color and style info
	 * @param txt
	 * @param fontSize
	 * @param fontColor
	 * @param style
	 */
	public FontifiedLabel(String txt, int fontSize, int fontColor, long style){
		this(txt,fontSize,fontColor);
		this._fontStyle = style;
	}
	
	public FontifiedLabel(String txt, int fontSize, int fontColor, long style, long labelStyle){
		super(txt,labelStyle);
		this._fontSize = fontSize;
		this._fontColor = fontColor;
		this._fontStyle = style;
		
	}
	
	
	
	
	/**
	 * Draws the font 
	 */
	protected void paint(Graphics g){
		Font f = Font.getDefault().derive((int) _fontStyle,_fontSize);
		if(font != null){
			f = font;
		}
		g.setFont(f);
		g.setColor(_fontColor);
		
		// draw the icon if necessary
		int x = 0;
		int y = 0;
		
		if(icon != null){ // draw the icon
			g.drawBitmap(x, y, icon.getWidth(), icon.getHeight(), icon, 0, 0);
			x+= icon.getWidth();
			x+= padding;
			
			y = (icon.getHeight() - getHeight())/2;
		}
		
		if(drawDropShadow){
			g.setColor(dropShadowColor);
			g.drawText(getText(),x + dropShadowXDistance,y + dropShadowYDistance);
		}
		
		g.setColor(_fontColor);
		g.drawText(getText(),x,y);
		
	}
	
	
	/**
	 * Overriden to give extra spacing
	 * TODO: Revisit this function to get more acccurate calculations 
	 */
	public int getPreferredWidth() {
		// TODO Auto-generated method stub
		Font f = Font.getDefault().derive((int) _fontStyle,_fontSize);
		int width = f.getBounds('A') * getText().length() + 2;
		
		if(drawDropShadow){
			width += Math.abs(dropShadowXDistance);
		}
		
		if(icon != null){
			width+= (padding + icon.getWidth());
		}
		return width;
	}
	
	public int getPreferredHeight(){
		int height = getHeight();
		if(drawDropShadow){
			height += Math.abs(dropShadowYDistance);
		}
		if(icon != null){
			height = Math.max(height, icon.getHeight());
		}
		
		return height;
	}

	public int getFontSize() {
		return _fontSize;
	}

	public void setFontSize(int _fontSize) {
		this._fontSize = _fontSize;
		this.setDirty(true);
	}

	public int getFontColor() {
		return _fontColor;
	}

	public void setFontColor(int _fontColor) {
		this._fontColor = _fontColor;
		this.setDirty(true);
	}

	public long getFontStyle() {
		return _fontStyle;
	}

	public void setFontStyle(long _fontStyle) {
		this._fontStyle = _fontStyle;
		this.setDirty(true);
	}

	public boolean isDrawDropShadow() {
		return drawDropShadow;
	}

	public void setDrawDropShadow(boolean drawDropShadow) {
		this.drawDropShadow = drawDropShadow;
	}

	public int getDropShadowXDistance() {
		return dropShadowXDistance;
	}

	public void setDropShadowXDistance(int dropShadowXDistance) {
		this.dropShadowXDistance = dropShadowXDistance;
	}

	public int getDropShadowYDistance() {
		return dropShadowYDistance;
	}

	public void setDropShadowYDistance(int dropShadowYDistance) {
		this.dropShadowYDistance = dropShadowYDistance;
	}

	public int getDropShadowColor() {
		return dropShadowColor;
	}

	public void setDropShadowColor(int dropShadowColor) {
		this.dropShadowColor = dropShadowColor;
	}
	
	public void setFont(Font f){
		font = f;
		super.setFont(f);
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
		this.setDirty(true);
	}
	
	
	
	
	
	
}

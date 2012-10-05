package glo.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;

public class GLHeader extends FontifiedLabel {
	public GLHeader(String caption){
		super(caption,24,0xEEEEEE,Font.BOLD,Field.FIELD_HCENTER);
	}
}

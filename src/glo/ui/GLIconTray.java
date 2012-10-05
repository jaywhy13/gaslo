package glo.ui;

import java.util.Vector;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class GLIconTray extends HorizontalFieldManager {

	public GLIconTray() {
		super(Manager.FIELD_HCENTER | Manager.FIELD_VCENTER);
		setPadding(4, 4, 4, 4);
	}

	/**
	 * Store for the GLIcons
	 */
	private Vector icons = new Vector();

	/**
	 * This field is used as the status field
	 */
	private LabelField statusLbl;

	/**
	 * Adds an icon to the tray and to the UI
	 * 
	 * @param icon
	 */
	public void addIcon(GLIcon icon) {
		icons.addElement(icon);
		icon.setPadding(new XYEdges(15,5,15,5));
		add(icon);
		icon.setTray(this);
	}

	/**
	 * Sets the status field
	 * 
	 * @param lbl
	 */
	public void setStatus(LabelField lbl) {
		this.statusLbl = lbl;
	}

	/**
	 * Returns the status field label
	 * 
	 * @return
	 */
	public LabelField getStatus() {
		return this.statusLbl;
	}

	/**
	 * Invoked by the GLIcon whenever the user clicks or hits enter while the
	 * icon is selected.
	 * 
	 * @param icon
	 */
	public boolean iconFocused(GLIcon icon) {
		if (statusLbl != null) {
			String description = icon.getDescription() == null ? "" : icon.getDescription();
			statusLbl.setText(description);
		}
		return true;
	}

	public boolean iconSelected(GLIcon glIcon) {
		GLScreenShell shell = glIcon.getContentArea();
		shell.getBanner().setCaption(glIcon.getCaption());
		pushScreen(shell);
		return true;
	}

	private void pushScreen(Screen toPush) {
		UiApplication.getUiApplication().pushScreen(toPush);
	}

}

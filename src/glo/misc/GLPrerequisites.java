package glo.misc;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.xml.rpc.ComplexType;

public class GLPrerequisites {

	private Hashtable prerequisites = new Hashtable();

	private Vector listeners = new Vector();

	private int numPrereqs = 0;

	public static final int PREREQUISTE_COMPLETE = 0;
	public static final int PREREQUISITES_COMPLETE = 1;

	private Hashtable completedPrerequisites = new Hashtable();

	private static final GLPrerequisites prereqs = new GLPrerequisites();

	public static void addPrerequisite(GLPrerequisite prereq) {
		prereqs.prerequisites.put(prereq.getName(), prereq);
		prereqs.numPrereqs++;
		System.out.println("GL [II] Prereq: [" + prereq.getName() + "] added");
	}

	public static void updatePrerequisteStatus(String prereqName, int status) {
		GLPrerequisite prereq = (GLPrerequisite) prereqs.prerequisites
				.get(prereqName);
		if (prereq != null) {
			prereq.setStatus(status);
		}
	}

	public static void prerequisiteStarted(String prereqName) {
		updatePrerequisteStatus(prereqName, GLPrerequisite.STARTED);
		System.out.println("GL [II] Prereq: [" + prereqName + "] started");
	}

	public static GLPrerequisite getPrerequisite(String prereqName) {
		if (prereqs.prerequisites.containsKey(prereqName)) {
			return (GLPrerequisite) prereqs.prerequisites.get(prereqName);
		} else if (prereqs.completedPrerequisites.containsKey(prereqName)) {
			System.out.println("GL [II] Prereq: [" + prereqName
					+ "] cannot be accessed, it is complete");
			return null;
		}

		System.out.println("GL [EE] Prereq: [" + prereqName + "] is unknown");
		return null;
	}

	public static boolean isPrerequisiteComplete(String prereqName) {
		if (prereqs.completedPrerequisites.containsKey(prereqName)) {
			return true;
		}
		return false;
	}

	public static void removePrerequisite(String prereqName) {
		Object o = GLPrerequisites.prereqs.prerequisites.remove(prereqName);
		if (o != null) {
			GLPrerequisite prereq = (GLPrerequisite) o;
			for (int i = 0; i < prereqs.listeners.size(); i++) {
				GLPrequisiteListener listener = (GLPrequisiteListener) prereqs.listeners
						.elementAt(i);
				listener.prerequisiteRemoved(prereq);
			}
		}

		updatePrerequisites();
	}

	public static void prerequisiteFailed(String prereqName) {
		if (isPrerequisiteComplete(prereqName)) {
			return;
		}

		GLPrerequisite prereq = getPrerequisite(prereqName);
		if (prereq != null) {
			if (prereq.isProceedOnFailure()) {
				prerequisiteComplete(prereqName);
			} else {
				prereq.failed();
			}
		}

	}

	public static void updatePrerequisites() {
		if (prereqs.prerequisites.size() == 0) {
			for (int i = 0; i < prereqs.listeners.size(); i++) {
				GLPrequisiteListener listener = (GLPrequisiteListener) prereqs.listeners
						.elementAt(i);
				listener.prerequisitesComplete();
			}
			System.out.println("GL [II] All prerequisites complete");
		}
	}

	public static void prerequisiteComplete(String prereqName) {
		if (isPrerequisiteComplete(prereqName)) {
			return;
		}

		GLPrerequisite prereq = getPrerequisite(prereqName);
		if (prereq == null)
			return;
		if (prereqs.prerequisites.remove(prereqName) != null) {
			prereq.setStatus(GLPrerequisite.COMPLETE);
			System.out.println("GL [II] Prereq: [" + prereq.getName()
					+ "] completed");
		} else {
			System.out.println("GL [EE] Prereq: [" + prereq.getName()
					+ "] could not be removed");
		}

		prereqs.completedPrerequisites.put(prereqName, prereq);

		if (prereqs.prerequisites.size() == 0) {
			updatePrerequisites();
		} else {
			for (int i = 0; i < prereqs.listeners.size(); i++) {
				GLPrequisiteListener listener = (GLPrequisiteListener) prereqs.listeners
						.elementAt(i);
				listener.prerequisiteComplete(prereq);
			}
		}
	}

	public static void addPrerequisiteListener(GLPrequisiteListener listener) {
		prereqs.listeners.addElement(listener);

	}

	public static void prerequisteTimedOut(String prereqName) {
		System.out.println("GL [II] Prereq: [" + prereqName + "] timed out");
		GLPrerequisite prereq = GLPrerequisites.getPrerequisite(prereqName);
		if (prereq != null) {
			if (prereq.isProceedOnTimeout()) {
				prerequisiteComplete(prereqName);
			} else {
				prereq.timedOut();
			}
		}
	}

	public static Enumeration getPrerequisiteNames() {
		return prereqs.prerequisites.keys();
	}

	public static int numPrerequisites() {
		return prereqs.numPrereqs;
	}

	public static int numPrequisitesComplete() {
		return prereqs.completedPrerequisites.size();

	}

}

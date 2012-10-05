package glo.misc;

public interface GLPrequisiteListener {
	
	public void prerequisitesComplete();
	
	public void prerequisiteComplete(GLPrerequisite prereq);

	public void prerequisiteRemoved(GLPrerequisite prereq);
	
}

package glo.db;

public class DbManagerEvent  {
	public static final int DATABASE_LOAD_FAILURE = -1;
	public static final int DATABASE_CREATED = 0;
	public static final int DATABASE_OPENED = 1;
	public static final int DATABASE_READY = 2;
	
	private int type;
	

	public DbManagerEvent(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
}

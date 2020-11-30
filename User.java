 

public class User{
	private int id;
	private String name;
	private String userType;
	private LabelMechanism mechanism;
	private WorkSpace workSpace;
	
	public User(int id, String name, String userType) {
		this.id=id;
		this.name=name;
		this.userType=userType;
		if(userType.contentEquals("RandomBot")) {
		mechanism=new RandomLabelingMechanism();
		}
		
	}
	
	public void customs() {
		System.out.println("what is your id");
		System.out.println("what is your name");
		System.out.println("what is your userType");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public LabelMechanism getMechanism() {
		return mechanism;
	}

	public void setMechanism(LabelMechanism mechanism) {
		this.mechanism = mechanism;
	}

	public WorkSpace getWorkSpace() {
		return workSpace;
	}

	public void setWorkSpace(WorkSpace workSpace) {
		this.workSpace = workSpace;
	}

}
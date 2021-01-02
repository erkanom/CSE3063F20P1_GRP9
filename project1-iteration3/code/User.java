public class User {
	private int id;
	private String name;
	private String userType;
	private LabelMechanism mechanism;
	private WorkSpace workSpace;
	private double consistencyCP;
	private String password;
	public User(int id, String name, String userType,double consistencyCP,String password) {
		this.id = id;
		this.name = name;
		this.userType = userType;
		this.consistencyCP = consistencyCP;
		this.password=password;
		if (userType.contentEquals("RandomBot")) {
			mechanism = new RandomLabelingMechanism();
		}
		if (userType.contentEquals("HumanUser")) {
			mechanism = new UserLabelingMechanism();
		}
		if (userType.contentEquals("RatingBot")) {
			mechanism = new RatingMechanism();
		}
		if (userType.contentEquals("EvenBot")) {
			mechanism = new EvenLabelingMechanism();
		}
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
	public double getConsistencyCP() {
		return consistencyCP;
	}
	public void setConsistencyCP(double consistencyCP) {
		this.consistencyCP = consistencyCP;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
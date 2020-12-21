import java.util.ArrayList;

public class LogHistory {
		private Instance instance ;
		private Label FinalValue;
		private ArrayList<User> userList;
		private ArrayList<ArrayList<Log>> history;
		public LogHistory() {
			super();
			userList=new ArrayList<User>();
			history= new ArrayList<>();
		}
		public Instance getInstance() {
			return instance;
		}
		public void setInstance(Instance instance) {
			this.instance = instance;
		}
		public Label getFinalValue() {
			return FinalValue;
		}
		public void setFinalValue(Label finalValue) {
			FinalValue = finalValue;
		}
		public ArrayList<User> getUserList() {
			return userList;
		}
		public void setUserList(ArrayList<User> userList) {
			this.userList = userList;
		}
		public ArrayList<ArrayList<Log>> getHistory() {
			return history;
		}
		public void setHistory(ArrayList<ArrayList<Log>> history) {
			this.history = history;
		}
		
		
}

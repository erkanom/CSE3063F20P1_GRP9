
public class Main {

	public static void main(String[] args) {
		
		//These statements just for testing when we write the json parser all of these will be delete
		
		
		User user1 = new User(1, "random1", "randombot");
		User user2 = new User(2, "random2", "randombot");
		User user3 = new User(3, "ML", "MLbot");

		Dataset database = new Dataset(1, "sentiment", 10);

		Label labels1 = new Label(1, "");

		Label labels2 = new Label(2, "");
		Label labels3 = new Label(3, "");
		Label labels4 = new Label(4, "");

		Label labels5 = new Label(5, "");
		Label labels6 = new Label(6, "");
		Label labels7 = new Label(7, "");

		Label labels8 = new Label(8, "");
		Label labels9 = new Label(9, "");
		Label labels10 = new Label(10, "");

		Label labels11 = new Label(11, "");
		Label labels12 = new Label(12, "");
		Label labels13 = new Label(13, "");

		Label labels14 = new Label(14, "");
		Label labels15 = new Label(15, "");
		Label labels16 = new Label(16, "");

		database.addLabel(labels1);
		database.addLabel(labels2);
		database.addLabel(labels3);
		database.addLabel(labels4);
		database.addLabel(labels5);
		database.addLabel(labels6);
		database.addLabel(labels7);
		database.addLabel(labels8);
		database.addLabel(labels9);
		database.addLabel(labels10);
		database.addLabel(labels11);
		database.addLabel(labels12);
		database.addLabel(labels13);
		database.addLabel(labels14);
		database.addLabel(labels15);
		database.addLabel(labels16);

		Instance instance1 = new Instance(1, "",10);
		Instance instance2 = new Instance(2, "",10);
		Instance instance3 = new Instance(3, "",10);
		Instance instance4 = new Instance(4, "",10);
		Instance instance5 = new Instance(5, "",10);

		database.addInstance(instance1);
		database.addInstance(instance2);
		database.addInstance(instance3);
		database.addInstance(instance4);
		database.addInstance(instance5);
		
		
		RandomLabelingMechanism random = new RandomLabelingMechanism(database);
		User[] userList=new User[3];
		userList[0]=user1;
		userList[1]=user2;
		userList[2]=user3;
		
		
		
		
		random.doLabeling(userList);

	}

}

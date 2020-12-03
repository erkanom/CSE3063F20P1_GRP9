import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		ArrayList<User> userList = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader("./Configuration.json");
		Object obj = jsonParser.parse(reader);
		JSONObject usersJson = (JSONObject) obj;
		JSONArray array = (JSONArray) usersJson.get("users");
		for (int i = 0; i < array.size(); i++) {
			JSONObject user = (JSONObject) array.get(i);
			long userId = (long) user.get("user id");
			String userName = (String) user.get("user name");
			String userType = (String) user.get("user type");
			userList.add(new User((int) userId, userName, userType));
		}
		WorkSpace workSpace = new WorkSpace(0);
		workSpace.createFromJson("input.json");
		boolean MakeSureEveryInstancesLabeledOption = true;
		while (MakeSureEveryInstancesLabeledOption) {
			MakeSureEveryInstancesLabeledOption = false;
			for (int i = 0; i < workSpace.getDataset().getInstances().size(); i++) {
				if (workSpace.getLogs().get("instance" + (i + 1)).isEmpty()) {
					MakeSureEveryInstancesLabeledOption = true;
				}
			}
			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).getUserType().equals("RandomBot"))
					userList.get(i).getMechanism().doLabeling(workSpace, userList.get(i));
			}
		}
		workSpace.CreateJsonFromDataset(userList.toArray());
	}
}

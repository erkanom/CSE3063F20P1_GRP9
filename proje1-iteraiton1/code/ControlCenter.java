import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ControlCenter {
	public ControlCenter() {
		super();
	}
	public void StartSystem(String inputFileName, String ConfigFileName) throws IOException, ParseException {
		ArrayList<User> userList = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(ConfigFileName+".json");
		Object obj = jsonParser.parse(reader);
		JSONObject usersJson = (JSONObject) obj;
		JSONArray array = (JSONArray) usersJson.get("users");
		File file = new File("logs.txt");
		if (file.exists()) {
			file.delete();
		}else file.createNewFile();
		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < array.size(); i++) {
			JSONObject user = (JSONObject) array.get(i);
			long userId = (long) user.get("user id");
			String userName = (String) user.get("user name");
			String userType = (String) user.get("user type");
			userList.add(new User((int) userId, userName, userType));
			
			
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			System.out.println(timeStamp+" [ControlCenter] INFO ControlCenter created:"+userName+" as "+userType);		
			bw.write((timeStamp+" [ControlCenter] INFO ControlCenter created:"+userName+" as "+userType));
			bw.write("\n");
			
		}
		bw.close();
		WorkSpace workSpace = new WorkSpace(0);
		workSpace.createFromJson(inputFileName+".json");
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
		User[] temp = new User[userList.size()];
		for (int i = 0; i < userList.size(); i++) {
			temp[i]=userList.get(i);
		}
		workSpace.CreateJsonFromDataset(temp);
	}
	
	
}

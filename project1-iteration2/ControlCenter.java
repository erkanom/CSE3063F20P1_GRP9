import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ControlCenter {

	private ArrayList<WorkSpace> states;

	public ControlCenter() {
		super();
		states = new ArrayList<WorkSpace>();
	}

	public void StartSystem(String ConfigFileName) throws IOException, ParseException {
		ArrayList<User> userList = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(ConfigFileName + ".json");
		Object obj = jsonParser.parse(reader);
		JSONObject usersJson = (JSONObject) obj;
		long currentDatasetId = (long) usersJson.get("CurrentDatasetId");
		// This is for datasets in the configuration.json file
		JSONObject datasetsJson = (JSONObject) obj;
		JSONArray datasetArray = (JSONArray) datasetsJson.get("datasets");
		JSONArray array = (JSONArray) usersJson.get("users");

		File file = new File("logs.txt");
		if (file.exists()) {
			file.delete();
		} else
			file.createNewFile();
		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		// get user objects in the config file
		for (int i = 0; i < array.size(); i++) {
			JSONObject user = (JSONObject) array.get(i);
			long userId = (long) user.get("user id");
			String userName = (String) user.get("user name");
			String userType = (String) user.get("user type");
			double consistencyCP = (double) user.get("ConsistencyCheckProbability");
			userList.add(new User((int) userId, userName, userType, consistencyCP));

			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			System.out
					.println(timeStamp + " [ControlCenter] INFO ControlCenter created:" + userName + " as " + userType);
			bw.write((timeStamp + " [ControlCenter] INFO ControlCenter created:" + userName + " as " + userType));
			bw.write("\n");

		}
		String currentPath = "";

		for (int i = 0; i < datasetArray.size(); i++) {
			JSONObject dataset = (JSONObject) datasetArray.get(i);
			long datasetId = (long) dataset.get("dataset id");
			String datasetName = (String) dataset.get("dataset name");

			String datasetFilePath = (String) dataset.get("dataset file path");
			if (datasetId == currentDatasetId) {
				currentPath = datasetFilePath;
			} else {
				states.add(new WorkSpace((int) datasetId));
				states.get(states.size() - 1).createFromJson("./" + datasetFilePath);
			}

		}

		bw.close();
		WorkSpace workSpace = new WorkSpace(0);
		workSpace.createFromJson("./" + currentPath);
		getStates(userList, workSpace);

		int userCount = 0;
		for (int i = 0; i < userList.size(); i++)
			if (userList.get(i).getUserType().equals("RandomBot"))
				userCount++;

		boolean isSystemHasCapacity = true;
		while (isSystemHasCapacity) {

			ArrayList<Instance> instanceHasNotCompleted = new ArrayList<Instance>();
			instanceHasNotCompleted.addAll(workSpace.getDataset().getInstances());

			for (int i = 0; i < workSpace.getDataset().getInstances().size(); i++) {
				if (workSpace.getLogs().containsKey("instance" + (i + 1))) {
					LogHistory temp = workSpace.getLogs().get("instance" + (i + 1));
					if (temp.getUserList().size() == userCount) {
						instanceHasNotCompleted.remove(workSpace.getDataset().getInstances().get(i));
					}
				}
			}

			if (instanceHasNotCompleted.isEmpty()) {
				isSystemHasCapacity = false;
				break;
			}

			for (int i = 0; i < userList.size(); i++) {

				if (userList.get(i).getUserType().equals("RandomBot")) {
					Random rand = new Random();
					int consis = (int) userList.get(i).getConsistencyCP();

					if (rand.nextInt(100) + 1 > consis) {
						userList.get(i).getMechanism().doLabeling(workSpace, userList.get(i), instanceHasNotCompleted,
								true);
					} else {
						ArrayList<Instance> temp = new ArrayList<Instance>();

						for (int j = 0; j < instanceHasNotCompleted.size(); j++) {
							if (workSpace.getLogs()
									.containsKey("instance" + instanceHasNotCompleted.get(j).getInstanceId())) {
								LogHistory tempHis = workSpace.getLogs()
										.get("instance" + instanceHasNotCompleted.get(j).getInstanceId());
								ArrayList<User> tul = tempHis.getUserList();
								if (tul.contains(userList.get(i)))
									;
								temp.add(instanceHasNotCompleted.get(j));
							}
						}
						if (!temp.isEmpty())
							userList.get(i).getMechanism().doLabeling(workSpace, userList.get(i), temp, false);
					}

				}

			}

		}
		workSpace.CreateState();

		// User[] obejctList = new User[2];
		// obejctList[0] = userList.get(0);
		// obejctList[1] = userList.get(1);

		// workSpace.CreateJsonFromDataset(obejctList);

	}

	private void getStates(ArrayList<User> usersListOfAll, WorkSpace current) throws IOException {
		Path dir = Paths.get("./");

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "state*.json")) {
			for (Path file : stream) {
				try {
					Object obj = new JSONParser().parse(new FileReader(file.toString()));
					JSONObject jo = (JSONObject) obj;
					long datasetId = (long) jo.get("datasetId");
					long totalInstance = (long) jo.get("totalInstance");
					JSONArray InstancesArray = (JSONArray) jo.get("Instances");

					for (int i = 0; i < InstancesArray.size(); i++) {
						JSONObject instanceObject = (JSONObject) InstancesArray.get(i);
						String Id = (String) instanceObject.get("instanceId");
						String instanceText = (String) instanceObject.get("instanceText");
						long finalValue = (long) instanceObject.get("finalValue");

						// user List instance
						JSONArray temp = (JSONArray) instanceObject.get("userList");
						JSONObject userListInInstance = (JSONObject) temp.get(0);
						int[] users = new int[userListInInstance.size()];
						for (int j = 0; j < userListInInstance.size(); j++) {

							long userId = (long) userListInInstance.get("user" + (j + 1));
							users[j] = (int) userId;
						}
						JSONArray temp2 = (JSONArray) instanceObject.get("labels");
						JSONObject labelListInInstance = (JSONObject) temp2.get(0);
						String[] labels = new String[labelListInInstance.size()];

						for (int j = 0; j < labels.length; j++) {
							String value = (String) labelListInInstance.get("value" + (j + 1));
							labels[j] = value;
						}

						// [_*15**7**9*, _*15**11**13**12**14**16**7**6*]

						// this for add users to states
						for (int j = 0; j < states.size(); j++) {
							// baska dataset burda atiyorum emenike
							if (datasetId == states.get(j).getDataset().getDatasetId()) {
								HashMap<String, LogHistory> logs = states.get(j).getLogs();
								LogHistory logHistory = new LogHistory();
								ArrayList<User> u = new ArrayList<User>();

								for (int k = 0; k < users.length; k++) {
									u.add(usersListOfAll.get(users[k] - 1));
								}
								logHistory.setUserList(u);
								// labellari parcala buraya koy
								for (int k = 0; k < labels.length; k++) {
									String cumulativelabel = labels[k];
									cumulativelabel = cumulativelabel.replaceFirst("_", "");
									ArrayList<ArrayList<Log>> history = logHistory.getHistory();
									ArrayList<Log> logList = new ArrayList<>();
									String[] aloneLabels = cumulativelabel.split("_", -1);
									ArrayList<Label> ArrayListLabel = new ArrayList<>();
									for (int l = 0; l < aloneLabels.length; l++) {
										String aloneLabelsElement = aloneLabels[l];
										aloneLabelsElement = aloneLabelsElement.replaceFirst("@", "");
										String[] labelListFinal = aloneLabelsElement.split("@");
										for (int m = 0; m < labelListFinal.length; m++) {
											ArrayListLabel.add(states.get(j).getDataset().getLabels()
													.get(Integer.parseInt(labelListFinal[m]) - 1));
										}
										Log tempLog = new Log();
										tempLog.setLabels(ArrayListLabel);
										logList.add(tempLog);

									}
									history.add(logList);
									logHistory.setHistory(history);

								}

								logs.put("instance" + (Integer.parseInt(Id) + 1), logHistory);

							}
							if (datasetId == current.getDataset().getDatasetId()) {
								HashMap<String, LogHistory> logs = current.getLogs();
								LogHistory logHistory = new LogHistory();
								ArrayList<User> u = new ArrayList<User>();

								for (int k = 0; k < users.length; k++) {
									u.add(usersListOfAll.get(users[k] - 1));
								}
								logHistory.setUserList(u);
								
								// labellari parcala buraya koy
								for (int k = 0; k < labels.length; k++) {
									String cumulativelabel = labels[k];
									cumulativelabel = cumulativelabel.replaceFirst("_", "");
									ArrayList<ArrayList<Log>> history = logHistory.getHistory();
									ArrayList<Log> logList = new ArrayList<>();
									String[] aloneLabels = cumulativelabel.split("_", -1);
									ArrayList<Label> ArrayListLabel = new ArrayList<>();
									for (int l = 0; l < aloneLabels.length; l++) {
										String aloneLabelsElement = aloneLabels[l];
										aloneLabelsElement = aloneLabelsElement.replaceFirst("@", "");
										String[] labelListFinal = aloneLabelsElement.split("@");
										for (int m = 0; m < labelListFinal.length; m++) {
											ArrayListLabel.add(current.getDataset().getLabels()
													.get(Integer.parseInt(labelListFinal[m]) - 1));
										}
										Log tempLog = new Log();
										tempLog.setLabels(ArrayListLabel);
										logList.add(tempLog);

									}
									history.add(logList);
									if(Id.equals("11")) {
										int x = 0 ;
									}
									logHistory.setInstance(current.getDataset().getInstances().get(Integer.parseInt(Id)-1));
									logHistory.setFinalValue(current.getDataset().getLabels().get((int) (finalValue-1)));
									logHistory.setHistory(history);
									
								}
								
								logs.put("instance" + (Integer.parseInt(Id)), logHistory);

							}
						}

					}

				} catch (FileNotFoundException e) {
					System.out.println("fileNotFoundControlCenter");
					return;
				} catch (IOException e) {
					System.out.println("IoExceptionControlCenter");
					return;
				} catch (ParseException e) {
					System.out.println("ParseExceptionControlCenter");
					return;
				}

			}
		}

	}
}

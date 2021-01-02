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
import java.util.Random;
import java.util.Scanner;

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
	public void StartSystem(String ConfigFileName) throws Throwable {
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
		if (!file.exists()) {
            file.createNewFile();
        }
		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		// get user objects in the config file
		for (int i = 0; i < array.size(); i++) {
			JSONObject user = (JSONObject) array.get(i);
			long userId = (long) user.get("user id");
			String userName = (String) user.get("user name");
			String userType = (String) user.get("user type");
			double consistencyCP = (double) user.get("ConsistencyCheckProbability");
			String password=(String) user.get("password");
			userList.add(new User((int) userId, userName, userType, consistencyCP,password));
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			System.out
					.println(timeStamp + " [ControlCenter] INFO ControlCenter created:" + userName + " as " + userType);
			bw.write((timeStamp + " [ControlCenter] INFO ControlCenter created:" + userName + " as " + userType));
			bw.write("\n");
		}
		bw.close();
		String currentPath = "";
		ArrayList<String> current=new ArrayList<>();
		for (int i = 0; i < datasetArray.size(); i++) {
			JSONObject dataset = (JSONObject) datasetArray.get(i);
			long datasetId = (long) dataset.get("dataset id");
			String users = (String) dataset.get("users");
			String usersInDataset[] = users.split(",");
			String datasetName = (String) dataset.get("dataset name");
			String datasetFilePath = (String) dataset.get("dataset file path");
			if (datasetId == currentDatasetId) {
				currentPath = datasetFilePath;
				for (int j = 0; j < usersInDataset.length; j++) {
					current.add(usersInDataset[j]);
				}
			} else {
				states.add(new WorkSpace((int) datasetId));
				states.get(states.size() - 1).createFromJson("./" + datasetFilePath);
			}
		}
		WorkSpace workSpace = new WorkSpace(0);
		workSpace.createFromJson("./" + currentPath);
		getStates(userList, workSpace);
		int userCount = 0;
		for (int i = 0; i < userList.size(); i++)
			if ((userList.get(i).getUserType().equals("RandomBot")||userList.get(i).getUserType().equals("EvenBot"))&&current.contains((userList.get(i).getId()+"")))
				userCount++;
		MetricsHelper metricsHelper = new MetricsHelper(workSpace, states);
		boolean isSystemHasCapacity = true;
		boolean Auth=true;
		boolean random=false;
		Scanner sc = new Scanner(System.in); 
		while(Auth) {
			System.out.print("Please enter username: ");
			String userName=sc.nextLine();
			System.out.print("Please enter password: ");
			String password=sc.nextLine();
			for (int i = 0; i < userList.size(); i++) {
                if((userList.get(i).getUserType().equals("HumanUser")||userList.get(i).getUserType().equals("RatingBot"))&&(userList.get(i).getName().equals(userName)||(userName.equals("")&&password.equals("")))) {
                    if(userList.get(i).getName().equals(userName)&&userList.get(i).getPassword().equals(password)&& current.contains((userList.get(i).getId()+""))) {
                    random=false;
                    userList.get(i).getMechanism().doLabeling(workSpace, userList.get(i), workSpace.getDataset().getInstances(), true);
                    metricsHelper.UserMetric(userList);
            		metricsHelper.InstanceMetrics();
            		metricsHelper.DatasetMetric();
                    Auth=false;
                    break;
                 }
                    else if(userList.get(i).getName().equals(userName)&& !current.contains((userList.get(i).getId()+""))) {
                        System.out.println("This user is not valid for dataset" + workSpace.getDataset().getDatasetId());
                        break;
                    }
                    else if(userName.equals("")&&password.equals("")) {
                    	random=true;
                        Auth=false;
                 }
                    else{
                        System.out.println("Please check username and password");
                        break;
                 }
                }
            }
		}	
		if(random) {
		while (isSystemHasCapacity) {
			ArrayList<Instance> instanceHasNotCompleted = new ArrayList<Instance>();
			instanceHasNotCompleted.addAll(workSpace.getDataset().getInstances());
			for (int i = 0; i < workSpace.getDataset().getInstances().size(); i++) {
				if (workSpace.getLogs().containsKey("instance" + (i + 1))) {
					LogHistory temp = workSpace.getLogs().get("instance" + (i + 1));
					int botcount=0;
					for (int j = 0; j<temp.getUserList().size(); j++) {
						if(temp.getUserList().get(j).getUserType().equals("RandomBot")||temp.getUserList().get(j).getUserType().equals("EvenBot")) {
							botcount++;
						}
					}	
					if (botcount == userCount) {
						instanceHasNotCompleted.remove(workSpace.getDataset().getInstances().get(i));
					}
				}
			}
			if (instanceHasNotCompleted.isEmpty()) {
				isSystemHasCapacity = false;
				break;
			}
			for (int i = 0; i < userList.size(); i++) {
				if ((userList.get(i).getUserType().equals("RandomBot")||userList.get(i).getUserType().equals("EvenBot"))&&current.contains((userList.get(i).getId()+""))) {
					Random rand = new Random();
					int consis = (int) (userList.get(i).getConsistencyCP()*100);
					if (rand.nextInt(100) + 1 > consis) {
						userList.get(i).getMechanism().doLabeling(workSpace, userList.get(i), instanceHasNotCompleted,
								true);
						Thread.sleep(500);
						metricsHelper.UserMetric(userList);
						metricsHelper.InstanceMetrics();
						metricsHelper.DatasetMetric();
					} else {
						ArrayList<Instance> temp = new ArrayList<Instance>();
						for (int j = 0; j < instanceHasNotCompleted.size(); j++) {
							if (workSpace.getLogs()
									.containsKey("instance" + instanceHasNotCompleted.get(j).getInstanceId())) {
								LogHistory tempHis = workSpace.getLogs()
										.get("instance" + instanceHasNotCompleted.get(j).getInstanceId());
								ArrayList<User> tul = tempHis.getUserList();
								if (tul.contains(userList.get(i)))			
								temp.add(instanceHasNotCompleted.get(j));
							}
						}
						if (!temp.isEmpty()) {
							userList.get(i).getMechanism().doLabeling(workSpace, userList.get(i), temp, false);
							Thread.sleep(500);
							metricsHelper.UserMetric(userList);
							metricsHelper.InstanceMetrics();
							metricsHelper.DatasetMetric();
						}	
					}
				}	
			}	
		}
	}
		file = new File("logs.txt");
		if (!file.exists()) {
            file.createNewFile();
        }
		 fw = new FileWriter(file, true);
		 bw = new BufferedWriter(fw);
		bw.write("dataset logs for dataset" + currentDatasetId + "\n");
		bw.close();
		workSpace.CreateState();
		metricsHelper.UserMetric(userList);
		metricsHelper.InstanceMetrics();
		metricsHelper.DatasetMetric();
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
									for (int l = 0; l < aloneLabels.length; l++) {
										ArrayList<Label> ArrayListLabel = new ArrayList<>();
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
									logHistory.setInstance(states.get(j).getDataset().getInstances().get(Integer.parseInt(Id)-1));
									logHistory.setFinalValue(states.get(j).getDataset().getLabels().get((int) (finalValue-1)));
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
								if(Id.equals("2")) {
									int x = 0 ;
								}
								// labellari parcala buraya koy
								for (int k = 0; k < labels.length; k++) {
									String cumulativelabel = labels[k];
									cumulativelabel = cumulativelabel.replaceFirst("_", "");
									ArrayList<ArrayList<Log>> history = logHistory.getHistory();
									ArrayList<Log> logList = new ArrayList<>();
									String[] aloneLabels = cumulativelabel.split("_", -1);
									for (int l = 0; l < aloneLabels.length; l++) {
										ArrayList<Label> ArrayListLabel = new ArrayList<>();
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

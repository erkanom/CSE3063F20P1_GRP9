import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MetricsHelper {
	private WorkSpace current;
	private ArrayList<WorkSpace> states;
	public MetricsHelper(WorkSpace current, ArrayList<WorkSpace> states) {
		super();
		this.current = current;
		this.states = states;
		this.states.add(current);
	}
	public void DatasetMetric() throws Throwable {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for (int i = 0; i < states.size(); i++) {
			Map m1 = new LinkedHashMap();
			Dataset tempSet = states.get(i).getDataset();
			System.out.println("\ndataset id: " + tempSet.getDatasetId());
			m1.put("dataset id: ", tempSet.getDatasetId());
			HashMap<String, LogHistory> logs = states.get(i).getLogs();
			double Completenesspercentage = (logs.size() * 1.0 / tempSet.getInstances().size() * 1.0) * 100;
			System.out.println("Completeness percentage: " + Completenesspercentage);
			m1.put("Completeness percentage", Completenesspercentage);
			int[] labelCount = new int[tempSet.getLabels().size()];
			for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
				labelCount[entry.getValue().getFinalValue().getLabelId() - 1]++;
			}
			int sumOfallCOunt = 0;
			for (int j = 0; j < labelCount.length; j++) {
				sumOfallCOunt += labelCount[j];
			}
			String ClassDist = "";
			for (int j = 0; j < labelCount.length; j++) {
				if (labelCount[j] != 0) {
					ClassDist += (tempSet.getLabels().get(j).getLabelText() + " %"
							+ (labelCount[j] * 1.0 / sumOfallCOunt) * 100 + "   ");
				}
			}
			System.out.println("Class distribution based on final instance labels " + ClassDist);
			m1.put("Class distribution based on final instance labels ", ClassDist);
			System.out.println();
			JSONArray UniqueIns = new JSONArray();
			for (int j = 0; j < tempSet.getLabels().size(); j++) {
				Map m2 = new LinkedHashMap();
				Label choosenLabel = tempSet.getLabels().get(j);
				String instance = "";
				for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
					ArrayList<ArrayList<Log>> history = entry.getValue().getHistory();
					for (int k = 0; k < history.size(); k++) {
						ArrayList<Log> log = history.get(k);
						for (int l = 0; l < log.size(); l++) {
							ArrayList<Label> tempLabels = log.get(l).getLabels();
							if (tempLabels.contains(choosenLabel)) {
								if (!instance.contains(entry.getKey()))
									instance += " ," + entry.getKey();
							}
						}
					}
				}
				System.out.print("{Label: " + choosenLabel.getLabelText() + " Instances[ " + instance + "] }\n");
				m2.put("Label: " + choosenLabel.getLabelText() + " Instances", "[ " + instance + "] ");
				UniqueIns.add(m2);
			}
			m1.put("List number of unique instances: ", UniqueIns);
			int userCount = 0;
			ArrayList<User> userArrCount = new ArrayList<User>();
			for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
				ArrayList<User> tempList = entry.getValue().getUserList();
				for (int j = 0; j < tempList.size(); j++) {
					User user = tempList.get(j);
					if (!userArrCount.contains(user)) {
						userArrCount.add(user);
					}
				}
			}
			userCount = userArrCount.size();
			System.out.println("TotaluserAsigned " + userCount);
			m1.put("TotaluserAsigned", userCount);
			JSONArray CPus = new JSONArray();
			for (int j = 0; j < userArrCount.size(); j++) {
				Map m3 = new LinkedHashMap();
				User user = userArrCount.get(j);
				int userCP = 0;
				for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
					if (entry.getValue().getUserList().contains(user)) {
						userCP++;
					}
				}
				System.out.println("user " + user.getId() + " Complete " + (+userCP * 1.0 / logs.size() * 100));
				m3.put("user " + user.getId(), " Complete " + (+userCP * 1.0 / logs.size() * 100));
				CPus.add(m3);
			}
			m1.put("List of users assigned and their completeness percentage", CPus);
			System.out.println();
			System.out.println("consistency percentage: ");
			String consis = "";
			for (int j = 0; j < userArrCount.size(); j++) {
				User user = userArrCount.get(j);
				int doubled = 0;
				int unique = 0;
				for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
					if (entry.getValue().getUserList().contains(user)) {
						int UserLogIndex = entry.getValue().getUserList().indexOf(user);
						ArrayList<Log> logForUser = entry.getValue().getHistory().get(UserLogIndex);
						unique++;
						if (logForUser.size() > 1) {
							doubled++;
						}
					}
				}
				consis += (" user " + user.getId() + " %" + (int) (doubled * 1.0 / unique * 100) + " ");
			}
			System.out.print(consis);
			m1.put(" List of users assigned and their consistency percentage ", consis);
			ja.add(m1);
		}
		jo.put(" ", ja);
		PrintWriter pw = new PrintWriter("DataMetrics.json");
		String output = jo.toJSONString();
		output = output.replaceAll(",\"", ",\n\"");
		output = output.replaceAll("\\{\"", "\\{\n\"");
		output = output.replaceAll("\\},\\{", "\\},\n\\{");
		output = output.replaceAll("\\}\\]", "\\}\n\\]");
		output = output.replaceFirst("\\]\\}", "\\]\n\\}");
		pw.write(output);
		pw.flush();
		pw.close();
	}
	public void InstanceMetrics() throws FileNotFoundException {
		JSONObject jo = new JSONObject();
		JSONArray DatabaseArray = new JSONArray();
		for (int i = 0; i < states.size(); i++) {
			Map databse = new LinkedHashMap();
			Dataset tempSet = states.get(i).getDataset();
			HashMap<String, LogHistory> logs = states.get(i).getLogs();
			System.out.println("\nFOR DATASET " + tempSet.getDatasetId());
			databse.put(" DATASET ", tempSet.getDatasetId());
			JSONArray instanceArr = new JSONArray();
			for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
				Map ins = new LinkedHashMap();
				System.out.println("\n\n" + entry.getKey() + " " + entry.getValue().getInstance().getinstanceText());
				ins.put(" ", entry.getKey() + " " + entry.getValue().getInstance().getinstanceText());
				int totalLabel = 0;
				ArrayList<ArrayList<Log>> TotalUser = entry.getValue().getHistory();
				for (int j = 0; j < TotalUser.size(); j++) {
					ArrayList<Log> userLogs = TotalUser.get(j);
					for (int k = 0; k < userLogs.size(); k++) {
						Log userLog = userLogs.get(k);
						totalLabel += userLog.getLabels().size();
					}
				}
				System.out.println("Total label " + totalLabel);
				ins.put("Total label ", totalLabel);
				int[] forUniqueLabel = new int[tempSet.getLabels().size()];
				int uniqueNumber = 0;
				for (int j = 0; j < TotalUser.size(); j++) {
					ArrayList<Log> userLogs = TotalUser.get(j);
					for (int k = 0; k < userLogs.size(); k++) {
						Log userLog = userLogs.get(k);
						ArrayList<Label> userLabels = userLog.getLabels();
						for (int l = 0; l < userLabels.size(); l++) {
							forUniqueLabel[userLabels.get(l).getLabelId() - 1]++;
						}
					}
				}
				for (int j = 0; j < forUniqueLabel.length; j++) {
					if (forUniqueLabel[j] > 0) {
						uniqueNumber++;
					}
				}
				System.out.println("Number of unique label assignments " + uniqueNumber);
				ins.put("Number of unique label assignments ", uniqueNumber);
				System.out.println("Number of unique users: " + entry.getValue().getUserList().size());
				ins.put("Number of unique users: ", entry.getValue().getUserList().size());
				int max = -0;
				int mostIndex = 0;
				for (int j = 0; j < forUniqueLabel.length; j++) {
					if (forUniqueLabel[j] > max) {
						max = forUniqueLabel[j];
						mostIndex = j;
					}
				}
				System.out.println(
						"Most frequent class label and percentage: " + tempSet.getLabels().get(mostIndex).getLabelText()
								+ " %" + (int) ((max * 1.0 / totalLabel) * 100));
				ins.put("Most frequent class label and percentage: ", tempSet.getLabels().get(mostIndex).getLabelText()
						+ " %" + (int) ((max * 1.0 / totalLabel) * 100));
				System.out.println("List class labels and percentages : ");
				String ListLbael = "";
				for (int j = 0; j < forUniqueLabel.length; j++) {
					ListLbael += (" " + tempSet.getLabels().get(j).getLabelText() + " %"
							+ (int) ((forUniqueLabel[j] * 1.0 / totalLabel) * 100));
				}
				System.out.print(ListLbael);
				ins.put("List class labels and percentages : ", ListLbael);
				double entropy = 0;
				for (int j = 0; j < forUniqueLabel.length; j++) {
					if (forUniqueLabel[j] > 0)
						if ((forUniqueLabel[j] * 1.0) / (totalLabel * 1.0) > 0)
							if ((Math.log(uniqueNumber) * 1.0) > 0)
								entropy += ((forUniqueLabel[j] * 1.0) / (totalLabel * 1.0))
										* ((Math.log((forUniqueLabel[j] * 1.0) / (totalLabel * 1.0)))
												/ (Math.log(uniqueNumber) * 1.0));

				}
				System.out.println("\nentropy: " + -1 * entropy);
				ins.put("entropy: ", -1 * entropy);
				instanceArr.add(ins);
			}
			databse.put("instances ", instanceArr);
			if (!Objects.isNull(databse))
				;
			DatabaseArray.add(databse);
		}
		jo.put("Datasets", DatabaseArray);
		PrintWriter pw = new PrintWriter("InstanceMetric.json");
		String output = jo.toJSONString();
		output = output.replaceAll(",\"", ",\n\"");
		output = output.replaceAll("\\{\"", "\\{\n\"");
		output = output.replaceAll("\\},\\{", "\\},\n\\{");
		output = output.replaceAll("\\}\\]", "\\}\n\\]");
		output = output.replaceFirst("\\]\\}", "\\]\n\\}");
		pw.write(output);
		pw.flush();
		pw.close();
	}

	public void UserMetric(ArrayList<User> users) throws FileNotFoundException {
		System.out.println();
		JSONObject jobject = new JSONObject();
		JSONArray joArray = new JSONArray();
		for (int j = 0; j < users.size(); j++) {
			Map jo = new LinkedHashMap();
			User user = users.get(j);
			if (user.getUserType().equals("RandomBot")) {
				System.out.println("\nuser: " + user.getId());
				jo.put("user: ", user.getId());
				int numOfDataset = 0;
				for (int i = 0; i < states.size(); i++) {
					HashMap<String, LogHistory> logs = states.get(i).getLogs();
					for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
						if (entry.getValue().getUserList().contains(user)) {
							numOfDataset++;
							break;
						}
					}
				}
				System.out.println("Number of datasets assigned " + numOfDataset);
				jo.put("Number of datasets assigned ", numOfDataset);
				String datacomp = "";
				for (int i = 0; i < states.size(); i++) {
					HashMap<String, LogHistory> logs = states.get(i).getLogs();
					int labelledCount = 0;
					for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
						if (entry.getValue().getUserList().contains(user)) {
							labelledCount++;
						}
					}
					datacomp += (" Dataset: " + states.get(i).getDataset().getDatasetId() + " %"
							+ (int) (labelledCount * 1.0 / logs.size()) * 100 + " ");
				}
				System.out.println("completeness percentage " + datacomp);
				jo.put("completeness percentage ", datacomp);
				int labelledCount = 0;
				for (int i = 0; i < states.size(); i++) {
					HashMap<String, LogHistory> logs = states.get(i).getLogs();
					for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
						if (entry.getValue().getUserList().contains(user)) {
							labelledCount++;
						}
					}
				}
				int uniqueLabel = 0;
				for (int i = 0; i < states.size(); i++) {
					HashMap<String, LogHistory> logs = states.get(i).getLogs();
					for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
						if (entry.getValue().getUserList().contains(user)) {
							int UserIndex = entry.getValue().getUserList().indexOf(user);
							ArrayList<Log> userLog = entry.getValue().getHistory().get(UserIndex);
							if (userLog.size() == 1) {
								uniqueLabel++;
							}
						}
					}
				}
				int inconsis = 0;
				for (int i = 0; i < states.size(); i++) {
					HashMap<String, LogHistory> logs = states.get(i).getLogs();
					for (Map.Entry<String, LogHistory> entry : logs.entrySet()) {
						if (entry.getValue().getUserList().contains(user)) {
							int UserIndex = entry.getValue().getUserList().indexOf(user);
							ArrayList<Log> userLog = entry.getValue().getHistory().get(UserIndex);
							if (userLog.size() > 1) {
								for (int k = 0; k < userLog.size() - 1; k++) {
									boolean control = true;
									ArrayList<Label> label1 = userLog.get(k).getLabels();
									ArrayList<Label> label2 = userLog.get(k + 1).getLabels();
									for (int l = 0; l < label1.size(); l++) {
										if (!label2.contains(label1.get(l))) {
											inconsis++;
											control = false;
										}
									}
									if (control == false) {
										break;
									}
								}
							}
						}
					}
				}
				LogReader logReader = new LogReader();
				if (!Objects.isNull(logReader.GetAllDate(user))) {
					ArrayList<java.util.Date> LogDates = logReader.GetAllDate(user);
					long totalTime = logReader.ReadTxtUser(user);
					if (LogDates.size() > 0) {
						System.out.println("Total labelled " + LogDates.size());
						System.out.println("Total number of unique instances labeled: " + labelledCount);
						System.out.println("inconsistency %" + (int) ((inconsis * 1.0 / labelledCount) * 100));
						System.out.println("Average Time: " + ((totalTime * 1.0) / ((LogDates.size() * 1.0))));
						jo.put("Total labelled ", LogDates.size());
						jo.put("Total number of unique instances labeled: ", labelledCount);
						jo.put("inconsistency %", (int) ((inconsis * 1.0 / labelledCount) * 100));
						jo.put("Average Time: ", ((totalTime * 1.0) / ((LogDates.size() * 1.0))));
					} else {
						System.out.println("Total labelled 0");
						System.out.println("Total number of unique instances labeled: 0");
						System.out.println("inconsistency %0");
						System.out.println("Average Time: 0");
						jo.put("Total labelled ", 0);
						jo.put("Total number of unique instances labeled: ", 0);
						jo.put("inconsistency %", 0);
						jo.put("Average Time: ", 0);
					}
					double stDev = 0;
					if (!LogDates.isEmpty() && LogDates != null) {
						for (int i = 0; i < LogDates.size() - 1; i++) {
							long diff = LogDates.get(i).getTime() - LogDates.get(i + 1).getTime();
							diff = diff / 1000;
							if (diff == 0)
								stDev += Math.pow(0.5 - ((totalTime * 1.0) / (LogDates.size() * 1.0)), 2);
							else
								stDev += Math.pow(diff - ((totalTime * 1.0) / (LogDates.size() * 1.0)), 2);
						}
					}
					stDev = stDev / labelledCount * 1.0;
					stDev = Math.sqrt(stDev);
					System.out.println("stDev: " + stDev);
					jo.put("stDev: ", stDev);
				}
			}
			joArray.add(jo);
		}
		jobject.put("Users ", joArray);
		PrintWriter pw = new PrintWriter("UserMetric.json");
		String output = jobject.toJSONString();
		output = output.replaceAll(",\"", ",\n\"");
		output = output.replaceAll("\\{\"", "\\{\n\"");
		output = output.replaceAll("\\},\\{", "\\},\n\\{");
		output = output.replaceAll("\\}\\]", "\\}\n\\]");
		output = output.replaceFirst("\\]\\}", "\\]\n\\}");
		pw.write(output);
		pw.flush();
		pw.close();
	}
}

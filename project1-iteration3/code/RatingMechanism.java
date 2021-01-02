import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class RatingMechanism extends LabelMechanism {
	@Override
	public void doLabeling(WorkSpace workSpace, User user, ArrayList<Instance> ins, boolean condition)
			throws IOException {
		System.out.println("RatingBot v1.0 Please Select labels ");
		for (int i = 0; i < workSpace.getDataset().getLabels().size(); i++) {
			System.out.println(workSpace.getDataset().getLabels().get(i).getLabelId() + ") "
					+ workSpace.getDataset().getLabels().get(i).getLabelText());
		}
		boolean RightInput = true;
		while (RightInput) {
			Scanner sc = new Scanner(System.in);
			String choosedLabel = sc.nextLine();
			String labels[] = choosedLabel.split(" ");
			if (labels.length > workSpace.getDataset().getMaxLabel()) {
				System.out.print("Maximum " + workSpace.getDataset().getMaxLabel() + " allowed please re-enter: ");
				continue;
			}
			for (int j = 0; j < labels.length; j++) {
				String label = labels[j];
				label = label.trim();
				labels[j] = label;
			}
			ArrayList<Label> labelList = new ArrayList<>();
			for (int j = 0; j < labels.length; j++) {
				for (int j2 = 0; j2 < workSpace.getDataset().getLabels().size(); j2++) {
					if (labels[j].equals(workSpace.getDataset().getLabels().get(j2).getLabelId() + "")) {
						if (labelList.contains(workSpace.getDataset().getLabels().get(j2))) {
							System.out.println("Duplicate label please re-enter :");
							continue;
						} else {
							labelList.add(workSpace.getDataset().getLabels().get(j2));
						}
					}
				}
			}
			if (labelList.size() != labels.length) {
				System.out.println("Wrong label selection please re-enter :");
				continue;
			}
			RightInput = false;
			// no labelled before
			for (int i = 0; i < ins.size(); i++) {
				LogWriter logHelper = LogWriter.getLog();
				if (!workSpace.getLogs().containsKey("instance" + ins.get(i).getInstanceId())) {
					HashMap<String, LogHistory> temp = new HashMap<>();
					LogHistory name = new LogHistory();
					name.setInstance(ins.get(i));
					name.getUserList().add(user);
					Log tempLog = new Log();
					labelList.get(0);
					tempLog.setLabels(labelList);
					ArrayList<Log> e = new ArrayList<>();
					e.add(tempLog);
					name.getHistory().add(e);
					Random rand = new Random();
					int finalIndex = rand.nextInt(labels.length);
					name.setFinalValue(
							workSpace.getDataset().getLabels().get(Integer.parseInt(labels[finalIndex]) - 1));
					temp.put("instance" + ins.get(i).getInstanceId(), name);
					workSpace.getLogs().putAll(temp);
					logHelper.getLogOutput(name, user);
					workSpace.CreateState();
				} else {
					// daha once labellandi ama baskasi tarafindan
					LogHistory tempHistory = workSpace.getLogs().get("instance" + ins.get(i).getInstanceId());
					// labeled before by different user,not labeling again
					if (!tempHistory.getUserList().contains(user)) {
						tempHistory.getUserList().add(user);
						Log tempLog = new Log();
						ArrayList<Log> e = new ArrayList<>();
						e.add(tempLog);
						tempHistory.getHistory().add(e);
						int[] LabelCount = new int[workSpace.getDataset().getLabels().size()];
						for (int k = 0; k < tempHistory.getHistory().size(); k++) {
							ArrayList<Log> logArray = tempHistory.getHistory().get(k);
							for (int j = 0; j < logArray.size(); j++) {
								Log LogTempForArray = logArray.get(j);
								ArrayList<Label> tempLabelForAray = LogTempForArray.getLabels();
								for (int l = 0; l < tempLabelForAray.size(); l++) {
									LabelCount[tempLabelForAray.get(l).getLabelId() - 1]++;
								}
							}
						}
						int max = -1;
						int label = -1;
						for (int j = 0; j < LabelCount.length; j++) {
							if (LabelCount[j] > max) {
								max = LabelCount[j];
								label = j;
							}
						}
						tempHistory.setFinalValue(workSpace.getDataset().getLabels().get(label));
						tempLog.getLabels().addAll(labelList);
						logHelper.getLogOutput(tempHistory, user);
						workSpace.CreateState();
					}
				}
			}
		}
	}
}

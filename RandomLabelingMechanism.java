import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class RandomLabelingMechanism extends LabelMechanism {
	public RandomLabelingMechanism() {
	}

	@Override
	public void doLabeling(WorkSpace workSpace, User user) {
		Random rand = new Random();
		int instanceR = rand.nextInt(workSpace.getDataset().getInstances().size()) + 1;
		int[] labelIds = randomLabelId(workSpace);

		try {
			if (verifyAndLabel(user, labelIds, instanceR, workSpace)) {
			} else {
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int[] randomLabelId(WorkSpace workSpace) {
		Random rand = new Random();
		int labelR = rand.nextInt(workSpace.getDataset().getMaxLabel()) + 1;
		int[] chosed = new int[labelR];
		int index = 0;
		int count = 0;
		for (int i = 0; i < labelR; i++) {

			int choosenLabelId = rand.nextInt(workSpace.getDataset().getLabels().size());
			boolean control = true;
			for (int j = 0; j < chosed.length; j++) {
				if (chosed[j] == choosenLabelId) {
					control = false;
					break;
				}
			}
			if (control == true) {
				chosed[index] = choosenLabelId;
				count++;
				index++;
			}
		}
		int[] result = new int[count];
		for (int i = 0; i < result.length; i++) {
			result[i] = chosed[i];
		}
		return result;
	}

	private boolean verifyAndLabel(User user, int[] choosenLabels, int instance, WorkSpace workspace)
			throws IOException {
		boolean result = false;
		if (workspace.getLogs().get("instance" + instance).isEmpty()) {
			ArrayList<Label> temp = new ArrayList<Label>();
			for (int i = 0; i < choosenLabels.length; i++) {
				temp.add(workspace.getDataset().getLabels().get(choosenLabels[i]));
			}
			workspace.getLogs().get("instance" + instance).put("user " + user.getId(), temp);
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			System.out.println("\n" + timeStamp + " [InstanceTagger]INFO " + "user id: " + user.getId() + " "
					+ user.getName() + " tagged" + " instance:" + instance + "\n with class label :");
			File file = new File("logs.txt");
			if (!file.exists()) {
				file.createNewFile();

			}
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\n" + timeStamp + " [InstanceTagger]INFO " + "user id: " + user.getId() + " " + user.getName()
					+ " tagged" + " instance:" + instance + "\n with class label :");
			bw.write("\n");
		

			for (int i = 0; i < choosenLabels.length; i++) {
				Label label = workspace.getDataset().getLabels().get(choosenLabels[i]);
				System.out.println(label.getLabelId() + ": " + label.getLabelText());
				bw.write(label.getLabelId() + ": " + label.getLabelText());
				bw.write("\n");
				
			}
			bw.close();
			result = true;
		}
		return result;
	}
}

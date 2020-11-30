import java.util.ArrayList;
import java.util.Random;

public class RandomLabelingMechanism extends LabelMechanism {

	public RandomLabelingMechanism() {

	}

	@Override
	public void doLabeling(WorkSpace workSpace, int userId) {

		Random rand = new Random();

		int instanceR = rand.nextInt(workSpace.getDataset().getInstances().size()) + 1;
		int[] labelIds = randomLabelId(workSpace);

		if (verifyAndLabel(userId, labelIds, instanceR, workSpace)) {
			System.out.println("User :" + userId + "\n");

		} else {

		}

	}

	private int[] randomLabelId(WorkSpace workSpace) {

		Random rand = new Random();
		int labelR = rand.nextInt(workSpace.getDataset().getMaxLabel()) + 1;

		int[] chosed = new int[labelR];
		int index = 0;
		for (int i = 0; i < labelR; i++) {

			int choosenLabelId = rand.nextInt(workSpace.getDataset().getLabels().size());
			boolean control = true;
			for (int j = 0; j < chosed.length; j++) {
				if (chosed[j] == choosenLabelId) {
					control = false;
				}
			}

			if (control) {
				chosed[index] = choosenLabelId;
			}

		}

		return chosed;

	}

	private boolean verifyAndLabel(int userId, int[] choosenLabels, int instance, WorkSpace workspace) {
		boolean result = false;
		if (workspace.getLogs().get("instance" + instance).isEmpty()) {
			ArrayList<Label> temp = new ArrayList<Label>();
			for (int i = 0; i < choosenLabels.length; i++) {
				temp.add(workspace.getDataset().getLabels().get(choosenLabels[i]));

			}

			workspace.getLogs().get("instance" + instance).put("user " + userId, temp);
			System.out.print("instance: " + instance + " labeled by");
			result = true;

		}
		return result;
	}

	
}

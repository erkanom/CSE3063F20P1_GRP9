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

		if (verifyAndLabel(user, labelIds, instanceR, workSpace)) {
		} else {
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
	private boolean verifyAndLabel(User user, int[] choosenLabels, int instance, WorkSpace workspace) {
		boolean result = false;
		if (workspace.getLogs().get("instance" + instance).isEmpty()) {
			ArrayList<Label> temp = new ArrayList<Label>();
			for (int i = 0; i < choosenLabels.length; i++) {
				temp.add(workspace.getDataset().getLabels().get(choosenLabels[i]));
			}
			workspace.getLogs().get("instance" + instance).put("user " + user.getId(), temp);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			//System.out.println(timeStamp+" [InstanceTagger]INFO "+"user id: "+user.getId()+" "+user.getName()+" tagged"+" instance:"+instance+);
			result = true;
		}
		return result;
	}
}

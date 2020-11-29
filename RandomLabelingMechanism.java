import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomLabelingMechanism extends LabelMechanism {
	private Dataset dataset;

	public RandomLabelingMechanism(Dataset dataset) {
		super(dataset);
		this.dataset = super.getDataset();

	}

	@Override
	public void doLabeling(User[] users) {
		

		for (int i = 0; i < dataset.getInstances().size(); i++) {
			Label[] controlList = new Label[this.dataset.getMaxLabel()];
			Arrays.fill(controlList, null);
			Instance selectedInstance = this.dataset.getInstances().get(i);

			Random userRandomGenerator = new Random();
			int userRandom = userRandomGenerator.nextInt(users.length);
			User selectedUser = users[userRandom];

			Random instanceMaxLabelRandomGenerator = new Random();

			int instanceMaxLabelRandom = instanceMaxLabelRandomGenerator.nextInt(this.dataset.getMaxLabel());

			int controlListIndex = 0;
			for (int k = 0; k < instanceMaxLabelRandom; k++) {

				Random labelRandomGenerator = new Random();

				int labelRandom = labelRandomGenerator.nextInt(this.dataset.getLabels().size());

				Label selectedLabel = this.dataset.getLabels().get(labelRandom);

				if (selectedInstance.getLabelCount() < selectedInstance.getMaxLabel()) {

					int controlForAvaiblty = 1;
					for (int j = 0; j < controlList.length; j++) {
						if (controlList[j] != null)
							if (controlList[j].getLabelId() == selectedLabel.getLabelId()) {
								controlForAvaiblty = 0;

							}

					}
					int itemCounter = 0;
					for (int j = 0; j < controlList.length; j++) {

						if (controlList[j] != null) {
							itemCounter++;

						}

					}
					if (itemCounter == controlList.length) {
						controlForAvaiblty = 0;
					}
					if (controlForAvaiblty == 1) {
						controlList[controlListIndex] = selectedLabel;
						controlListIndex++;
					}

				}

			}
			System.out.println("instance: " + selectedInstance.getInstanceId());
			System.out.println("User : " + selectedUser.getId());
			for (int j = 0; j < controlList.length; j++) {
				if (controlList[j] != null)
					System.out.print("  *" + controlList[j].getLabelId() + "*  \n");

			}
		}

		;
	}

}

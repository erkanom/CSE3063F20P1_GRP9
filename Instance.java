
public class Instance {
	private int instanceId;
	private String instanceText;
	private int maxLabel;
	private int labelCount;
	
	public Instance(int instanceId, String labelText,int maxLabel) {
		super();
		this.instanceId = instanceId;
		this.instanceText = labelText;
		this.maxLabel=maxLabel;
		this.labelCount=0;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public String getLabelText() {
		return instanceText;
	}

	public void setLabelText(String labelText) {
		this.instanceText = labelText;
	}

	public int getMaxLabel() {
		return maxLabel;
	}

	public void setMaxLabel(int maxLabel) {
		this.maxLabel = maxLabel;
	}

	public String getInstanceText() {
		return instanceText;
	}

	public void setInstanceText(String instanceText) {
		this.instanceText = instanceText;
	}

	public int getLabelCount() {
		return labelCount;
	}

	public void setLabelCount(int labelCount) {
		this.labelCount = labelCount;
	}
	
	
	
}

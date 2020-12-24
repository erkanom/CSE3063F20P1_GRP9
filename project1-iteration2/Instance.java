
public class Instance {
	private int instanceId;
	private String instanceText;
	private int maxLabel;
	private Label finalVal;
	public Instance(int instanceId, String instanceText, int maxLabel) {
		super();
		this.instanceId = instanceId;
		this.instanceText = instanceText;
		this.maxLabel = maxLabel;
	}
	public int getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}
	public String getinstanceText() {
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
	public void setInstanceText(String instanceText) {
		this.instanceText = instanceText;
	}
}

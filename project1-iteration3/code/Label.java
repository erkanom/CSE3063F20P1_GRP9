
public class Label {
	private int labelId;
	private String labelText;
	public Label(int labelId, String labelText) {
		this.labelId = labelId;
		this.labelText = labelText;
	}
	public int getLabelId() {
		return labelId;
	}
	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}
	public String getLabelText() {
		return labelText;
	}
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}	
}

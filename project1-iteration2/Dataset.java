import java.util.ArrayList;
public class Dataset {
	private int datasetId;   
	private String datasetName;
	private int maxLabel;
	private String instanceType;
	private ArrayList<Label> labels = new ArrayList<Label>();
	private ArrayList<Instance> instances = new ArrayList<Instance>();
	public Dataset(int datasetId, String instanceType, String datasetName, int maxLabel) {
		super();
		this.datasetId = datasetId;
		this.instanceType = instanceType;
		this.datasetName = datasetName;
		this.maxLabel = maxLabel;
	}
	public int getDatasetId() {
		return datasetId;
	}
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}
	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	public int getMaxLabel() {
		return maxLabel;
	}
	public void setMaxLabel(int maxLabel) {
		this.maxLabel = maxLabel;
	}
	public void addInstance(Instance instance) {
		this.instances.add(instance);
	}
	public void addLabel(Label label) {
		this.labels.add(label);
	}
	public ArrayList<Label> getLabels() {
		return labels;
	}
	public void setLabels(ArrayList<Label> labels) {
		this.labels = labels;
	}
	public ArrayList<Instance> getInstances() {
		return instances;
	}
	public void setInstances(ArrayList<Instance> instances) {
		this.instances = instances;
	}
}

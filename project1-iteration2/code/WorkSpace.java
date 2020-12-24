
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
public class WorkSpace {
	private Dataset dataset;
	private int id;
	private HashMap<String, LogHistory> logs;
	public WorkSpace(int id) {
		super();
		this.logs = new HashMap<>();
	}
	public void createFromJson(String jsonFileName) throws FileNotFoundException, IOException, ParseException {
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader("./" + jsonFileName);
		Object obj = jsonParser.parse(reader);
		JSONObject dataset = (JSONObject) obj;
		long datasetId = (long) dataset.get("dataset id");
		String datasetName = (String) dataset.get("dataset name");
		String instanceType = (String) dataset.get("instance type");
		long maxlabel = (long) dataset.get("maximum number of labels per instance");
		this.dataset = new Dataset((int) datasetId, instanceType, datasetName, (int) maxlabel);
		JSONArray array = (JSONArray) dataset.get("class labels");
		for (int i = 0; i < array.size(); i++) {
			JSONObject classLabel = (JSONObject) array.get(i);
			long labelID = (long) classLabel.get("label id");
			String labelText = (String) classLabel.get("label text");
			Label tempLabel = new Label((int) labelID, labelText);
			this.dataset.addLabel(tempLabel);
		}
		JSONArray array1 = (JSONArray) dataset.get("instances");
		for (int i = 0; i < array1.size(); i++) {
			JSONObject instances = (JSONObject) array1.get(i);
			long id = (long) instances.get("id");
			String instanceText = (String) instances.get("instance");
			Instance tempInstance = new Instance((int) id, instanceText, (int) maxlabel);
			this.dataset.addInstance(tempInstance);
			ArrayList<Label> tempLabel = new ArrayList<>();
			HashMap<String, ArrayList<Label>> tempMap = new HashMap<>();
		}
	}
	public void CreateState() throws FileNotFoundException {
		JSONObject jo = new JSONObject();
		jo.put("datasetId", this.dataset.getDatasetId());
		jo.put("totalInstance", this.dataset.getInstances().size());
		JSONArray instance = new JSONArray();
		for (Map.Entry<String, LogHistory> entry : this.logs.entrySet()) {
			Map m = new LinkedHashMap();
			String key = entry.getKey();
			LogHistory value = entry.getValue();
			key = key.replace("instance", "");
			m.put("instanceId", key);
			m.put("instanceText", value.getInstance().getinstanceText());
			m.put("finalValue", value.getFinalValue().getLabelId());
			JSONArray userList = new JSONArray();
			Map u = new LinkedHashMap();
			for (int i = 0; i < value.getUserList().size(); i++) {
				u.put("user" + (i + 1), value.getUserList().get(i).getId());
			}
			userList.add(u);
			JSONArray labels = new JSONArray();
			Map l = new LinkedHashMap();
			for (int i = 0; i < value.getHistory().size(); i++) {
				String temp = "";
				ArrayList<Log> logs = value.getHistory().get(i);
				for (int j = 0; j < logs.size(); j++) {
					temp += "_";
					ArrayList<Label> tempLabelList = logs.get(j).getLabels();
					for (int k = 0; k < tempLabelList.size(); k++) {
						temp += "@";
						temp += "" + tempLabelList.get(k).getLabelId();
					}
				}
				l.put("value" + (i + 1), temp);
			}
			labels.add(l);
			m.put("userList", userList);
			m.put("labels", labels);
			instance.add(m);
		}
		jo.put("Instances", instance);
		PrintWriter pw = new PrintWriter("state" + dataset.getDatasetId() + ".json");
		String output = jo.toJSONString();
		output = output.replaceFirst("\\{\"", "\\{\\\n\"");
		output = output.replaceAll("\\[\\{", "\\[\n\\{");
		output = output.replaceAll("\\},\\{", "\\},\n\\{");
		output = output.replaceAll("\\}\\]", "\\}\n\\]");
		output = output.replaceFirst("\\]\\}", "\\]\n\\}");
		pw.write(output);
		pw.flush();
		pw.close();
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public HashMap<String, LogHistory> getLogs() {
		return logs;
	}
	public void setLogs(HashMap<String, LogHistory> logs) {
		this.logs = logs;
	}
}

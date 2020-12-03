
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WorkSpace {
	private Dataset dataset;
	private int id;
	private HashMap<String, Map<String, ArrayList<Label>>> logs;

	public WorkSpace(int id) {
		super();
		this.logs = new HashMap<>();
		this.id = id;
	}

	public void createFromJson(String jsonFileName) throws FileNotFoundException, IOException, ParseException {
		HashMap<String, Map<String, ArrayList<Label>>> tempLog = new HashMap<>();
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
			tempLog.put("instance" + id, tempMap);

		}
		this.logs = tempLog;

	}
	public void CreateJsonFromDataset(Object[] objects) throws IOException {
		Map<String, Object> outputDataset = new LinkedHashMap();
		JSONArray classLabels = new JSONArray();
		for (int i = 0; i < dataset.getLabels().size(); i++) {
			Map m = new LinkedHashMap(2);
			m.put("label id", dataset.getLabels().get(i).getLabelId());
			m.put("label text", dataset.getLabels().get(i).getLabelText().trim());
			classLabels.add(m);
		}
		JSONArray classInstances = new JSONArray();
		for (int i = 0; i < dataset.getInstances().size(); i++) {
			Map ins = new LinkedHashMap(2);
			ins.put("id:" + dataset.getInstances().get(i).getInstanceId(),
					"instance: " + dataset.getInstances().get(i).getinstanceText());
			classInstances.add(ins);
		}
		JSONArray InstanceLabelConection = new JSONArray();

		for (int i = 0; i < logs.size(); i++) {
			Map ins = new LinkedHashMap(2);

			String toTakeSecondOne = "instance" + (i + 1);
			// String firstOne = "instance id: " + (i + 1);
			int userInfo = Integer.parseInt(logs.get(toTakeSecondOne).toString().substring(6, 7));
			String labelInfo = "[";

			for (int j = 0; j < logs.get(toTakeSecondOne).get("user " + userInfo).size(); j++) {
				if (j < logs.get(toTakeSecondOne).get("user " + userInfo).size()
						&& (j + 1) != logs.get(toTakeSecondOne).get("user " + userInfo).size())
					labelInfo += logs.get(toTakeSecondOne).get("user " + userInfo).get(j).getLabelId() + ",";
				else
					labelInfo += logs.get(toTakeSecondOne).get("user " + userInfo).get(j).getLabelId();
			}
			labelInfo += "]";
			String timeStamp = "";
			timeStamp += new SimpleDateFormat("yyyy MM dd, HH mm ss").format(Calendar.getInstance().getTime());
			timeStamp += "";
			ins.put("instance id", (i + 1));
			ins.put("class label ids", labelInfo);
			ins.put("user id", userInfo);
			ins.put("datetime", timeStamp);
			InstanceLabelConection.add(ins);

		}
		outputDataset.put("dataset id", dataset.getDatasetId());
		outputDataset.put("dataset name", dataset.getDatasetName());
		outputDataset.put("maximum number of labels", dataset.getMaxLabel());
		outputDataset.put("class labels", classLabels);
		outputDataset.put("instances", classInstances);
		outputDataset.put("class label assignments", InstanceLabelConection);
		JSONObject outJson = new JSONObject(outputDataset);
		PrintWriter pw;
		try {
			pw = new PrintWriter("JSONExample2.json");
			String output = outJson.toJSONString();
			output = output.replaceFirst("\\{\"", "\\{\\\n\"");
			output = output.replaceAll("\\[\\{", "\\[\n\\{");
			output = output.replaceAll("\\},\\{", "\\},\n\\{");
			output = output.replaceAll("\\}\\]", "\\}\n\\]");
			output = output.replaceFirst("\\]\\}", "\\]\n\\}");
			pw.write(output);
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public HashMap<String, Map<String, ArrayList<Label>>> getLogs() {
		return logs;
	}

	public void setLogs(HashMap<String, Map<String, ArrayList<Label>>> logs) {
		this.logs = logs;
	}

}

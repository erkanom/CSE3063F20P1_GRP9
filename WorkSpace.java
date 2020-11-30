
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class WorkSpace {
	private Dataset dataset;
	private int id;
	private HashMap<String,Map<String, ArrayList<Label>>> logs;

	
	public WorkSpace(int id) {
		super();
		this.logs=new HashMap<>();
		this.id = id;
	}

	public void createFromJson(String jsonFileName) throws FileNotFoundException, IOException, ParseException {
		HashMap<String,Map<String, ArrayList<Label>>> tempLog = new HashMap<>();
		JSONParser jsonParser = new JSONParser();

		FileReader reader = new FileReader("./" + jsonFileName);

		// Read JSON file
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

			Label tempLabel=new Label((int) labelID, labelText);
			
			this.dataset.addLabel(tempLabel);
		

		}

		JSONArray array1 = (JSONArray) dataset.get("instances");

		for (int i = 0; i < array1.size(); i++) {

			JSONObject instances = (JSONObject) array1.get(i);

			long id = (long) instances.get("id");
			String instanceText = (String) instances.get("instance");

//			System.out.println("id:" + id);
//			System.out.println("instance:" + instanceText);
			Instance tempInstance=new Instance((int) id, instanceText, (int) maxlabel);
			this.dataset.addInstance(tempInstance);
			
			ArrayList<Label> tempLabel= new ArrayList<>();
			HashMap<String, ArrayList<Label>> tempMap = new HashMap<>();
			tempLog.put("instance"+id, tempMap);
			
					

			
		}
			this.logs=tempLog;
		int x = 0;
		
	}

	public void CreateJsonFromDataset() {

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

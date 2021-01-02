import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
public class EvenLabelingMechanism extends LabelMechanism implements BotSelectionHelper{
	@Override
	public void doLabeling(WorkSpace workSpace, User user, ArrayList<Instance> instances, boolean condition)
			throws IOException {
		Random rand = new Random();
		int InstanceIndex=rand.nextInt(instances.size());
		//Chosen instance 
		Instance Instance=instances.get(InstanceIndex);	
		Label[] labels = randomLabelId(workSpace, Instance);
		try {
			verifyAndLabel(user, labels, Instance, workSpace, condition);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Label[] randomLabelId(WorkSpace workSpace,Instance Instance) {
		int maxLabel=Instance.getMaxLabel();
		Random rand = new Random();
		maxLabel=rand.nextInt(maxLabel);
		ArrayList<Label> temp = new ArrayList<Label>();
		Label[] result = new Label[maxLabel+1];
		while(temp.size()<maxLabel+1) {
		int choosenLabelIndex=rand.nextInt(workSpace.getDataset().getLabels().size());
		Label choosenLabel=workSpace.getDataset().getLabels().get(choosenLabelIndex);
		if(temp.contains(choosenLabel)) {
			continue;
		}
		else if(choosenLabel.getLabelId()%2==0) {
		temp.add(choosenLabel);		
		}
		else continue;
		}
		for (int i = 0; i < temp.size(); i++) {
			result[i]=temp.get(i);
		}	
		return result;
	}
	public void verifyAndLabel(User user, Label[] choosenLabels, Instance instance, WorkSpace workspace,
			boolean condition) throws IOException {	
		LogWriter logHelper = LogWriter.getLog();
		//no labeled before
		if(!workspace.getLogs().containsKey("instance"+instance.getInstanceId())) {
			HashMap<String, LogHistory> temp = new HashMap<>();
			LogHistory name = new LogHistory();
			name.setInstance(instance);
			name.getUserList().add(user);
			Log tempLog= new Log();
			for (int i = 0; i < choosenLabels.length; i++) {
				tempLog.getLabels().add(choosenLabels[i]);	
			}
			ArrayList<Log>e = new ArrayList<>();
			e.add(tempLog);
			//index matter careful
			name.getHistory().add(e);
			Random rand = new Random(); 
			int finalIndex = rand.nextInt(choosenLabels.length);
			name.setFinalValue(choosenLabels[finalIndex]);
			temp.put("instance"+instance.getInstanceId(), name);
			workspace.getLogs().putAll(temp);
			logHelper.getLogOutput(name, user);
		}//Labeled before
		else {
			LogHistory tempHistory=workspace.getLogs().get("instance"+instance.getInstanceId());
			//labeled before by different user,not labeling again
          if(!tempHistory.getUserList().contains(user)&& condition==true) {
        	  tempHistory.getUserList().add(user);
              Log tempLog= new Log();
   			ArrayList<Log>e = new ArrayList<>();
   			e.add(tempLog);
              tempHistory.getHistory().add(e);
              int[] LabelCount= new int[workspace.getDataset().getLabels().size()];
     			for (int i = 0; i < tempHistory.getHistory().size(); i++) {
  				ArrayList<Log> logArray = tempHistory.getHistory().get(i);
  				for (int j = 0; j <logArray.size(); j++) {
  					Log LogTempForArray=logArray.get(j);
  					ArrayList<Label> tempLabelForAray=LogTempForArray.getLabels();
  					for (int k = 0; k < tempLabelForAray.size(); k++) {
  						LabelCount[tempLabelForAray.get(k).getLabelId()-1]++;
  					}
  				}
  			}
              int max= -1 ; 
              int label=-1;
              for (int i = 0; i < LabelCount.length; i++) {
  				if(LabelCount[i]>max) {
  					max=LabelCount[i];
  					label=i;
  				}
  			}
     			tempHistory.setFinalValue(workspace.getDataset().getLabels().get(label));
     			for (int i = 0; i < choosenLabels.length; i++) {
     				tempLog.getLabels().add(choosenLabels[i]);	
     			}
     			logHelper.getLogOutput(tempHistory, user);
          }else {
        	  //labeled before by same user,will label again
        	  if(tempHistory.getUserList().contains(user)&&condition==false) {
        		  int LogArrayIndex=-1;
        		  for (int i = 0; i < tempHistory.getUserList().size(); i++) {
					if(user.equals(tempHistory.getUserList().get(i)))
						LogArrayIndex=i;
				}
        		  ArrayList<Log> userLabels= tempHistory.getHistory().get(LogArrayIndex);
        		  Log tempLog = new Log();
        		  ArrayList<Label> c = new ArrayList<>();
        		  for (int i = 0; i < choosenLabels.length; i++) {
					c.add(choosenLabels[i]);
				}
        		  tempLog.getLabels().addAll(c); 
        		  userLabels.add(tempLog);
        		  logHelper.getLogOutput(tempHistory, user);
        	  }
			}
		}
	}
}

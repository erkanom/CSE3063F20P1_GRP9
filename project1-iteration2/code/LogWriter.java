import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//singleton class
public class LogWriter {
	private static LogWriter log = new LogWriter();
	private LogWriter() {
	}
	public static LogWriter getLog() {
		return log;
	}
	// for output operations
	public void getLogOutput(LogHistory history, User user) throws IOException {
		File file = new File("logs.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
		String logOutput = "LOG Instance Info : User " + user.getId() + " labeled instance "
				+ history.getInstance().getInstanceId() + " with " + history.getFinalValue().getLabelText() + " "
				+ history.getFinalValue().getLabelId() + " " + timeStamp;
		bw.write(logOutput + "\n");
		System.out.println(logOutput);
		bw.close();
	}
	// metric sysout
	public void printString(String str) {
		System.out.println(str);
	}
}

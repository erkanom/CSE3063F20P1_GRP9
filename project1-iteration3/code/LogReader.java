import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
public class LogReader {
	public LogReader() {
		super();
	}
	public long ReadTxtUser(User user) {
		File sourcefile = new File("logs.txt");
		if (!sourcefile.exists()) {
			System.out.println("Source file does not exist");
			System.exit(1);
		}
		// System.out.println(lines);
		// reader.close();
		// Create a Scanner for the file
		String firstTime = "";
		String lastTime = "";
		try {
			Scanner input = new Scanner(sourcefile);
			ArrayList<String> dates = new ArrayList<String>();
			int i = 0;
			int count = 0;
			while (input.hasNext()) {
				String s = input.nextLine();
				int a = s.indexOf("User") + 5;
				int b = s.indexOf("labeled") - 1;
				// s.substring(a,b);
				if (b > -1 && a > -1) {
					// System.out.println(s.substring(a,b));
					if (s.substring(a, b).equals(user.getId() + "")) {
						int timea = s.indexOf("2021");
						dates.add(s.substring(timea));
						count++;
						firstTime = dates.get(0);
						lastTime = dates.get(dates.size() - 1);
					}
				}
			}
			if (!dates.isEmpty())
				return DateAndTimeDifference(firstTime, lastTime);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public long DateAndTimeDifference(String firstDate, String lastDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		long totalDiffSeconds = -1;
		java.util.Date d1 = null;
		java.util.Date d2 = null;
		try {
			d1 = format.parse(firstDate);
			d2 = format.parse(lastDate);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			totalDiffSeconds = diff / 1000;
			return totalDiffSeconds;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalDiffSeconds;
	}
	public ArrayList<java.util.Date> GetAllDate(User user) {
		File sourcefile = new File("logs.txt");
		if (!sourcefile.exists()) {
			System.out.println("Source file does not exist");
			System.exit(1);
		}
		// System.out.println(lines);
		// reader.close();
		// Create a Scanner for the file
		String firstTime = "";
		String lastTime = "";
		try {
			Scanner input = new Scanner(sourcefile);
			ArrayList<java.util.Date> dates = new ArrayList<java.util.Date>();
			int i = 0;
			int count = 0;
			while (input.hasNext()) {
				String s = input.nextLine();
				int a = s.indexOf("User") + 5;
				int b = s.indexOf("labeled") - 1;
				// s.substring(a,b);
				if (b > -1 && a > -1) {
					// System.out.println(s.substring(a,b));
					if (s.substring(a, b).equals(user.getId() + "")) {
						int timea = s.indexOf("2021");
						SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						try {
							dates.add(format.parse(s.substring(timea)));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						count++;
					}
				}
			}
			if (!dates.isEmpty())
				return dates;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

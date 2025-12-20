package eu.ajg.fairdistribution;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVInputReader {
	
	protected File optionsFile;
	protected File priorityFile;
	
	protected Option[] options;
	protected List<Student> studentsWithPriorities;
	
	public CSVInputReader(String optionsFilePath, String priorityFilePath) throws IOException {
		optionsFile = new File(optionsFilePath);
		priorityFile = new File(priorityFilePath);
		
		if(!optionsFile.exists() || !optionsFile.canRead()) {
			throw new IOException("[Error] Cannot read file at given path: " + optionsFilePath);
		}
		
		if(!priorityFile.exists() || !priorityFile.canRead()) {
			throw new IOException("[Error] Cannot read file at given path: " + priorityFilePath);
		}
		
		readFiles();
	}
	
	private void readFiles() throws FileNotFoundException {
		// READ OPTIONS FILE
		Scanner s = new Scanner(new InputStreamReader(new BufferedInputStream(new FileInputStream(optionsFile)), Charset.forName("UTF-8")));
		
		// Count lines (substracting headline)
		int numLines = -1;
		while(s.hasNextLine()) {
			s.nextLine();
			numLines++;
		}
		s.close();
		
		s = new Scanner(new InputStreamReader(new BufferedInputStream(new FileInputStream(optionsFile)), Charset.forName("UTF-8")));
		
		// Prepare result array
		options = new Option[numLines];
		
		// Skip headline
		s.nextLine();
		
		// Read line by line
		while(s.hasNextLine()) {
			// Split line
			String line = s.nextLine();
			String[] lineParts = line.split(";");
			
			// Temporarily save line parts
			int optionId = Integer.parseInt(lineParts[0]);
			String optionName = lineParts[1];
			int optionCapacity = Integer.parseInt(lineParts[2]);
			
			// Throw exception if any duplicate id is found
			if(options[optionId] != null) {
				s.close();
				throw new IllegalArgumentException("[Error] At least two options share the same ID: " + optionId);
			}
			
			// Create object using line parts
			options[optionId] = new Option(optionName, optionCapacity);
		}
		s.close();
		
		
		// READ OPTIONS FILE
		s = new Scanner(new InputStreamReader(new BufferedInputStream(new FileInputStream(priorityFile)), Charset.forName("UTF-8")));
		
		// Prepare result list
		studentsWithPriorities = new ArrayList<>();
		
		// Skip headline
		s.nextLine();
		
		// Read line by line		
		while(s.hasNextLine()) {
			String line = s.nextLine();
			String[] lineParts = line.split(";");
			
			String studentName = lineParts[0];
			int optionId0 = Integer.parseInt(lineParts[1]);
			int optionId1 = Integer.parseInt(lineParts[2]);
			int optionId2 = Integer.parseInt(lineParts[3]);
			
			studentsWithPriorities.add(new Student(studentName, optionId0, optionId1, optionId2));
		}
		s.close();
	}
	
	public Option[] getOptions() {
		return options;
	}
	
	public List<Student> getStudentsWithPriorities() {
		return studentsWithPriorities;
	}

}

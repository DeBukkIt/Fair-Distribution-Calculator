package eu.ajg.fairdistribution;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class ResultWriter {

	protected File outputFile;
	
	public ResultWriter(String outputFilePath) throws IOException {
		outputFile = new File(outputFilePath);
		
		if(outputFile.isDirectory()) {
			throw new IOException("[Error] Given path leads to a directory, not a file: " + outputFilePath);
		}
		
		outputFile.createNewFile();
		
		if(!outputFile.canWrite()) {
			throw new IOException("[Error] Cannot access file for writing at given path: " + outputFilePath);
		}
	}
	
	public void write(Map<Integer, List<String>> resultMap, Option[] options, List<Student> studentsWithPriorities) throws IOException {
		// Prepare result string
		StringBuilder sb = new StringBuilder();
		
		// For every option...
		for(Integer optionId : resultMap.keySet()) {
			
			// Append a headline to the result string
			sb.append("Option #" + optionId + ": '" + options[optionId].getName() + "' (max. " + options[optionId].getCapacity() + "):\n");
			
			// For every student assigned to that option
			for(int i = 0; i < resultMap.get(optionId).size(); i++) {
				String studentName = resultMap.get(optionId).get(i);
				
				// Add number to result string
				sb.append((i+1) + "\t");
				// Add name
				sb.append(studentName + "\t\t");
				// Check priority matching, add it
				PRIO_MATCHING_LOOP:
				for(Student student : studentsWithPriorities) {
					if(student.getName().equals(studentName)) {
						sb.append(student.getWishRank(optionId));
						break PRIO_MATCHING_LOOP;
					}
				}
				
				// Add line break
				sb.append("\n");
			}
			
			// Add another line break between to options
			sb.append("\n");
			
		}
		
		OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)), Charset.forName("UTF-8"));
		osw.write(sb.toString());
		osw.flush();
		osw.close();
		
		System.out.println("\nResult written to: " + outputFile.getAbsolutePath());
	}
	
}

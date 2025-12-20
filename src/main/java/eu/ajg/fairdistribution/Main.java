package eu.ajg.fairdistribution;

import java.util.List;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) throws Exception {
		CSVInputReader inputReader = new CSVInputReader(args[0], args[1]);
		
		FairDistribution fd = new FairDistribution();
		Map<Integer, List<String>> result = fd.distribute(inputReader.getOptions(), inputReader.getStudentsWithPriorities());
		
		ResultWriter resultWriter = new ResultWriter("C:\\Users\\VdF-Steinfurt-5\\Downloads\\result.txt");
		resultWriter.write(result, inputReader.getOptions(), inputReader.getStudentsWithPriorities());
	}

}

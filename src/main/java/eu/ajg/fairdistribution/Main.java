package eu.ajg.fairdistribution;

import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length >= 1 && args[0].equalsIgnoreCase("gui")) {
			
			SwingUtilities.invokeLater(() -> new GuiFairDistribution().setVisible(true));
			
		} else if(args.length >= 2) {

			CSVInputReader inputReader = new CSVInputReader(args[0], args[1]);

			FairDistribution fd = new FairDistribution();
			Map<Integer, List<String>> result = fd.distribute(
						inputReader.getOptions(),
						inputReader.getStudentsWithPriorities()
					);

			ResultWriter resultWriter = new ResultWriter(args[2]);
			resultWriter.write(result, inputReader.getOptions(), inputReader.getStudentsWithPriorities());

		} else {
			
			System.out.println("Usage: (gui | <optionsInputFile> <wishesInputFile> <outputFile>)");
			
		}
	}

}

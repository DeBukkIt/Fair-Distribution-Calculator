package eu.ajg.fairdistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class FairDistribution {

	public Map<Integer, List<String>> distribute(Option[] options, List<Student> students) {
		long startTime = System.nanoTime();
		
		// ---------- Model ----------
		Model solverModel = new Model("FairDistribution");

		// trip assignment per student
		IntVar[] trip = new IntVar[students.size()];
		for (int i = 0; i < students.size(); i++) {
			trip[i] = solverModel.intVar("trip_" + i, 0, options.length - 1);
		}

		// capacity constraints
		for (int t = 0; t < options.length; t++) {
			solverModel.count(t, trip, solverModel.intVar(0, options[t].getCapacity())).post();
		}

		// wish satisfaction variables
		BoolVar[] first = new BoolVar[students.size()];
		BoolVar[] second = new BoolVar[students.size()];
		BoolVar[] third = new BoolVar[students.size()];

		for (int i = 0; i < students.size(); i++) {
			Student s = students.get(i);

			first[i] = solverModel.boolVar("first_" + i);
			second[i] = solverModel.boolVar("second_" + i);
			third[i] = solverModel.boolVar("third_" + i);

			solverModel.arithm(trip[i], "=", s.getOptionId(0)).reifyWith(first[i]);
			solverModel.arithm(trip[i], "=", s.getOptionId(1)).reifyWith(second[i]);
			solverModel.arithm(trip[i], "=", s.getOptionId(2)).reifyWith(third[i]);
		}

		IntVar sumFirst = solverModel.intVar("sumFirst", 0, students.size());
		IntVar sumSecond = solverModel.intVar("sumSecond", 0, students.size());
		IntVar sumThird = solverModel.intVar("sumThird", 0, students.size());

		solverModel.sum(first, "=", sumFirst).post();
		solverModel.sum(second, "=", sumSecond).post();
		solverModel.sum(third, "=", sumThird).post();

		Solver solver = solverModel.getSolver();

		// ---------- Lexicographic optimization ----------
		Solution s1 = solver.findOptimalSolution(sumFirst, true);
		int bestFirst = s1.getIntVal(sumFirst);
		solverModel.arithm(sumFirst, "=", bestFirst).post();
		solver.reset();

		Solution s2 = solver.findOptimalSolution(sumSecond, true);
		int bestSecond = s2.getIntVal(sumSecond);
		solverModel.arithm(sumSecond, "=", bestSecond).post();
		solver.reset();

		Solution s3 = solver.findOptimalSolution(sumThird, true);

		long duration = System.nanoTime() - startTime;
		
		// ---------- Output ----------
		System.out.println("=== Optimal allocation ===");
		System.out.println("Time needed: " + (int) (duration / 1E6) + " ms\n");
		System.out.println("First wishes : " + s3.getIntVal(sumFirst));
		System.out.println("Second wishes: " + s3.getIntVal(sumSecond));
		System.out.println("Third wishes : " + s3.getIntVal(sumThird));
		System.out.println();

		Map<Integer, List<String>> tripToStudents = new HashMap<>();
		for (int t = 0; t < options.length; t++) {
			tripToStudents.put(t, new ArrayList<>());
		}

		for (int i = 0; i < students.size(); i++) {
			int assigned = s3.getIntVal(trip[i]);
			tripToStudents.get(assigned).add(students.get(i).name);
		}

		for (int t = 0; t < options.length; t++) {
			System.out.println(options[t].getName() + " (" + tripToStudents.get(t).size() + "/" + options[t].getCapacity() + ")");
			for (String name : tripToStudents.get(t)) {
				System.out.println("  - " + name);
			}
		}
		
		return tripToStudents;
	}
}

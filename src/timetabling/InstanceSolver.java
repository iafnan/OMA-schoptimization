package timetabling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.coinor.opents.BestEverAspirationCriteria;
import org.coinor.opents.MultiThreadedTabuSearch;
import org.coinor.opents.SimpleTabuList;
import org.coinor.opents.TabuList;
import org.jamesframework.core.problems.GenericProblem;
import org.jamesframework.core.problems.sol.RandomSolutionGenerator;
import org.jamesframework.core.search.neigh.Neighbourhood;

import com.google.common.graph.ImmutableValueGraph;

import timetabling.ettp.EttpAuxiliaryObjective;
import timetabling.ettp.EttpData;
import timetabling.ettp.EttpHardConstraint;
import timetabling.ettp.EttpObjective;
import timetabling.ettp.EttpSolution;
import timetabling.ettp.EttpVNS;
import timetabling.ettp.MaxElapsedTime;
import timetabling.ettp.TSMoveManager;
import timetabling.ettp.neigh.EttpKempeNeighbourhood;
import timetabling.ettp.neigh.EttpMoveConflictsNeighbourhood;
import timetabling.ettp.neigh.EttpMoveNeighbourhood;
import timetabling.ettp.neigh.EttpMoveTimeslotsNeighbourhood;
import timetabling.ettp.neigh.EttpSwapExamsNeighbourhood;
import timetabling.ettp.neigh.EttpSwapTimeslotsNeighbourhood;

public class InstanceSolver {

	public EttpSolution solve(EttpData data, long timelimit) {
		Set<ImmutableValueGraph<Integer, Integer>> partitions = GraphUtilities.partitionGraph(data.getConflictGraph());

		ExecutorService executor = Executors.newFixedThreadPool(partitions.size());

		List<Callable<EttpSolution>> callables = new ArrayList<>();
		for (ImmutableValueGraph<Integer, Integer> partition : partitions) {
			EttpData pdata = new EttpData(partition, data.getNSlots(), data.getNStudents());
			EttpObjective obj = new EttpObjective();
			RandomSolutionGenerator<EttpSolution, EttpData> rsg = InstanceSolver.getRandomFeasibleSolutionGenarator();
			GenericProblem<EttpSolution, EttpData> problem = new GenericProblem<>(pdata, obj, rsg);
			problem.addMandatoryConstraint(new EttpHardConstraint());

			callables.add(InstanceSolver.solvePartition(problem, timelimit - 1000));
		}

		try {
			List<Future<EttpSolution>> futures = executor.invokeAll(callables);
			executor.shutdown();

			List<EttpSolution> solutions = futures.stream().map(f -> {
				try {
					return f.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					return null;
				}
			}).collect(Collectors.toList());

			EttpSolution ret = new EttpSolution(data);
			for (EttpSolution s : solutions) {
				ret.merge(s);
			}
			return ret;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Callable<EttpSolution> solvePartition(GenericProblem<EttpSolution, EttpData> problem,
			long timelimit) {
		return () -> {
			List<Neighbourhood<EttpSolution>> neighs = new ArrayList<>();
			neighs.add(new EttpMoveNeighbourhood(1, problem.getData()));
			neighs.add(new EttpSwapExamsNeighbourhood(problem.getData()));
			neighs.add(new EttpMoveNeighbourhood(2, problem.getData()));
			neighs.add(new EttpMoveNeighbourhood(3, problem.getData()));
			neighs.add(new EttpMoveNeighbourhood(4, problem.getData()));
			neighs.add(new EttpMoveNeighbourhood(5, problem.getData()));
			neighs.add(new EttpMoveTimeslotsNeighbourhood(problem.getData()));
			neighs.add(new EttpSwapTimeslotsNeighbourhood(problem.getData()));
			neighs.add(new EttpKempeNeighbourhood(problem.getData()));

			EttpVNS<EttpSolution> vns = new EttpVNS<>(problem, neighs);
			vns.addStopCriterion(
					new MaxElapsedTime(System.currentTimeMillis(), timelimit - 1000, TimeUnit.MILLISECONDS));
			vns.start();
			EttpSolution best = vns.getBestSolution();
			return best;
		};
	}

	static RandomSolutionGenerator<EttpSolution, EttpData> getRandomFeasibleSolutionGenarator() {
		return (rnd, data) -> {
			RandomSolutionGenerator<EttpSolution, EttpData> rsg = InstanceSolver.getRandomSolutionGenarator();

			EttpSolution bsol;
			do {
				EttpAuxiliaryObjective obj_fun = new EttpAuxiliaryObjective();
				EttpSolution initial_solution = rsg.create(rnd, data);
				TSMoveManager move_manager = new TSMoveManager(new EttpMoveConflictsNeighbourhood(data));
				TabuList tabu_list = new SimpleTabuList(7);

				MultiThreadedTabuSearch tabu_search = new MultiThreadedTabuSearch(initial_solution, move_manager,
						obj_fun, tabu_list, new BestEverAspirationCriteria(), false);
				tabu_search.addTabuSearchListener(move_manager);
				tabu_search.setIterationsToGo(1000);
				tabu_search.startSolving();
				tabu_search.waitToFinish();
				bsol = (EttpSolution) tabu_search.getBestSolution();
			} while (!bsol.isFeasible());

			return bsol;
		};
	}

	static RandomSolutionGenerator<EttpSolution, EttpData> getRandomSolutionGenarator() {
		return (rnd, data) -> {
			return InstanceSolver.greedyAllocation(rnd, data);
		};
	}

	private static EttpSolution greedyAllocation(Random rnd, EttpData data) {
		EttpSolution sol = new EttpSolution(data);

		List<Integer> exams = new ArrayList<>(data.exams());
		Collections.sort(exams, (a, b) -> Integer.compare(data.edegree(b), data.edegree(a)));

		List<Integer> tslots = new ArrayList<>();
		for (int i = 0; i < data.getNSlots(); ++i)
			tslots.add(i);
		Collections.shuffle(tslots, rnd);

		// Assign the first exam to first timeslot
		sol.assignExamToTimeslot(exams.get(0), tslots.get(0));

		// A temporary array to store the available timeslots. True
		// value of available[ts] would mean that the timeslot ts is
		// assigned to one of its adjacent vertices
		boolean available[] = new boolean[data.getNSlots()];
		Arrays.fill(available, true);

		// Assign timeslots to remaining vertices
		for (Integer e : exams.subList(1, exams.size())) {
			// Process all adjacent vertices and flag their timeslot
			// as unavailable
			for (Integer ae : data.aexams(e)) {
				if (sol.isExamScheduled(ae)) {
					available[sol.getExamScheduling(ae)] = false;
				}
			}

			// Find the first available timeslot
			int ts = 0;
			for (Integer t : tslots) {
				if (available[t] == true) {
					ts = t;
					break;
				}
			}

			// No available timeslot! This means that no feasible solution
			// can be built using this greedy algorithm
			if (ts >= data.getNSlots()) {
				ts = rnd.nextInt(data.getNSlots());
			}

			sol.assignExamToTimeslot(e, ts);

			// Reset the values back to true for the next iteration
			for (Integer ae : data.aexams(e)) {
				if (sol.isExamScheduled(ae)) {
					available[sol.getExamScheduling(ae)] = true;
				}
			}
		}

		return sol;
	}
}

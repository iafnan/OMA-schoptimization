package timetabling.ettp.neigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

import com.google.common.graph.EndpointPair;

import timetabling.ettp.EttpData;
import timetabling.ettp.EttpSolution;

public class EttpMoveConflictsNeighbourhood implements Neighbourhood<EttpSolution> {
	private EttpData data;

	public EttpMoveConflictsNeighbourhood(EttpData data) {
		this.data = data;
	}

	@Override
	public List<? extends Move<? super EttpSolution>> getAllMoves(EttpSolution sol) {
		List<EttpMove> moves = new ArrayList<>();
		for (EndpointPair<Integer> c : sol.getConflicts()) {
			for (int t = 0; t < this.data.getNSlots(); ++t)
				moves.add(new EttpMove(c.nodeU(), t));
			for (int t = 0; t < this.data.getNSlots(); ++t)
				moves.add(new EttpMove(c.nodeV(), t));
		}
		return moves;
	}

	@Override
	public Move<? super EttpSolution> getRandomMove(EttpSolution sol, Random rnd) {
		EndpointPair<Integer> conflict = this.getRandomConflict(sol, rnd);
		Integer exam = rnd.nextBoolean() ? conflict.nodeU() : conflict.nodeV();
		Integer new_timeslot = rnd.nextInt(data.getNSlots());
		return new EttpMove(exam, new_timeslot);
	}

	private EndpointPair<Integer> getRandomConflict(EttpSolution sol, Random rnd) {
		int i = rnd.nextInt(sol.getConflicts().size());
		return (EndpointPair<Integer>) sol.getConflicts().toArray()[i];
	}
}

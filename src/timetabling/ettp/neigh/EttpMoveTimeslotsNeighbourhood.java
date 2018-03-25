package timetabling.ettp.neigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

import timetabling.ettp.EttpData;
import timetabling.ettp.EttpSolution;

public class EttpMoveTimeslotsNeighbourhood implements Neighbourhood<EttpSolution> {
	private EttpData data;

	public EttpMoveTimeslotsNeighbourhood(EttpData data) {
		this.data = data;
	}

	@Override
	public List<? extends Move<? super EttpSolution>> getAllMoves(EttpSolution sol) {
		List<Move<? super EttpSolution>> ret = new ArrayList<>();
		for (int from = 0; from < this.data.getNSlots(); ++from) {
			for (int to = 0; to < this.data.getNSlots(); ++to) {
				if (from != to) {
					ret.add(this.getMove(sol, from, to));
				}
			}
		}
		return ret;
	}

	@Override
	public Move<? super EttpSolution> getRandomMove(EttpSolution sol, Random rnd) {
		Integer from = rnd.nextInt(this.data.getNSlots());
		Integer to;
		do {
			to = rnd.nextInt(this.data.getNSlots());
		} while (from == to);

		return this.getMove(sol, from, to);
	}

	private Move<? super EttpSolution> getMove(EttpSolution sol, Integer from, Integer to) {
		EttpMove ret = new EttpMove();
		for (Integer e : sol.getExamsForTimeslot(from))
			ret.addMove(e, to);

		for (int t = to; t % this.data.getNSlots() < from; ++t) {
			for (Integer e : sol.getExamsForTimeslot(t % this.data.getNSlots()))
				ret.addMove(e, (t + 1) % this.data.getNSlots());
		}

		return ret;
	}
}
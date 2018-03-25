package timetabling.ettp.neigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

import timetabling.ettp.EttpData;
import timetabling.ettp.EttpSolution;

public class EttpSwapTimeslotsNeighbourhood implements Neighbourhood<EttpSolution> {
	private EttpData data;

	public EttpSwapTimeslotsNeighbourhood(EttpData data) {
		this.data = data;
	}

	@Override
	public List<? extends Move<? super EttpSolution>> getAllMoves(EttpSolution sol) {
		List<Move<? super EttpSolution>> ret = new ArrayList<>();

		for (int t1 = 0; t1 < this.data.getNSlots() - 1; ++t1) {
			for (int t2 = t1 + 1; t2 < this.data.getNSlots(); ++t2) {
				ret.add(this.getSwapMove(sol, t1, t2));
			}
		}

		return ret;
	}

	@Override
	public Move<? super EttpSolution> getRandomMove(EttpSolution sol, Random rnd) {
		Integer t1 = rnd.nextInt(this.data.getNSlots());
		Integer t2;
		do {
			t2 = rnd.nextInt(this.data.getNSlots());
		} while (t2 == t1);

		return this.getSwapMove(sol, t1, t2);
	}

	private Move<? super EttpSolution> getSwapMove(EttpSolution sol, Integer t1, Integer t2) {
		EttpMove ret = new EttpMove();
		for (Integer e : sol.getExamsForTimeslot(t1))
			ret.addMove(e, t2);
		for (Integer e : sol.getExamsForTimeslot(t2))
			ret.addMove(e, t1);
		return ret;
	}

}

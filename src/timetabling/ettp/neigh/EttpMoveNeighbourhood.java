package timetabling.ettp.neigh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

import com.google.common.collect.ImmutableList;

import timetabling.ettp.EttpData;
import timetabling.ettp.EttpSolution;

public class EttpMoveNeighbourhood implements Neighbourhood<EttpSolution> {
	private Integer N;
	private EttpData data;

	public EttpMoveNeighbourhood(Integer N, EttpData data) {
		this.N = N;
		this.data = data;
	}

	@Override
	public List<? extends Move<? super EttpSolution>> getAllMoves(EttpSolution sol) {
		if (this.N != 1) {
			return null;
		}

		List<Move<? super EttpSolution>> ret = new ArrayList<>();
		for (Integer e : this.data.exams()) {
			for (Integer t : this.getFeasibleSlots(sol, e)) {
				ret.add(new EttpMove(e, t));
			}
		}

		return ret;
	}

	@Override
	public Move<? super EttpSolution> getRandomMove(EttpSolution sol, Random rnd) {
		int m = 0;

		EttpMove ret = new EttpMove();
		ImmutableList<Integer> exams = this.data.exams();
		for (Integer i : this.getShuffledIndexes(0, exams.size(), rnd)) {
			if (ret.getN() >= this.N)
				break;

			Integer e = exams.get(i);
			List<Integer> fs = this.getFeasibleSlots(sol, e);

			if (!fs.isEmpty()) {
				Integer si = rnd.nextInt(fs.size());
				ret.addMove(e, fs.get(si));
			}
		}
		return ret;
	}

	private List<Integer> getFeasibleSlots(EttpSolution sol, Integer e) {
		List<Integer> ret = new ArrayList<>(this.data.getNSlots());
		for (int i = 0; i < this.data.getNSlots(); ++i)
			ret.add(i);
		for (Integer ae : this.data.aexams(e))
			ret.remove(sol.getExamScheduling(ae));
		return ret;
	}

	private List<Integer> getShuffledIndexes(Integer fromInclusive, Integer toExclusive, Random rnd) {
		List<Integer> ret = new ArrayList<>();
		for (int i = fromInclusive; i < toExclusive; ++i)
			ret.add(i);
		Collections.shuffle(ret, rnd);
		return ret;
	}
}

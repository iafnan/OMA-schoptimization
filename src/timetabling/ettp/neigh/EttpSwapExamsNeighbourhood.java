package timetabling.ettp.neigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

import timetabling.ettp.EttpData;
import timetabling.ettp.EttpSolution;

public class EttpSwapExamsNeighbourhood implements Neighbourhood<EttpSolution> {
	private EttpData data;

	public EttpSwapExamsNeighbourhood(EttpData data) {
		this.data = data;
	}

	@Override
	public List<? extends Move<? super EttpSolution>> getAllMoves(EttpSolution sol) {
		List<Move<? super EttpSolution>> ret = new ArrayList<>();
		for (int i1 = 0; i1 < this.data.getNExams() - 1; ++i1) {
			for (int i2 = i1 + 1; i2 < this.data.getNExams(); ++i2) {
				int e1 = this.data.exams().get(i1);
				int e2 = this.data.exams().get(i2);
				ret.add(this.getSwapMove(sol, e1, e2));
			}
		}
		return ret;
	}

	@Override
	public Move<? super EttpSolution> getRandomMove(EttpSolution sol, Random rnd) {
		Integer i1 = rnd.nextInt(this.data.getNExams());
		Integer i2;
		do {
			i2 = rnd.nextInt(this.data.getNExams());
		} while (i2 == i1);

		int e1 = this.data.exams().get(i1);
		int e2 = this.data.exams().get(i2);
		return this.getSwapMove(sol, e1, e2);
	}

	private Move<? super EttpSolution> getSwapMove(EttpSolution sol, Integer e1, Integer e2) {
		EttpMove ret = new EttpMove();
		ret.addMove(e1, sol.getExamScheduling(e2));
		ret.addMove(e2, sol.getExamScheduling(e1));
		return ret;
	}
}

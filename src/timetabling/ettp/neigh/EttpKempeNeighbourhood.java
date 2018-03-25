package timetabling.ettp.neigh;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;

import com.google.common.collect.ImmutableList;

import timetabling.ettp.EttpData;
import timetabling.ettp.EttpSolution;

public class EttpKempeNeighbourhood implements Neighbourhood<EttpSolution> {
	private EttpData data;

	public EttpKempeNeighbourhood(EttpData data) {
		this.data = data;
	}

	@Override
	public List<? extends Move<? super EttpSolution>> getAllMoves(EttpSolution sol) {
		List<Move<? super EttpSolution>> ret = new ArrayList<>();
		for (Integer exam : this.data.exams()) {
			for (Integer adjacent_exam : this.data.aexams(exam)) {
				ret.add(this.getMove(exam, sol.getExamScheduling(adjacent_exam), sol));
			}
		}
		return ret;
	}

	@Override
	public Move<? super EttpSolution> getRandomMove(EttpSolution sol, Random rnd) {
		Integer exam1 = this.data.exams().get(rnd.nextInt(this.data.getNExams()));
		ImmutableList<Integer> adjacent_exams = this.data.aexams(exam1);
		Integer exam2 = adjacent_exams.get(rnd.nextInt(adjacent_exams.size()));
		return getMove(exam1, sol.getExamScheduling(exam2), sol);
	}

	private Move<? super EttpSolution> getMove(Integer v, Integer j, EttpSolution sol) {
		EttpMove ret = new EttpMove();
		Integer i = sol.getExamScheduling(v);

		Set<Integer> chain = new LinkedHashSet<>();
		Queue<Integer> queued_exams = new ArrayDeque<>();
		chain.add(v);
		queued_exams.add(v);

		while (!queued_exams.isEmpty()) {
			Integer current_exam = queued_exams.remove();
			for (Integer adjacent_exam : this.data.aexams(current_exam)) {
				Integer c = sol.getExamScheduling(adjacent_exam);
				if (c != i && c != j)
					continue;
				if (chain.add(adjacent_exam)) {
					queued_exams.add(adjacent_exam);
				}
			}
		}

		for (Integer exam : chain) {
			Integer new_slot = sol.getExamScheduling(exam) == i ? j : i;
			ret.addMove(exam, new_slot);
		}

		return ret;
	}
}

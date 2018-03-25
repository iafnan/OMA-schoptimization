package timetabling.ettp;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;

public class EttpObjective implements Objective<EttpSolution, EttpData> {
	@Override
	public Evaluation evaluate(EttpSolution sol, EttpData data) {
		return new SimpleEvaluation(sol.getPenalty());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Evaluation evaluate(Move move, EttpSolution curSolution, Evaluation curEvaluation, EttpData data) {
		move.apply(curSolution);
		Double temp = curSolution.getPenalty();
		move.undo(curSolution);
		return new SimpleEvaluation(temp);
	}

	@Override
	public boolean isMinimizing() {
		return true;
	}

}

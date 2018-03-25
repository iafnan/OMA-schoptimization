package timetabling.ettp;

import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.search.neigh.Move;

public class EttpHardConstraint implements Constraint<EttpSolution, EttpData> {

	@Override
	public Validation validate(EttpSolution sol, EttpData data) {
		return new SimpleValidation(sol.isFeasible());
	}

	@Override
	public Validation validate(Move move, EttpSolution curSolution, Validation curValidation, EttpData data) {
		move.apply(curSolution);
		boolean ret = curSolution.isFeasible();
		move.undo(curSolution);
		return new SimpleValidation(ret);
	}

}

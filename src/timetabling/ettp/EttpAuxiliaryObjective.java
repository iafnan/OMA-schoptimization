package timetabling.ettp;

import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import timetabling.ettp.neigh.EttpMove;

public class EttpAuxiliaryObjective implements ObjectiveFunction {
	@Override
	public double[] evaluate(Solution sol, Move move) {
		EttpSolution tc_sol = (EttpSolution) sol;
		EttpMove tc_move = (EttpMove) move;

		if (tc_move == null) {
			return this.evaluateAbsolutely(tc_sol);
		} else {
			return this.evaluateIncrementally(tc_sol, tc_move);
		}
	}

	private double[] evaluateAbsolutely(EttpSolution sol) {

		return new double[] { sol.getConflicts().size() };
	}

	private double[] evaluateIncrementally(EttpSolution sol, EttpMove move) {
		move.operateOn(sol);
		double[] ret = evaluateAbsolutely(sol);
		move.undo(sol);
		return ret;
	}
}

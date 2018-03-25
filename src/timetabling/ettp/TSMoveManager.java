package timetabling.ettp;

import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.Move;
import org.coinor.opents.MoveManager;
import org.coinor.opents.Solution;
import org.coinor.opents.TabuSearchEvent;
import org.coinor.opents.TabuSearchListener;
import org.jamesframework.core.search.neigh.Neighbourhood;

public class TSMoveManager implements MoveManager, TabuSearchListener {

	private Neighbourhood<EttpSolution> neigh;

	public TSMoveManager(Neighbourhood<EttpSolution> neigh) {
		this.neigh = neigh;
	}

	@Override
	public Move[] getAllMoves(Solution sol) {
		List<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < 100; ++i) {
			moves.add((Move) neigh.getRandomMove((EttpSolution) sol));
		}

		return moves.toArray(new Move[moves.size()]);
	}

	@Override
	public void improvingMoveMade(TabuSearchEvent ev) {
	}

	@Override
	public void newBestSolutionFound(TabuSearchEvent ev) {
		EttpSolution solution = (EttpSolution) ev.getTabuSearch().getBestSolution();
		if (solution.isFeasible())
			ev.getTabuSearch().setIterationsToGo(0);
	}

	@Override
	public void newCurrentSolutionFound(TabuSearchEvent ev) {
	}

	@Override
	public void noChangeInValueMoveMade(TabuSearchEvent ev) {
	}

	@Override
	public void tabuSearchStarted(TabuSearchEvent ev) {
		EttpSolution solution = (EttpSolution) ev.getTabuSearch().getBestSolution();
		if (solution.isFeasible())
			ev.getTabuSearch().setIterationsToGo(0);
	}

	@Override
	public void tabuSearchStopped(TabuSearchEvent ev) {
	}

	@Override
	public void unimprovingMoveMade(TabuSearchEvent ev) {
	}
}

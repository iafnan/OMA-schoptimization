/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 * Copyright 2017 Andrea Azzarone
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package timetabling.ettp;

import java.util.List;

import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.sol.Solution;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.MultiNeighbourhoodSearch;
import org.jamesframework.core.search.algo.SteepestDescent;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.status.SearchStatus;

public class EttpVNS<SolutionType extends Solution> extends MultiNeighbourhoodSearch<SolutionType> {

	// index of currently used neighbourhood
	private int k;

	public EttpVNS(Problem<SolutionType> problem, List<? extends Neighbourhood<? super SolutionType>> neighs) {
		this(null, problem, neighs);
	}

	public EttpVNS(String name, Problem<SolutionType> problem,
			List<? extends Neighbourhood<? super SolutionType>> neighs) {
		super(name != null ? name : "ETTPVariableNeighbourhoodSearch", problem, neighs);
		// start with 0th neighbourhood
		k = 0;
	}

	@Override
	protected void searchStep() {

		if (getCurrentSolutionEvaluation().getValue() <= 0.0) {
			stop();
			return;
		}

		// cyclically reset s to zero if no more shaking neighbourhoods are available
		if (k >= getNeighbourhoods().size()) {
			k = 0;
		}

		// create copy of current solution to shake and modify by applying local search
		// procedure
		SolutionType shakedSolution = Solution.checkedCopy(getCurrentSolution());

		// 1) SHAKING

		// get random move from current shaking neighbourhood
		Move<? super SolutionType> shakeMove = getNeighbourhoods().get(k).getRandomMove(shakedSolution, getRandom());
		// shake (only if a move was obtained)
		Evaluation shakedEval = null;
		Validation shakedVal = null;
		if (shakeMove != null) {
			shakedEval = evaluate(shakeMove);
			shakedVal = validate(shakeMove);
			shakeMove.apply(shakedSolution);
		}

		if (shakedVal == null || !shakedVal.passed()) {
			incNumRejectedMoves(1);
			k++;
			return;
		}

		// 2) LOCAL SEARCH

		// create instance of local search algorithm
		LocalSearch<SolutionType> localSearch = new SteepestDescent<>(getProblem(), getNeighbourhoods().get(0));
		// set initial solution to be modified
		localSearch.setCurrentSolution(shakedSolution, shakedEval, shakedVal);
		// interrupt local search algorithm when main VNS search wants to terminate
		localSearch.addStopCriterion(_search -> getStatus() == SearchStatus.TERMINATING);
		// run local search
		localSearch.start();
		// dispose local search when completed
		localSearch.dispose();

		// 3) ACCEPTANCE

		SolutionType lsBestSolution = localSearch.getBestSolution();
		Evaluation lsBestSolutionEvaluation = localSearch.getBestSolutionEvaluation();
		Validation lsBestSolutionValidation = localSearch.getBestSolutionValidation();
		// check improvement
		if (lsBestSolution != null && lsBestSolutionValidation.passed() // should always be true but it doesn't hurt to
																		// check
				&& computeDelta(lsBestSolutionEvaluation, getCurrentSolutionEvaluation()) > 0) {
			// improvement: increase number of accepted moves
			incNumAcceptedMoves(1);
			// update current and best solution
			updateCurrentAndBestSolution(lsBestSolution, lsBestSolutionEvaluation, lsBestSolutionValidation);
		} else {
			// no improvement: stick with current solution, adopt next shaking neighbourhood
			incNumRejectedMoves(1);
			k++;
		}

	}
}

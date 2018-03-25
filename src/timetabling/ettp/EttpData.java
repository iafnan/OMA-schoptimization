package timetabling.ettp;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableValueGraph;

public class EttpData {
	private ImmutableValueGraph<Integer, Integer> cgraph;
	private ImmutableList<Integer> exams;
	private ImmutableList<EndpointPair<Integer>> cexams;
	private Map<Integer, ImmutableList<Integer>> adj_cache = new HashMap<>();
	private Integer nslots;
	private Integer nstudents;

	public EttpData(ImmutableValueGraph<Integer, Integer> cgraph, Integer nslots, Integer nstudents) {
		this.cgraph = cgraph;
		this.exams = ImmutableList.copyOf(this.cgraph.nodes());
		this.cexams = ImmutableList.copyOf(this.cgraph.edges());
		this.nslots = nslots;
		this.nstudents = nstudents;
	}

	public ImmutableValueGraph<Integer, Integer> getConflictGraph() {
		return this.cgraph;
	}

	public Integer getNSlots() {
		return this.nslots;
	}

	public Integer getNStudents() {
		return this.nstudents;
	}

	public Integer getNExams() {
		return this.exams.size();
	}

	/**
	 * Get an ImmutableSet of exams
	 *
	 * @return an ImmutableSet of exams
	 */
	public ImmutableList<Integer> exams() {
		return this.exams;
	}

	/**
	 * Get an ImmutableSet of conflicting exams, that's to say exams that have at
	 * least student in common.
	 *
	 * @return an ImmutableSet of conflicting exams
	 */
	public ImmutableList<EndpointPair<Integer>> cexams() {
		return this.cexams;
	}

	/**
	 * Get an ImmutableSet of adjacent exams to exam
	 *
	 * @param exam
	 *            exam of which adjacent exams we are interested
	 *
	 * @return an ImmutableSet of adjacent exams
	 */
	public ImmutableList<Integer> aexams(Integer exam) {
		if (!this.adj_cache.containsKey(exam))
			this.adj_cache.put(exam, ImmutableList.copyOf(this.cgraph.adjacentNodes(exam)));
		return this.adj_cache.get(exam);
	}

	/**
	 * Get the number of adjacent exams to exam
	 *
	 * @param exam
	 *            exam of which degree we are interested
	 *
	 * @return the number of adjacent exams to exam
	 */
	public Integer edegree(Integer exam) {
		return this.cgraph.degree(exam);
	}

	public Integer getStudentsInCommon(Integer e1, Integer e2) {
		return this.cgraph.edgeValueOrDefault(e1, e2, 0);
	}

}

package timetabling.ettp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jamesframework.core.problems.sol.Solution;

import com.google.common.graph.EndpointPair;

public class EttpSolution extends Solution implements org.coinor.opents.Solution {
	private EttpData data;
	public Map<Integer, Integer> e2t;
	private Set<EndpointPair<Integer>> conflicts;
	private Double penalty;

	public EttpSolution(EttpData data) {
		this.data = data;
		this.e2t = new LinkedHashMap<>();
		this.conflicts = new LinkedHashSet<>();
		this.penalty = 0.0;
	}

	public void assignExamToTimeslot(Integer exam, Integer timeslot) {
		if (this.isExamScheduled(exam)) {
			this.data.aexams(exam).forEach(ae -> {
				if (!this.isExamScheduled(ae))
					return;

				Integer old_distance = Math.abs(this.getExamScheduling(ae) - this.getExamScheduling(exam));
				if (old_distance == 0)
					this.conflicts.remove(EndpointPair.unordered(exam, ae));
				else if (old_distance <= 5)
					this.penalty -= Math.pow(2, 5 - old_distance) * data.getStudentsInCommon(exam, ae);
			});
		}

		this.e2t.put(exam, timeslot);

		this.data.aexams(exam).forEach(ae -> {
			if (!this.isExamScheduled(ae))
				return;

			Integer distance = Math.abs(this.getExamScheduling(ae) - timeslot);

			if (distance == 0)
				this.conflicts.add(EndpointPair.unordered(exam, ae));
			else if (distance <= 5)
				this.penalty += Math.pow(2, 5 - distance) * data.getStudentsInCommon(exam, ae);
		});
	}

	public boolean isExamScheduled(Integer exam) {
		return this.e2t.containsKey(exam);
	}

	public Integer getExamScheduling(Integer exam) {
		return this.e2t.get(exam);
	}

	public Set<Integer> getExamsForTimeslot(Integer t) {
		return this.e2t.entrySet().stream().filter(e -> t.equals(e.getValue())).map(e -> e.getKey())
				.collect(Collectors.toSet());
	}

	public Set<EndpointPair<Integer>> getConflicts() {
		return this.conflicts;
	}

	public boolean isFeasible() {
		return this.conflicts.isEmpty() && this.e2t.size() == data.exams().size();
	}

	public Double getPenalty() {
		return this.penalty / this.data.getNStudents();
	}

	@Override
	public Solution copy() {
		EttpSolution ret = new EttpSolution(this.data);
		ret.e2t = new LinkedHashMap<>(this.e2t);
		ret.conflicts = new LinkedHashSet<>(this.conflicts);
		ret.penalty = this.penalty;
		ret.objectiveValue = this.objectiveValue.clone();
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EttpSolution) {
			return e2t.equals(((EttpSolution) obj).e2t);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return e2t.hashCode();
	}

	/*
	 * OpenTS
	 */
	private double[] objectiveValue;

	@Override
	public Object clone() {
		return this.copy();
	}

	@Override
	public double[] getObjectiveValue() {
		return this.objectiveValue;
	}

	@Override
	public void setObjectiveValue(double[] objValue) {
		this.objectiveValue = objValue;
	}

	/*
	 * Custom methods
	 */
	public final void printToFile(String instance_name) {
		try {
			FileWriter fw = new FileWriter(instance_name + "_OMAAL_group03.sol");
			for (Map.Entry<Integer, Integer> entry : this.e2t.entrySet()) {
				fw.write(entry.getKey() + " " + Integer.toString(entry.getValue() + 1) + "\n");
			}
			fw.write("\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void merge(EttpSolution s) {
		this.e2t.putAll(s.e2t);
		this.conflicts.addAll(s.conflicts);
		this.penalty += s.penalty;
	}

}

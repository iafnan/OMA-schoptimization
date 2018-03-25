package timetabling.ettp.neigh;

import java.util.ArrayList;
import java.util.List;

import org.coinor.opents.Solution;
import org.jamesframework.core.search.neigh.Move;

import timetabling.ettp.EttpSolution;

public class EttpMove implements Move<EttpSolution>, org.coinor.opents.Move {
	private List<Integer> exams = new ArrayList<>();
	private List<Integer> new_slots = new ArrayList<>();
	private List<Integer> old_slots = new ArrayList<>();

	public EttpMove() {
	}

	public EttpMove(Integer exam, Integer new_slot) {
		this.addMove(exam, new_slot);
	}

	public void addMove(Integer exam, Integer new_slot) {
		this.exams.add(exam);
		this.new_slots.add(new_slot);
	}

	public Integer getN() {
		return this.exams.size();
	}

	@Override
	public void apply(EttpSolution sol) {
		for (int i = 0; i < this.exams.size(); ++i) {
			this.old_slots.add(sol.getExamScheduling(this.exams.get(i)));
			sol.assignExamToTimeslot(this.exams.get(i), this.new_slots.get(i));
		}
	}

	@Override
	public void undo(EttpSolution sol) {
		for (int i = 0; i < this.exams.size(); ++i) {
			sol.assignExamToTimeslot(this.exams.get(i), this.old_slots.get(i));
		}
	}

	@Override
	public void operateOn(Solution sol) {
		this.apply((EttpSolution) sol);
	}

	@Override
	public int hashCode() {
		return exams.hashCode();
	}
}

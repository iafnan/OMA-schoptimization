package timetabling;

import java.util.HashSet;
import java.util.Set;

public class Student {
	private final String id;
	private final Set<Integer> enrolled_exams;

	public Student(String id) {
		this.id = id;
		this.enrolled_exams = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public void enrollToExam(Integer exam_id) {
		this.enrolled_exams.add(exam_id);
	}

	public Set<Integer> getEnrolledExams() {
		return this.enrolled_exams;
	}
}

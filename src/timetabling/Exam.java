package timetabling;

public class Exam {
	private final Integer id;
	private final Integer numStudents;

	public Exam(Integer id, int numStudents) {
		this.id = id;
		this.numStudents = numStudents;
	}

	public Integer getId() {
		return id;
	}

	public Integer getNumStudents() {
		return numStudents;
	}

}

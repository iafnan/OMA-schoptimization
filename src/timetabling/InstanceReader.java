package timetabling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import timetabling.ettp.EttpData;

public class InstanceReader {
	private String instance_name;
	private EttpData data;
	private Integer available_slots;
	private Map<Integer, Exam> exams = new HashMap<>();
	private Map<String, Student> students = new HashMap<>();
	private MutableValueGraph<Integer, Integer> cgraph = ValueGraphBuilder.undirected().build();

	public InstanceReader(String instance_name) {
		this.instance_name = instance_name;

		this.readSlotFile();
		this.readExamFile();
		this.readStudentFile();

		this.data = new EttpData(ImmutableValueGraph.copyOf(this.cgraph), available_slots, this.students.size());
	}

	public EttpData getData() {
		return this.data;
	}

	private void readSlotFile() {
		try {
			FileReader fr = new FileReader(this.instance_name + ".slo");
			BufferedReader br = new BufferedReader(fr);
			this.available_slots = Integer.parseInt(br.readLine());
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void readExamFile() {
		try {
			FileReader fr = new FileReader(this.instance_name + ".exm");
			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					String[] parts = line.split(" ");
					this.addExam(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				}
			}

			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void readStudentFile() {
		try {
			FileReader fr = new FileReader(this.instance_name + ".stu");
			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					String[] parts = line.split(" ");
					this.enrollStudentToExam(parts[0], Integer.parseInt(parts[1]));
				}
			}

			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void addExam(Integer eid, Integer num_students) {
		exams.put(eid, new Exam(eid, num_students));
		this.cgraph.addNode(eid);
	}

	private void enrollStudentToExam(String sid, Integer new_eid) {
		this.ensureStudent(sid);

		for (Integer eid : students.get(sid).getEnrolledExams()) {
			Integer old_value = this.cgraph.edgeValueOrDefault(eid, new_eid, 0);
			this.cgraph.putEdgeValue(eid, new_eid, 1 + old_value);
		}

		students.get(sid).enrollToExam(new_eid);
	}

	private void ensureStudent(String sid) {
		if (!students.containsKey(sid)) {
			students.put(sid, new Student(sid));
		}
	}
}

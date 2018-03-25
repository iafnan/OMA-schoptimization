# OMA - Exam schedule optimization
Our solution for constrained, penalty based college exam scheduling problem by using Graph theory and multithreaded Tabu search algorithm to discover a feasible and quick solution and consequently optimizing it using multithreaded implementation of Variable Neighbourhood Search metaheuristic.


## Prerequisites
- Understanding of mathematical modeling
- Understanding of algorithms and optimization methods (Greedy, Evolutionary, MetaHeuristics, Exact methods)
- Understanding of Graph coloring problem and Kemp's algorithm
- Instances with students and exams information

### Platform
```
- Java
- OpenTS
- Guava
- JAMES 
- Error Prone
- Animal Sniffer
- args4j
```

## Where?
Politecnico di Torino

## Why?
University project for Optimization Methods and Algorithms course
### Problem statement
```
Let us consider a set E of exams, to be scheduled during an examination period at the end of the semester, and a set S of students. Each student is enrolled in a subset of exams, at least one. 
The examination period is divided in tmax ordered time-slots.
By law, conflicting exams (i.e. having enrolled students in common) cannot take place during the same time-slot. 
Moreover, to incentive universities at creating timetables more sustainable for the students, the Ministry of Education assigns a penalty for each couple of conflicting exams scheduled up to 5 time-slots apart. 
More precisely, given the number ne,e′ of students enrolled in both conflicting exams e and e′, which are scheduled i time-slots apart, the penalty is calculated as:
                                         [2^(5−i)] n_(e,e′) / |S|
The Examination Timetabling Problem (ETP) aims at assigning each exam to a specific time-slot ensuring that:
- each exam is scheduled once during the period,
- two conflicting exams are not scheduled in the same time-slot.
The objective is to minimize the total penalty resulting from the created timetable.

Assumptions:
during each time-slot there is always a number of available rooms greater than the total number of exams; rooms have enough capacity with respect to the number of enrolled students.
```

## When?
Fall 2017

## Status?
Closed

package timetabling;

import static com.google.common.graph.Graphs.inducedSubgraph;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import com.google.common.graph.ImmutableValueGraph;

public class GraphUtilities {
	static <N, V> Set<ImmutableValueGraph<N, V>> partitionGraph(ImmutableValueGraph<N, V> graph) {
		Set<ImmutableValueGraph<N, V>> partitions = new LinkedHashSet<>();
		Set<N> visited_nodes = new HashSet<>();

		for (N node : graph.nodes()) {
			if (!visited_nodes.contains(node)) {
				Set<N> reachable_nodes = reachableNodes(graph, node);
				visited_nodes.addAll(reachable_nodes);
				partitions.add(ImmutableValueGraph.copyOf(inducedSubgraph(graph, reachable_nodes)));
			}
		}

		return partitions;
	}

	static <N, V> Set<N> reachableNodes(ImmutableValueGraph<N, V> graph, N node) {
		Set<N> visitedNodes = new LinkedHashSet<>();
		Queue<N> queuedNodes = new ArrayDeque<>();
		visitedNodes.add(node);
		queuedNodes.add(node);
		// Perform a breadth-first traversal rooted at the input node.
		while (!queuedNodes.isEmpty()) {
			N currentNode = queuedNodes.remove();
			for (N successor : graph.successors(currentNode)) {
				if (visitedNodes.add(successor)) {
					queuedNodes.add(successor);
				}
			}
		}
		return Collections.unmodifiableSet(visitedNodes);
	}
}

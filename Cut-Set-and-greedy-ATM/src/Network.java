import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/**
 * @author AlgoDat Team
 * 
 */
public class Network implements INetwork {
	// -- attributes --
	private HashMap<Integer, Node> nodes;

	public INetwork residualGraph;

	// -- constructor --
	public Network() {
		nodes = new HashMap<Integer, Node>();
		residualGraph = null;
	}


	// -- node functions --
	/**
	 * returns the nodes
	 */
	@Override
	public LinkedList<Node> getNodes() {
		return new LinkedList<Node>(nodes.values()); //nodes.values() only returns a Collection, not a List, so we need to create it
	}


	@Override
	public Node addNode() {
		int newId = nodes.size();
		Node newNode = new Node(newId);
		nodes.put(newId, newNode);
		return newNode;
	}

	// -- edge functions --
	public void addEdge(Node startNode, Node endNode, int capacity) {
		if (!(testEdgeNodes(startNode, endNode)))
			inputError();
		startNode.addEdge(startNode, endNode, capacity);
	}

	public void addEdge(int startnode, int endnode, int capacity) {
		addEdge(nodes.get(startnode), nodes.get(endnode), capacity);
	}


	public void updateEdge(int start, int end, int flow, int capacity) {
		Edge tmp = this.getEdge(start,end);
		tmp.currentFlow = flow;
		tmp.capacity = capacity;
	}

	/**
	 * Returns graph edge specified by source and destination indices.
	 * 
	 * @param startNodeInd
	 *            index of start node
	 * @param targetNode
	 *            index of target node
	 */
	public Edge getEdge(int startNodeInd, int targetNodeint) {
		Node n = nodes.get(startNodeInd);
		for (Edge e : n.getIncidentEdges())
			if (e.endNode.id == targetNodeint)
				return e;
		return null;
	}

	public boolean testEdgeNodes(Node startNode, Node endNode) {
		return (startNode != null) && (endNode != null)
				&& nodes.values().contains(startNode)
				&& nodes.values().contains(startNode);
	}

	// -- state reset functions --
	/**
	 * resets the state of all nodes and edges to white
	 */
	public void clearMarksAll() {
		clearMarksNodes();
		for (Node currentNode : nodes.values())
			for (Edge currentEdge : currentNode.getIncidentEdges())
				currentEdge.status = Edge.WHITE;
	}

	/**
	 * help function to reset the state of all nodes to white
	 */
	public void clearMarksNodes() {
		for (Node n : nodes.values())
			n.status = Node.WHITE;
	}

	public boolean isAdjacent(Node startNode, Node endNode) {
		if (!(testEdgeNodes(startNode, endNode)))
			inputError();
		return startNode.hasEdgeTo(endNode);
	}

	/**
	 * Searches for sources in the graph
	 * 
	 * @return All sources found in the graph
	 */
	public Node findSource() {
		LinkedList<Node> sources = new LinkedList<Node>();
		boolean isSource = true;
		// source <-> no incoming edges
		for (Node n : nodes.values()) {
			isSource = true;
			for (Node m : nodes.values()) {
				if (!m.equals(n) && isAdjacent(m, n)) {
					isSource = false;
					break;
				}
			}
			if (isSource)
				sources.add(n);
		}
		// error handling
		testSingle(sources);
		return sources.getFirst();
	}

	/**
	 * Searches the graph for sinks.
	 * 
	 * @return All sinks found in the graph
	 */
	public Node findSink() {
		LinkedList<Node> sinks = new LinkedList<Node>();
		// sink <-> no incoming edges
		for (Node n : nodes.values()) {
			if (n.getIncidentEdges().isEmpty())
				sinks.add(n);
		}
		// error handling
		testSingle(sinks);
		return sinks.getFirst();
	}

	public void testSingle(LinkedList<Node> nodes) {
		if (nodes.size() == 0 || nodes.size() > 1)
			inputError();
	}

	/**
	 * Finds an augmenting path from start to end in the graph A path is
	 * augmenting if all it's edges have residual capacity > 0 (You can choose
	 * from several algorithms to find a path)
	 * 
	 * @param startNodeId
	 *            the id of the start node from where we should start the search
	 * @param endNodeId
	 *            the id of the end node which we want to reach
	 * 
	 * @return the path from start to end or an empty list if there is none
	 */
	public LinkedList<Node> findAugmentingPath(int startNodeId, int endNodeId) {
		Node startNode = nodes.get(startNodeId);
		Node endNode = nodes.get(endNodeId);

		// this list is also the stack for depth first search
		LinkedList<Node> path = new LinkedList<Node>();
		// For testing purposes, you could use here your Edmonds-Karp implementation from Blatt 06
		// clean up
		clearMarksAll();
		return path;
	}

	// -- code to complete --

	/**
	 * Computes the maximum flow over the network with the Ford-Fulkerson
	 * Algorithm
	 * 
	 * @returns Value of maximal flow
	 */
	public int edmondsKarp() {
		int totalFlow = 0;
		// For testing purposes, you could use here your Edmonds-Karp implementation from Blatt 06
		return totalFlow;
	}


	/**
	 * Finds min cut in this graph
	 * 
	 * 
	 * @return a list of edges which have to be cut for 
	 * severing the flow between source and sink node
	 */
	public List<Edge> findMinCut() {
		List<Edge> cutEdges = new LinkedList<Edge>();
		Node source = findSource();
		// TODO: Your implementation here
		//fertig
		
		if(source == null || nodes.values().contains(source) == false)
			throw new NullPointerException("No source exists");
		
		LinkedList<Node> S = findReachableNodes(source);
		LinkedList<Node> T = new LinkedList<Node>();
		
		//T beinhaltet alle Knoten, die nicht in S sind.
		for(Node n : getNodes()) {
			if(S.contains(n) == false) {
				T.add(n);
			}
		}
		
		//Wenn ein Knoten in S und ein Knoten in T eine gemeinsame Kante haben, gehoert diese Kante zum minCut.
		for(Node n : getNodes()) {
			for(Edge e : n.getIncidentEdges()) {
				if(S.contains(e.startNode) && T.contains(e.endNode) && cutEdges.contains(e) == false) {
					cutEdges.add(e);
				}
			}
		}
		
		return cutEdges;
	}


	public LinkedList<Node> findReachableNodes(int startNode) {
		return findReachableNodes(nodes.get(startNode));
	}

	/** Does a Breadth-First-Search to find the set of nodes which 
	 * are connected to the startNode with nonzero flow capacity
	 * 
	 * @argument startNode: start/source node
	 * 
	 * @return List of nodes that are reachable from source 
	 */	
	public LinkedList<Node> findReachableNodes(Node startNode) {
		LinkedList<Node> nodeList = new LinkedList<Node>();
		clearMarksNodes();
		// TODO: Your implementation here
		//fertig
		
		if (startNode == null || nodes.values().contains(startNode) == false)
			throw new RuntimeException("There is no startnode.");
		
		//BFS
		LinkedList<Node> queue = new LinkedList<Node>();
		
		startNode.status = 0;
		
		queue.add(startNode);
		
		while(queue.isEmpty() == false) { 
			Node node = queue.poll();
			node.status = 1;			
			List<Node> nachbarn = node.getSuccessorNodes();
			Collections.sort(nachbarn, new Comparator<Node>() {
			    @Override
			    public int compare(Node o1, Node o2) {
				return o1.id - o2.id;
			    }
			});
			for(Node kind : nachbarn) { //Wenn die Kantenkapazitaet nicht die nonzero flow capacity ist, wird der entsprechende Knoten hinzugefuegt.
				if(node.getEdgeTo(kind).capacity != node.getEdgeTo(kind).currentFlow && kind.status == 0) {
				queue.add(kind);
				kind.status = 1;
				}
			}
			node.status = 2;
			nodeList.add(node);
		}
		
		return nodeList;
	}	
	
	/**
	 * Builds a residual graph from a flow graph
	 * 
	 * @return the residual graph of this flow graph
	 */
	public INetwork initializeResidualGraph() {

		Network residualGraph = new Network();
		Edge reverseEdge;
		// adding nodes
		for (int i = 0; i < nodes.values().size(); i++)
			residualGraph.addNode();
		// adding edges
		for (Node n : nodes.values()) {
			for (Edge e : n.getIncidentEdges()) {
				// Add forward edges with same capacity
				residualGraph.addEdge(n.id, e.endNode.id, e.capacity);
				// Add backwards edges
				reverseEdge = getEdge(e.endNode.id, n.id);
				if (reverseEdge != null)
					residualGraph.addEdge(e.endNode.id, n.id, reverseEdge.capacity);
				else
					residualGraph.addEdge(e.endNode.id, n.id, 0);
			}
		}
		return residualGraph;
	}

	
	
	/**
	 * Finds the minimal residual capacity over the given path
	 * 
	 * @return the minimal capacity
	 */
	public int findMinCapacity(LinkedList<Node> path) {
		int minCapacity = Integer.MAX_VALUE;
 		// For testing purposes, you could use here your Edmonds-Karp implementation from Blatt 06
		return minCapacity;
	}

	/**
	 * Update capacity on given path, to be executed on residual graph
	 */
	public void updateResidualCapacity(int minCapacity, LinkedList<Node> path) {
		// For testing purposes, you could use here your Edmonds-Karp implementation from Blatt 06
	}

	/**
	 * Calculates the current flow in this graph.
	 * 
	 * @param source
	 *            the source of the flow
	 * 
	 * @return the value of the flow
	 */
	public int getFlow(Node source) {
		int flow = 0;
		for (Edge e : source.getIncidentEdges()) {
			if (e.currentFlow > 0)
				flow += e.currentFlow;
		}
		return flow;
	}

	public LinkedList<Node> breadthFirstSearch(int startNode) {
		return breadthFirstSearch(nodes.get(startNode));
	}

	public LinkedList<Node> breadthFirstSearch(Node startNode) {
		LinkedList<Node> nodeList = null;
		clearMarksNodes();

		if (startNode == null || !nodes.values().contains(startNode)) {
			nodeList = new LinkedList<Node>();
		} else {
			nodeList = new LinkedList<Node>();
			LinkedList<Node> queue = new LinkedList<Node>();

			startNode.status = Node.GRAY;
			queue.addLast(startNode);

			while (!queue.isEmpty()) {
				Node current = queue.removeFirst();
				current.status = Node.BLACK;
				nodeList.addLast(current);

				for (Node neighbor : current.getSuccessorNodes()) {

					if (neighbor.status == Node.WHITE) {
						neighbor.status = Node.GRAY;
						queue.addLast(neighbor);
					}
				}
			}
		}
		return nodeList;
	}

	// -- utils --
	public void inputError() {
		System.out.println("Incorrect input.");
		System.exit(1);
	}
}


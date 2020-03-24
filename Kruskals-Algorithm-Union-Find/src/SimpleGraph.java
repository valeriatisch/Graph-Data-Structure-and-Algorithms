import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import java.util.LinkedList;
import java.util.List;

/**
 * A data structure to store a graph consisting of a set of unlabeled nodes and
 * a set of undirected, weighted edges connecting the nodes. Internally,
 * bidirectional edges are stored by inserting forward and backward edges.
 * 
 * The interface provides methods to add nodes and edges. Both methods can be
 * called at all time and the data structure should not assume a limitation on
 * the number of nodes or edges.
 * 
 * Furthermore, there are methods to access a single edge or to check whether
 * two nodes are connected as well as methods to access all nodes connected with
 * a specific node and thus all outgoing edges.
 * 
 * @author Joerg Schneider, modified by AlgoDat-Team
 */
public class SimpleGraph implements Graph {

	public HashMap<Integer, Node> nodes;

	// needed for testing, where the execution of methods should not stop
	private boolean showSteps = true;

	public SimpleGraph() {
		nodes = new HashMap<Integer, Node>();
	}

	/**
	 * Adds an edge to the graph.
	 * 
	 * The edge is specified by a start node and a target node. If there is
	 * already an edge between these two nodes the behavior of the method is
	 * unspecified. The edge is labeled with a positive weight value.
	 * 
	 * @param startNode
	 *            the start node
	 * @param targetNode
	 *            the target node
	 * @param weight
	 *            a positive, non-zero value assigned to this edge
	 */
	public void addEdge(Node startnode, Node endnode, int weight) {
		if (startnode != null && endnode != null) {

			// only add edges between nodes which are already part of this graph
			if (nodes.values().contains(startnode)
					&& nodes.values().contains(endnode)) {
				startnode.addEdge(endnode, weight);
				endnode.addEdge(startnode, weight);
			}
		}
	}


	@Override
	public void addEdge(int startnode, int endnode, int weight) {
		addEdge(nodes.get(startnode), nodes.get(endnode), weight);
	}


	/**
	 * Adds a node to the graph.
	 * 
	 * As the nodes are unlabeled this method has no parameter. An identifier is
	 * returned which can be used to access this specific node when calling the
	 * other operations.
	 * 
	 * @return the added Node.
	 */
	@Override
	public Node addNode() {
		Node newOne = null;
		int newId = nodes.size();
		newOne = new Node(newId);
		nodes.put(newId, newOne);
		return newOne;
	}


	/**
	 * Returns the weight of the edge between the given nodes.
	 * 
	 * @param startNode
	 *            the the start node as provided by addNode()
	 * @param targetNode
	 *            the target node as provided by addNode()
	 * @return WEIGHT_NO_EDGE, if there is no directed edge between the start
	 *         node and the target node or the positive, non-zero weight stored
	 *         by addEdge().
	 */
	public int getWeight(Node startnode, Node endnode) {
		if (startnode != null && endnode != null) {
			if (nodes.values().contains(startnode)
					&& nodes.values().contains(endnode)) {
				return startnode.getWeight(endnode);
			}
		}
		return Graph.WEIGHT_NO_EDGE;
	}

	@Override
	public int getWeight(int startnode, int endnode) {
		return getWeight(nodes.get(startnode), nodes.get(endnode));
	}

	/**
	 * Returns all nodes of the graph.
	 * 
	 * @return list containing all valid node identifiers.
	 */
	@Override
	public List<Node> getNodes() {
		return new ArrayList<Node>(nodes.values());
	}

	/**
	 * Returns all edges of the graph. Caution: this method returns each edge
	 * twice, as both (u,v) and (v,u) are stored internally
	 * 
	 * @return list containing all valid node identifiers.
	 */
	public List<Edge> getEdges() {
		List<Edge> edges = new LinkedList<Edge>();

		for (Node node : nodes.values()) {
			List<Edge> incidentEdges = node.getIncidentEdges();
			edges.addAll(incidentEdges);
		}
		return edges;
	}

	/**
	 * For a node returns all nodes the outgoing edges of the node directly lead
	 * to.
	 * 
	 * @return list containing all directly connected nodes.
	 */
	public List<Node> getAdjacentNodes(Node startnode) {
		List<Node> nodes = null;
		if (startnode != null && this.nodes.values().contains(startnode)) {
			return startnode.getAdjacentNodes();
		}
		nodes = new LinkedList<Node>();
		return nodes;
	}


	@Override
	public List<Node> getAdjacentNodes(int startnode) {
		return getAdjacentNodes(nodes.get(startnode));
	}

	/**
	 * Determines if the given nodes are directly connected, that is whether
	 * there is some directed edge that links startNode to targetNode. For all
	 * existing nodes: isConnected(n1, n2) <=> getAdjacentNodes(n1).contains(n2)
	 * 
	 * @param startNode
	 *            the start node as provided by addNode()
	 * @param targetNode
	 *            the target node as provided by addNode()
	 * @return true iff there is a directed edge between the given start node
	 *         and target node.
	 */
	public boolean isConnected(Node startnode, Node endnode) {
		if (startnode != null && endnode != null) {
			if (nodes.values().contains(startnode)
					&& nodes.values().contains(endnode)) {
				return startnode.hasEdgeTo(endnode);
			}
		}
		return false;
	}


	@Override
	public boolean isConnected(int startNodeID, int endNodeID) {
		return isConnected(nodes.get(startNodeID), nodes.get(endNodeID));
	}

	/**
	 * resets all colors in visual representation of the graph to white
	 */
	@Override
	public void clearMarks() {
		for (Node nextNode : nodes.values()) {
			nextNode.status = Node.WHITE;
		}
	}

	/**
	 * help function for reset the state of all nodes to white
	 */
	private void resetState() {
		for (Node n : nodes.values()) {
			n.status = Node.WHITE;
		}
	}

	/**
	 * travels randomly in the graph. used to demonstrate visualization class
	 * 
	 * @param index
	 *            of startnode
	 */
	public void showGraph(Node startNode) {

		resetState();
		if (startNode == null || !nodes.values().contains(startNode)) {
			System.out.println("Error! this node does not exist!");
			return;
		}
		startNode.status = Node.GRAY;

		this.stopExecutionUntilSignal();
		startNode.status = Node.BLACK;

		List<Edge> edges = startNode.getIncidentEdges();
		Random ran = new Random();

		while (edges.size() != 0) {
			List<Node> nodes = new ArrayList<Node>();
			// filter black neighbor node
			// white and gray neighbor nodes will add to nodes-list
			for (Edge e : edges) {
				if (e.getEndnode().status != Node.BLACK) {
					e.getEndnode().status = Node.GRAY;
					nodes.add(e.getEndnode());
				}
			}
			this.stopExecutionUntilSignal();
			if (nodes.size() == 0)
				return;

			startNode = nodes.get(ran.nextInt(nodes.size()));
			edges = startNode.getIncidentEdges();
			startNode.status = Node.BLACK;
		}
	}

	public void showGraph(int nodeID) {
		showGraph(nodes.get(nodeID));
	}


	// checks whether the given node has a white neighbor or not
	private boolean hasWhiteNeighbor(Node node) {

		for (Node neighbour : node.getAdjacentNodes()) {
			if (neighbour.status == Node.WHITE) {
				return true;
			}
		}
		return false;
	}

	// checks whether the given node has a gray neighbor or not
	private boolean hasGrayNeighbor(Node node) {

		for (Node neighbour : node.getAdjacentNodes()) {

			if (neighbour.status == Node.GRAY) {
				return true;
			}
		}

		return false;
	}

	// ---- Methods to visualize the operations of a graph algorithm -----

	/**
	 * Returns an index, representing the color of the given node. 0:black,
	 * 1:blue, 2:red, 3:green, 4:orange, 5:gray You may want to paint nodes that
	 * don't exist in gray.
	 * 
	 * @param nodeNumber
	 *            the node for which the color is requested, as returned by
	 *            addNode()
	 * 
	 * @return an index between 0 and 5, representing the color of the given
	 *         node
	 */
	public int getColorOfNode(Node node) {
		if (null == node) {
			return 5;
		} else {
			return node.status;
		}
	}

	@Override
	public int getColorOfNode(int nodeID) {
		return getColorOfNode(nodes.get(nodeID));
	}

	public int getColorOfEdge(int startNode, int targetNode) {
		return getColorOfEdge(nodes.get(startNode), nodes.get(targetNode));
	}

	/**
	 * Returns an index, representing the color of the given edge. 0:black,
	 * 1:blue, 2:red, 3:green, 4:orange, 5:gray You may want to paint edges that
	 * don't exist in gray.
	 * 
	 * @param sourceNodeNumber
	 *            the index of the node where the edge starts , as returned by
	 *            addNode()
	 * @param targetNodeNumber
	 *            the index of the node where the edge ends , as returned by
	 *            addNode()
	 * 
	 * @return an index between 0 and 5, representing the color of the given
	 *         edge
	 */
	public int getColorOfEdge(Node startNode, Node targetNode) {
		if (null == startNode || null == targetNode) {
			return 5;
		}
		LinkedList<Edge> incidentEdges = nodes.get(startNode.id)
				.getIncidentEdges();
		// search edge
		Edge searchedEdge = null;
		for (Edge nextEdge : incidentEdges) {
			if (nextEdge.getEndnode().id == targetNode.id) {
				searchedEdge = nextEdge;
			}
		}
		if (null == searchedEdge) {
			return 5;
		} else {
			return searchedEdge.status;
		}
	}

	// ------------------------------------------------------------------------
	// ---- Methods for Kruskal -----------------------------------------------

	/**
	 * calculates the minimum span tree using Kruskal's algorithm
	 * 
	 * @return a new Graph representing the minimum span tree
	 */
	public Graph toMinSpanTree() {
		// TODO Homework 2.2
		
		
	    /**
		 * Implementiert den Algorithmus von Kruskal in der Methode
		 * toMinSpanTree() der Klasse SimpleGraph. Beachtet, dass
		 * SimpleGraph ein ungerichteten Graphen implementiert, in
		 * dem jede Kante beim Auruf von addEdge zweimal hinzugefügt
		 * wird — Einmal als Vorwärts- und einmal als Rückwärtskante.
		 * Die Methode SimpleGraph toMinSpanTree() soll den minimalen
		 * Spannbaum zurückgeben.
		 * Dieser Spannbaum soll ebenfalls ein SimpleGraph sein, wird
		 * also jede Kante des Baumes zweimal enthalten.
		 * 
		 * Hinweis: Verwendet in eurer Implementierung eine PriorityQueue
		 * für die sortierte Bearbeitung der Kanten, und ein UnionFindSet
		 * um hinzufügbare Kanten zu erkennen. Die für die PriorityQueue
		 * benötigte Methode compareTo in Edge.java haben wir bereits implementiert.
		 * Behandelt fehlerhafte Eingabegraphen (z.B. nicht zusammenhängende Graphen)
		 * mit demWerfen einer RuntimeException(), oder indem ihr NULL zurückgebt.
		 */
		
		/**
		 * Pseudocode aus der VL:
		 * 
		 * UF: UnionFind(V) //Initialisierung mit einzelnen Komponenten fuer jeden Knoten
		 * MST <- leer //Kanten des minimalen Spannbaums (z.B. als Queue)
		 * sortiere Kanten E aufsteigen nach Gewicht
		 * for (v,w)eE in dieser Reihenfolge
		 * 		if UF.find(v) != UF.find(w) then
		 * 			UF.union(v,w)
		 * 			add (v,w) to MST
		 * 		end
		 * end
		 */
		
		/**
		 * Sortiere alle Kanten absteigen nach dem Gewicht (PrioQueue).
		 * choosen enthaelt alle Kanten, die wir in den "neuen" Graphen
		 * mit dem kuerzesten Weg nehmen wollen. 
		 */
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>();
        LinkedList<Edge> choosen = new LinkedList<>();

		/**
		 * Alle Kanten der PrioQueue hinzufuegen:
		 */
		for(Edge edge : getEdges()) {
			edges.add(edge);
		}
		
		/**
		 * Nodes in Union Struktur als verbunden darstellen
		 * und alle aktuellen Kanten hinzufuegen.
		 */
		UnionFindSet<Node> ufs = new UnionFindSet<Node>();
		ufs.add(getNodes());
		
		/**
		 * Vorbereitung abgeschlossen.
		 * 
		 * Algorithmusanfang:
		 * Fuer alle Kanten:
		 * Nimm jeweils die erste Kante mit dem niedrigsten Gewicht.
		 * Vergleiche den Repraesentanten des Start- und Endnodes.
		 * 		Wenn diese gleich sind, wuerde das Hinzufuegen dieser Kanten einen Kreis verursachen.
		 * 		Wenn diese nicht gleich sind, in die choosen Liste hinzufuegen und union/verbinden.
		 */
		while(!edges.isEmpty()) {
		    
		    Edge e = edges.poll();
		    
		    if(ufs.getRepresentative(e.getStartnode()) != ufs.getRepresentative(e.getEndnode())) {
			choosen.add(e);
			ufs.union(e.getStartnode(), e.getEndnode());
		    }
		    
		}
		
		 /**
		  * Sind ueberhaupt Knoten in der Liste?
	      */
		if(getNodes() == null || getNodes().size() == 0){
			throw new RuntimeException("Es existieren keine Knoten.");
	    }
	        
        /**
         * Ist der MST zusammenhängend?
         */
        Node vg = getNodes().get(0);
        for(Node n : getNodes()){
          if(ufs.getRepresentative(n) != ufs.getRepresentative(vg)) {
            throw new RuntimeException("Der MST ist nicht zusammenhaengend.");
          }
        }
        
        /**
         * Ab hier sollten in choosen alle Kanten fuer den MST drin sein.
         * Erstelle den MST Graphen.
         */
        
		SimpleGraph graph = new SimpleGraph();
		
		/**
		 * Fuege dem MST Graph alle Nodes hinzu.
		 */
		for(Node n : getNodes()) {
			graph.addNode();
		}
		
		/**
		 * Fuege dem MST Graph alle MST Kanten hinzu bzw. den kuerzesten Weg.
		 */
		for(Edge e : choosen) {
		    graph.addEdge(e.getStartnode().id, e.getEndnode().id, e.getWeight());
		}
		
		
		return graph;
	    
	}


	// ---- Methods to stop and resume the execution of a graph algorithm -----

	// synchronization variable used to stop and resume processing
	private boolean isStopped = false;

	/**
	 * Tests if the current execution of a graph-algorithm was stopped
	 * 
	 * @return true if the processing is stopped, false otherwise.
	 */
	@Override
	synchronized public boolean isStopped() {
		return this.isStopped;
	}

	/**
	 * Stops or resumes the current executed graph algorithm
	 * 
	 * @param status
	 *            true to stop, false to resume execution
	 */
	@Override
	synchronized public void setStopped(boolean status) {
		this.isStopped = status;
	}

	/**
	 * Stops the execution of an algorithm until the visualization signals that
	 * he can be resumed. This feature can be used to visualize the steps of the
	 * algorithms.
	 */
	private void stopExecutionUntilSignal() {
		if (showSteps) {
			System.out.print(".");
			this.setStopped(true);
			while (this.isStopped()) {
				this.sleep(100);
			}
		}
	}

	/**
	 * Stops the execution for a given time
	 * 
	 * @param milliseconds
	 *            time the execution will stop in milliseconds
	 */
	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// the VM doesn't want us to sleep anymore,
			// so get back to work
		}
	}

	/**
	 * Sets the showSteps flag.
	 * 
	 * @param show
	 *            the flag
	 */
	@Override
	public void setShowSteps(boolean show) {
		this.showSteps = show;
	}
}

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The class <code>Node</code> implements a node.
 * 
 * @author Marc Alexa - modified by MPGI2 - Tutors
 * @version 1.2
 */
public class Node{
	LinkedList<Edge> edges;
	int id;
	
	/**
	 * Stores the status of this node, which is either WHITE, GRAY, or BLACK.
	 */
	public int status;
	static final int WHITE = 0;
	static final int GRAY = 1;
	static final int BLACK = 2;
	
	/**
	 * Predecessor on the shortest path from startNode to this node.
	 */
	public Node predecessor;
	
	/**
	 * Constructor that creates specified node.
	 * 
	 * @param name
	 *            the drawn value in visualization
	 */
	public Node(int name) {
		// initialize list of edges
		edges = new LinkedList<Edge>();
		this.id = name;
	}

	/**
	 * An edge pointing from this node to end node is inserted at the beginning of the edge list.
	 *
	 * @param endnode
	 *            End point of this edge.
	 * @param weight
	 *            Weight of this edge.
	 *
	 */
	public void addEdge(Node endnode, double weight) {
		edges.addFirst(new Edge(endnode, weight));
	}
	
	/**
	 * Finds the correct edge, which points to given end node and return
	 * the weight.
	 * 
	 * @param endnode
	 *            End point of the edge to find.
	 *            
	 * @return weight of the edge
	 */
	public double getWeight(Node endnode) {
		for(Edge e : edges){
			if(e.endNode == endnode){
				return e.weight;
			}
		}
		return Graph.WEIGHT_NO_EDGE;
	}
	
	public int getID(){
		return id;
	}
	
	/**
	 * Returns an list of nodes which are connected to this node.
	 * 
	 * @return list of nodes connected to this node
	 */
	public ArrayList<Node> getAdjacentNodes() {
			ArrayList<Node> adjNodes = new ArrayList<Node>();
			
			for(Edge e: edges){
				adjNodes.add(e.endNode);
			}
				
			return adjNodes;
	}

	/**
	 * Determines if this node is connected to a given one.
	 * 
	 * @param endnode
	 *            End point of the edge.
	 *            
	 * @return true if node is connected to given end node
	 */
	public boolean hasEdgeTo(Node endnode) {
		for(Edge e: edges){
			if(e.endNode.equals(endnode)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns edge from this node to endnode, if it exists.
	 * @param endnode
	 *            End point of the edge.
	 *            
	 * @return edge to endnode, if it exists, null otherwise
	 */
	public Edge getEdgeTo(Node endnode) {
		for(Edge e: edges){
			if(e.endNode.equals(endnode)){
				return e;
			}
		}
		return null;
	}


	/**
	 * Generates a collection of all outgoing edges of this node
	 * 
	 * @return the collection of outgoing edges.
	 */
	public LinkedList<Edge> getOutgoingEdges() {
		return new LinkedList<Edge>(edges);
	}
	
	@Override
	public String toString(){
		return "Node(id="+id+", edges="+edges+")";
	}
	
	@Override
	// essential for drawing. do not besmudge
	public boolean equals(Object o){
		if(o instanceof Node){
			Node other = (Node) o;
			return id == other.getID();
		}
		return false;
	}
	
	@Override
	// essential for drawing. do not besmudge
	public int hashCode() {
		return id;
	}
}


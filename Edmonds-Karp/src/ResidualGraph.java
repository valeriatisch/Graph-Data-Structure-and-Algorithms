import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author Arne
 * 
 */
public class ResidualGraph extends DiGraph {

	// -- constructor --
	public ResidualGraph() {
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
		// TODO: Your implementation here
		//fertig
		
		//Pruefe, ob Knoten ueberhaupt vorhanden sind.
		if (nodes.get(startNodeId) == null || nodes.get(endNodeId) == null) {
			throw new NullPointerException("Startnode/Endnode doesn't exist.");
		}

		//Von vorne... Diesmal aber ohne Blatt 3.
//		LinkedList<Node> path = new LinkedList<Node>(); //Liste vom Pfad, die am Ende zurueck gegeben wird
//		LinkedList<Node> queue = new LinkedList<>(); //Queue zum Suchen benutzen
//		
//		queue.add(nodes.get(startNodeId)); //Starte mit source
//		
//		while(!queue.isEmpty()) {
//			Node n = queue.poll(); //Hole jeweils 1. Element aus der Queue
//			
//			for(Node kind : n.getAdjacentNodes()) {
//				Edge edge = n.getEdgeTo(kind);
//				//Knoten nicht mehrmals hinzufuegen; Kanten mit Kapazitaet 0 werden nicht betrachtet.
//				if(path.contains(kind) || edge.weight == 0) continue;
//				else {
//					queue.push(kind);
//				}
//			}
//		
//			path.add(n);
//			//Pruefe, ob sink erreicht wurde
//			if(n == nodes.get(endNodeId))
//				return path;
//		}
//
//		return new LinkedList<>();
		

		//Ich komme zurueck zu Blatt03...
		
		this.clearMarks(); //Fange mit unmarkierten Nodes an!
		
		LinkedList<Node> nodeList = new LinkedList<Node>(); // als Queue benutzen
		LinkedList<Node> path = new LinkedList<Node>(); // augmenting path list, die am Ende zurueck gegeben wird
		
		Node source = nodes.get(startNodeId);
		Node sink = nodes.get(endNodeId);
		
		nodeList.add(source); //fange mit source an

		source.status = 0;
		//BFS
		while (nodeList.isEmpty() == false) {
			Node node = nodeList.removeFirst();
			node.status = 1;
			path.add(node);
			if (node.equals(sink)) break;
			for (Node kind : node.getAdjacentNodes()) {
				Edge edge = node.getEdgeTo(kind);
				if (kind.status == 0 && edge.weight > 0) {
						kind.status = 1;
						kind.predecessor = node;
						nodeList.add(kind);
					}
				}
			node.status = 2;
			}
		
		this.resetState(); //"Raeum dein Zimmer auf!" haben wir gelernt! :)
		
		if(!path.contains(nodes.get(endNodeId))) return new LinkedList<>(); //Fals sink nicht im Pfad ist, leere Liste zurueck geben
		else { //So koennte der Fluss wirklich aussehen:
			//Eine Liste bestehend aus den "Vorgaengern" (BACKTRACKING) am Ende:
			//Also wird von sink to source gelaufen.
			LinkedList<Node> zurueck = new LinkedList<>();
			Node predecessor = nodes.get(endNodeId);
			while(predecessor != null) {
				zurueck.addFirst(predecessor); //Aufs richtige Einfuegen achten >.<
				predecessor = predecessor.predecessor;
			}
			return zurueck;
		}
	}

	/**
	 * Finds the minimal residual capacity over the given path
	 * 
	 * @return the minimal capacity
	 */
	public double findMinCapacity(LinkedList<Node> path) {
		// TODO: Your implementation here
		//fertig
		
		/**
		 * Idee: Alle Kanten zwischen den Nodes aus path in eine PriorityQueue einfuegen und das 1. Element holen
		 * Da keine compareTo in Edge, selbst eine schreiben...
		 */
		
		/**
		 * Andere moegliche Idee:
		 * Mincapacity = weight der 1. Kante in path.
		 * Mincapacity mit jedem weight der anderen Kanten in path vergleichen (for-Schleife durch path.size()).
		 * Wenn mincapacity groesser als dieser Wert ist, setze mincapacity auf diesen Wert
		 * Wenn Kante nicht existiert, dann vergleiche nicht (oder achte vorher auf Index)
		 */
		
		double mincapacity;
		
		if (path == null) {
			throw new RuntimeException("There is no path.");
		}
		
		//Wenn path leer ist, ist die Kapazitaet 0
		if (path.size() == 0) {
			return 0.0;
		}
		
		//Speichere alle Edges zwischen den Nodes in path in der Liste edgeList
		LinkedList<Edge> edgeList = new LinkedList<>();
		
		for (int i = 0; i < path.size() - 1; i++) {
			Node n = path.get(i);
			Edge e = n.getEdgeTo(path.get(i + 1));
			edgeList.add(e);
		}
		
		//Sortiere alle Edges nach weight/Kapazitaet in der Liste edgeList
		//mit einem Comparator
		Collections.sort(edgeList, new Comparator<Edge>() {
			@Override
			public int compare(Edge a, Edge b) {
				//a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
				if (a.weight < b.weight) {
					return -1;
				} else if (a.weight > b.weight) {
					return 1;
				} else {
					return 0;
				}
				//"Quelle:" https://stackoverflow.com/questions/14154127/collections-sortlistt-comparator-super-t-method-example
			}
		});
		
		//Hole das 1. Element aus der edgeList raus bzw. das Element mit dem kleinsten Gewicht/der kleinsten Kapazitaet
		Edge e1 = edgeList.poll();
		//Setze mincapacity auf das Gewicht der rausgeholten Kante
		mincapacity = e1.weight;

		return mincapacity;

	}

	/**
	 * Update capacity on given path, to be executed on residual graph
	 */
	public void updateResidualCapacity(double minCapacity, LinkedList<Node> path) {
		// TODO: Your implementation here
		//fertig
		
		/**
		 * Fuer jede Kante in dem gegebenen Pfad die Restkapazitaet berechnen.
		 * Tut.Blatt: Restkapazität = Kapazität - aktueller Fluss (bzw. verbrauchte Kapazität)
		 */
		
		//Von vorne... und anders, eine for-Schleife reicht ja auch aus.
		
		Node predecessor = null;
		
		for (Node node : path) {
			if (predecessor == null) predecessor = node;
			else {
				getEdge(predecessor.id, node.id).weight -= minCapacity; //Hinrichtung
				getEdge(node.id, predecessor.id).weight += minCapacity; //Rueckrichtung
				predecessor = node;
			}
		}
		
//		minCapacity = findMinCapacity(path);
//		
//		//Hinrichtung:
//		for (int i = 0; i < path.size() - 1; i++) {
//			Node n = path.get(i);
//			Edge e = n.getEdgeTo(path.get(i + 1));
//			e.weight -= minCapacity;
//		}
//		
//		//Rueckrichtung:
//		for (int i = path.size()-1; i > 1; i--) {
//			Node n = path.get(i);
//			Edge e = n.getEdgeTo(path.get(i - 1));
//			e.weight += minCapacity;
//		}
	
	}

}

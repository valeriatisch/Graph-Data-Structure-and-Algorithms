import java.util.Comparator;
import java.lang.Math;

/**
 * This class implements a heuristic based on the "Manhatten" distance metric (L1 Norm)
 * 
 **/
public class HeuristicManhattan implements Comparator<CellNode> {
	
		/**
		 * The goal node which the heuristic operates on:
		 */
		CellNode goal;
		
		/**
		 * 
		 * @param goal
		 * 		the target/goal node the heuristic should be computed with
		 */
		public HeuristicManhattan(CellNode goal) {
			this.goal = goal;
		}
		
		/**
		 * Computes an estimate of the remaining distance from node n to the goal node and
		 * updates the node attribute distanceRemainingEstimate
		 * 
		 *  This class implements the L1 norm ("Manhattan" distance)
		 *  as the permissible heuristic
		 *  
		 * @param n
		 * 		node to estimate the remaining distance from
		 */
		public void estimateDistanceToGoal(CellNode n) {
        	//Your implementation here

			//Hint: you only need to estimate once for each node.
			//Berechne wie auf dem Tutoriumsblatt d(n, ziel) = |xn − xziel| + |yn − yziel|
			n.distanceToGoalEstimate = Math.abs(n.i - goal.i) + Math.abs(n.j - goal.j);
        	 
       	}

    	/*
   		 * compares two nodes based on the distance heuristic
   		 * Computes the function cost(n) = d(start, n)+h(n, goal), 
   		 * where d(start, n) is the distance from the start to n \
    	 * and h(n, goal) is an estimate of the distance from n to the goal based on the Manhattan distance
   		 *  
   		 *  
   		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    	 */		
         public int compare(CellNode n1, CellNode n2) {
        	 //Your implementation here  
           	
            //Rufe erst estimateDistanceGoal auf (fuer distanceToGoalEstimate)
           	this.estimateDistanceToGoal(n1);
           	this.estimateDistanceToGoal(n2);
           	//Berechne
           	double kosten1 = n1.distance + n1.distanceToGoalEstimate;
           	double kosten2 = n2.distance + n2.distanceToGoalEstimate;
           	//Vergleiche
           	if(kosten1 < kosten2) return -1;
           	else if(kosten1 > kosten2) return 1;
           	else return 0;
         }		

}


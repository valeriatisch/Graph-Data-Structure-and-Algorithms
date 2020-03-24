import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class MinCutTest
{

	Network g;
	Network g2;
	
	@Before
	public void setup() throws IOException {
		g = GraphIO.loadGraphEdgeList("tests/testgraphen/graphMC_1_flow.txt");
		g2 = GraphIO.loadGraphEdgeList("tests/testgraphen/graphMC_2_flow.txt");
		g.residualGraph = GraphIO.loadGraphEdgeList("tests/testgraphen/graphMC_1_residual.txt");
		g2.residualGraph = GraphIO.loadGraphEdgeList("tests/testgraphen/graphMC_2_residual.txt");	
	}

	
	@Test
	public void testReachableNodes() throws IOException{		
		List<Node> nodes = g2.residualGraph.findReachableNodes(0);
		
		Assert.assertEquals("findReachableNodes() does not find the correct number of nodes.", 2, nodes.size());
		int node0inSet=0;
		int node1inSet=0;
		for (Node n : nodes) {
			if (0 == n.getID()) {
				node0inSet++;
			} else if (1 == n.getID()){
				node1inSet++;
			} else {
				Assert.fail("The set contains an unexpected node");
			}
		}			
		Assert.assertEquals("Node 0 is missing in the Source set", 1, node0inSet);		
		Assert.assertEquals("Node 1 is missing in the Source set", 1, node1inSet);		
		
	}

	
	@Test
	public void testMinCutGraph1() throws IOException{
		List<Edge> cutset = g.findMinCut();
		Assert.assertEquals("size of cutset is wrong.", 1, cutset.size());
		Edge e = cutset.get(0);
		Assert.assertEquals("Wrong edge was cut (wrong startnode).", 0, e.getStartnode().getID());
		Assert.assertEquals("Wrong edge was cut (wrong endnode).", 1, e.getEndnode().getID());
	}

	@Test
	public void testMinCutGraph2() throws IOException{
		List<Edge> cutset = g2.findMinCut();
		String msg = String.format("Number of edges cut expected: %d, but got %d", 2, cutset.size());
		Assert.assertEquals(msg, 2, cutset.size());
	}

 }







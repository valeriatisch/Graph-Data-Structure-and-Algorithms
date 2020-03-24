import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class EdmondsKarpTest
{
	
	private Network g1;
	
	@Test
	public void testEdmondsKarpGraph_pokalfinale(){
		try {
			g1 = GraphIO.loadGraph("tests/testgraphen/graph_pokalfinale.txt");
			g1.setShowSteps(true);

			VisualGraph v = new VisualGraph(g1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("Error Calculating Max Flow on Graph: graph_pokalfinale . ", 11, g1.edmondsKarp(), 0.01);
	}

 }



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Database {

	/**
	 * A mapping from the annotation to the WavFile
	 */
	List<Pair<String, ISignal>> database;
	

	public Database(String filename) throws IOException {		
		this.loadFromFile(filename);
	}
	
	/**
	 * Reads in a database file and stores all the Signals
	 * with the according labels 
	 * @param filename
	 * @throws IOException
	 * @return Number of entries read
	 */
	protected int loadFromFile(String filename) throws IOException {
		database = new LinkedList<Pair<String,ISignal>>();
		
		// opens a file
		// if this file was not found this method will exit
		// and an IOException will be thrown to the calling method
		BufferedReader br = new BufferedReader(new FileReader(filename));

		try {
			// the BufferedReader allows to read a line of
			// the file by calling the method readLine()
			// if the end of the file is arrived, readLine()
			// will return null
	
			String line = br.readLine();
			int lineNr = 0;
			while (line != null) {
				line = line.trim();
				if (line.length() > 0 && line.charAt(0) == '#') {
					// ignore comments
					line = br.readLine();
					lineNr++;
					continue;
				}
				
				String[] lineAsArray = line.split(" ");
				if (lineAsArray.length != 2) {
					throw new IOException("Error in line " + lineNr + ": expected two entries, got " + lineAsArray.length);
				}
				
				String path = lineAsArray[1];
				ISignal signal = new SignalFromWavFile(path);
				System.out.println("-----");
				database.add(new Pair<String,ISignal>(lineAsArray[0], signal));
				
				line = br.readLine();
				lineNr++;
			}
		} finally { //makes sure the file gets closed properly even in the event of an Exception
			br.close();
		}
		return database.size();
				
	}
	
	
	/**
	 * Common Method to compare two signals.
	 *
	 * @param two signals to compare
	 * @return Matcher object containing the result 
	 * 
	 * Use this method in lookup()!
	 * 
	 */
	protected IMatcher compare(ISignal x, ISignal y) {		
		IMatcher m = new Matcher(x, y);
		m.compute();
		return m;		
	}
	
	
	/**
	 * Returns the WavFile and the annotation with the shortest
	 * distance to the queried file 
	 * 
	 * @param query
	 * @return name of the match and the Matcher containing detailed results (e.g. distance, mapping path)
	 * 
	 *		the matcher has the query as left signal, and the database reference as the right signal
	 */
	public Pair<String, IMatcher> lookup(ISignal query) {

		if(query == null) return null;
		if (database == null) return null;
		if (database.size() == 0) return null;
		// TODO Your implementation here

		/* IMPORTANT: Use the method compare() to create Matcher objects, 
		 * and do not create them directly via "new Matcher()"!
		*/
				
		double distance = Double.MAX_VALUE;
		
		IMatcher bruecke = null;
		IMatcher Match = null;
		String signalName = null;
		
		/**
		 * Fuer jedes Signal in database:
		 * 		Vergleiche dessen Distanz mit der Anfrage (query).
		 * 			Wenn die Distanz groesser ist als die vorige,
		 * 				setze distance auf die neue Distanz und
		 * 				hole den Namen des Signals mit der kleinsten Distanz zur Anfrage.
		 * 
		 * Gib ein Pair mit dem Namen dieses Signals und deren Match zurueck.
		 */
		
		for (Pair<String, ISignal> signal : database) {
			bruecke = compare(query, signal.getRight());
			
			if (distance > bruecke.getDistance()) {
				distance = bruecke.getDistance();
				signalName = signal.getLeft();
				Match = compare(query, signal.getRight());
			}
		}
		
		return new Pair<String, IMatcher>(signalName, Match);
	}
	
}


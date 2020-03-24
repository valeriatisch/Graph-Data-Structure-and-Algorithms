
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the match of two wave files
 * 
 * Implements the comparison and matching methods according
 * to the optimal dynamic programming presented in the tutorial
 */
public class Matcher implements IMatcher {

	/**
	 * Signal X
	 */
	protected ISignal signalX;
	/**
	 * Signal Y
	 */
	protected ISignal signalY;

	
	/**
	 * The distance/dissimilarity score between
	 * the two wav files. The score is calculated in the
	 * compute method
	 * 
	 *  @see Matcher.compute
	 *  @see Matcher.computeDistance
	 */
	protected double distance = -1.0;

	/**
	 * The path of minimal distance between the two
	 * wave files.
	 * 
	 * The first integer of the pair denotes the
	 * frame number of WavFile w, the second integer
	 * the frame number of WavFile v
	 * 
	 * It will be stored here by the compute method
	 * 
	 * @see Matcher.compute
	 */
	protected List<Pair<Integer, Integer>> matchingPath = null;

	/**
	 * The accumulated distance/dissimilarity matrix
	 * will be stored here by the compute method
	 * 
	 * @see Matcher.compute
	 */
	protected double accumulatedDistance[][] = null;
	
	
		/**
	 * Construct a WavDistance object
	 *
	 * With this constructor, you can use signals defined by a buffer
	 * 
	 * For both passed ISignal buffers we have to check that
	 * they only have one channel and that they do not
	 * exceed the maximum number of frames MAX_FRAMES
	 * 
	 * @param dataX Array with signal values
	 * @param dataY Array with signal values
	 * @param sampleRate in samples per second
	 */
	public Matcher(double[] dataX, double[] dataY, int sampleRate) {
		this.signalX = new SignalFromBuffer(dataX, sampleRate);
		this.signalY = new SignalFromBuffer(dataY, sampleRate);
	}


	/**
	 * Construct a Matcher object
	 *
	 * With this constructor, you can use signals defined by a buffer
	 * 
	 * For both passed ISignal buffers we have to check that
	 * they only have one channel and that they do not
	 * exceed the maximum number of frames MAX_FRAMES
	 * 
	 * @param x 1st Object with ISignal interface
	 * @param y 2nd Object with ISignal interface
	 */
	public Matcher(ISignal x,ISignal y) {
		this.signalX = x;
		this.signalY = y;
	}
	
	/**
	 * Warps each signal to mimic the other one
	 *
	 * @return Pair of warped signals
	 * @throws IOException, WavFileException
	 */
	public Pair<ISignal, ISignal> warpSignals()  {
		
		 ISignal resultX = new SignalFromBuffer(matchingPath.size(), signalX.getSampleRate()); 
		 ISignal resultY = new SignalFromBuffer(matchingPath.size(), signalY.getSampleRate()); 
		
		//Construct signals:
		Iterator<Pair<Integer, Integer>> it = matchingPath.iterator();
		int indexX=0;
		int indexY=0;
		for (int matchcounter = 0; matchcounter < matchingPath.size(); matchcounter++) {
			Pair<Integer, Integer> pair = it.next();
			indexX = pair.getLeft();
			indexY = pair.getRight();
			resultX.setFrame(matchcounter, this.signalX.getFrame(indexX));
			resultY.setFrame(matchcounter, this.signalY.getFrame(indexY));
		}
		//trim the signal to the actual length of the signal:
		resultX.trimTo(indexX);
		resultY.trimTo(indexY);
		
		Pair <ISignal, ISignal> result = new Pair<ISignal, ISignal>(resultX, resultY);
		return result;
	}

	/**
	 * Returns the computed distance
	 * 
	 * @return Calculated distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns the computed mapping 
	 * 
	 * @return Calculated mapping
	 */
	public List<Pair<Integer, Integer>> getMappingPath() {
		return matchingPath;
	}

	/**
	 * 
	 * @return the computed mapping between the two signals as an array
	 * 		row: matches
	 * 		column: signals
	 */
	public int[][] getMappingPathAsArray() {
		int[][] array = new int[matchingPath.size()][2];
		int count = 0;
		for (Pair<Integer, Integer> pair : matchingPath) {
			array[count][0] = pair.getLeft();
			array[count][1] = pair.getRight();					
			count++;
		}
		return array;
	}

	
	/**
	 * Compute the distance score and the match between
	 * the two WavFiles
	 * 
	 * The result of this computation is stored in score
	 * 
	 * @throws WavFileException
	 */
	public void compute() throws RuntimeException {
		System.out.println("------");
		
		//Check size of the distance matrix to avoid bombing the memory heap
		Long heapsize = ((long)signalX.getNumFrames()) * ((long)signalX.getNumFrames()) * Double.SIZE / 1048576;
		System.out.println("Allocating " + heapsize.toString() + "MB");
		if (heapsize >  4000) {  // 4000 MB
			throw new RuntimeException("Signals are too long - I would gobble up too much memory when matching them!");
		} 
		
		// initialize the accumulated distance matrix properly
		initializeAccDistanceMatrix();
	
		// use dynamic programming to compute accumulated distance matrix 
		computeAccDistanceMatrix();
		// compute and store the distance score for the two files 
		computeDistance();
		// compute the mapping between the two files
		computeMatchingPath();
		
		System.out.println("Distance: " + distance);
		System.out.println("------");
		
	}
	
	/**
	 * Create an accumulated distance matrix for the wave file  
	 * and initialize correctly
	 * 
	 */
	protected void initializeAccDistanceMatrix() {
		// TODO Your implementation here
		
		/**
		 * Matrix sieht folgendermassen aus:
		 * 		xsize
		 * 	 0 ∞ ∞ ∞ ∞ ... 
		 * y ∞ 0 0 0 0
		 * s ∞ 0 0 0 0
		 * i ∞ 0 0 0 0
		 * z ∞ 0 0 0 0
		 * e .
		 *   .
		 *   .
		 * 
		 * xsize = #Frames vom Signal x + 1
		 * ysize = #Frames vom Signal y + 1
		 * 
		 */
		
		double infinity = Double.POSITIVE_INFINITY;
		
		int xsize = signalX.getNumFrames() + 1;
		int ysize = signalY.getNumFrames() + 1;
				
		accumulatedDistance = new double[xsize][ysize];
		
		this.accumulatedDistance[0][0] = 0.0;
		
		for(int x = 1; x < xsize; x++) {
			this.accumulatedDistance[x][0] = infinity;
		}
		
		for(int y = 1; y < ysize; y++) {
			this.accumulatedDistance[0][y] = infinity;
		}
		
		for(int x = 1; x <= signalX.getNumFrames(); x++) {
			for(int y = 1; y <= signalY.getNumFrames(); y++) {
				this.accumulatedDistance[x][y] = 0.0;
			}
		}
		
	}
	
	
	
	/**
	 * Read the frames of the two signals and uses them
	 * to compute the accumulated distance matrix
	 * {@link Matcher#getAccumulatedaccumulatedDistance()}.
	 *
	 * This method assumes that
	 * {@link Matcher#initializeAccaccumulatedDistance()} has already
	 * been called.
	 *
	 * @see initializeAccaccumulatedDistance
	 * @see computeDistance
	 * @see computeMatchingPath
	 */
	protected void computeAccDistanceMatrix() {
		if (this.accumulatedDistance == null) throw new RuntimeException("Called before initializing the distance matrix!");
		System.out.print("Computing...");
		// TODO Your implementation here
		
		/**
		 * Matrixberechnung:
		 * 		Wie Kreuzprodukt: jedes Frame von Signal x mit jedem von y
		 * 		Distanz zwischen einem Frame von x und einem von y (Differenz im Betrag) + die kleinste Distanz von den drei: (x,y-1), (x-1,y), (x-1,y-1)
		 */
		
		for(int x = 1; x <= signalX.getNumFrames(); x++) {
			for(int y = 1; y <= signalY.getNumFrames(); y++) {
				this.accumulatedDistance[x][y] = Math.min(this.accumulatedDistance[x-1][y-1], Math.min(this.accumulatedDistance[x][y-1], this.accumulatedDistance[x-1][y]))
						+ Math.abs(signalX.getFrame(x-1) - signalY.getFrame(y-1));
			}
		}
		
		System.out.println("done");		
	}
	
	/**
	 * Computes the minimum distance of two signals saved within the computed
	 * accumulated distance matrix. The distance can be retrieved with
	 * {@link Matcher#getDistance()}.
	 *
	 * This method assumes that {@link Matcher#computeAccaccumulatedDistance()} has
	 * already been called.
	 *
	 * @see computeAccumulatedDistance
	 */
	protected void computeDistance() throws RuntimeException {
		if(this.accumulatedDistance == null) {throw new RuntimeException("Called before computing the distance matrix!");}
		// TODO Your implementation here
		
		//Die Zahl ganz rechts unten in der Matrix ist die gesuchte Distanz.
		distance = this.accumulatedDistance[signalX.getNumFrames()][signalY.getNumFrames()];
		
	}
	
	/**
	 * Computes the matching path of two signals with the following criteria:
	 *
	 * 1. The path is a LinkedList of Pairs of frame indexes: [ (x_i1, y_i1),
	 * ..., (x_in, y_in) ]
	 *
	 * 2. The path is as short as possible: If we have the opportunity to go
	 * diagonally, we should do this (i.e. take the lexicographically smallest
	 * pair).
	 *
	 * 3. The indexes contained in the Pairs correspond to the indexes of the
	 * frames, i.e. they range from 0 to (fileSize-1) of the respective files.
	 *
	 * 4. The path is in ascending order.
	 *
	 * 5. The path can be retrieved with {@link Matcher#getMappingPath()}.
	 *
	 * This method assumes that {@link Matcher#computeAccaccumulatedDistance()} has
	 * already been called.
	 *
	 * @see computeAccaccumulatedDistance
	 */
	protected void computeMatchingPath() throws RuntimeException {
		if(this.accumulatedDistance == null) {throw new RuntimeException("Called before computing the distance matrix!");}		
		System.out.print("Computing mapping path...");		
		// TODO Your implementation here
		
		matchingPath = new LinkedList<Pair<Integer, Integer>>();
		
		List<Pair<Integer, Integer>> hilfsPath = new LinkedList<Pair<Integer, Integer>>();
		
		int x = signalX.getNumFrames();
		int y = signalY.getNumFrames();
		
		/**
		 * Fange ganz rechts unten in der Matrix an und gehe bis nach ganz oben links (also bis sowohl x als auch y 0 sind)
		 * und nehme immer das kleinste Element zwischen den drei: (x,y-1), (x-1,y), (x-1,y-1) und fuege es in mein hilfsPath hinzu.
		 * Wenn das Element auf (x-1,y-1) gleich den beiden anderen ist, waehle es (da die Hypotenuse zu laufen schneller geht).
		 */
		while(x != 0 && y != 0) {
			hilfsPath.add(new Pair<>(x-1, y-1));
			if(this.accumulatedDistance[x-1][y-1] == Math.min(this.accumulatedDistance[x][y-1], this.accumulatedDistance[x-1][y]) ||
					this.accumulatedDistance[x-1][y-1] < Math.min(this.accumulatedDistance[x][y-1], this.accumulatedDistance[x-1][y]) ) {
				x--;
				y--;
			}
			else if(this.accumulatedDistance[x-1][y] == Math.min(this.accumulatedDistance[x-1][y-1], Math.min(this.accumulatedDistance[x][y-1], this.accumulatedDistance[x-1][y]))) {
				x--;								
			}
			else if(this.accumulatedDistance[x][y-1] == Math.min(this.accumulatedDistance[x-1][y-1], Math.min(this.accumulatedDistance[x][y-1], this.accumulatedDistance[x-1][y]))) {
				y--;								
			}
		}
		
		//Den gesuchten Pfad in matchingPath in der richtigen Reihenfolge speichern.
		for(int i = hilfsPath.size() - 1; i >= 0; i--) {
			matchingPath.add(hilfsPath.get(i));
		}
		
		System.out.println("done");
	}
	
}


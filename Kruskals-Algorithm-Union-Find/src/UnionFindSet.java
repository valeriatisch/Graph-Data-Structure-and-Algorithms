import java.util.HashMap;
import java.util.List;

/**
 * A class implementing a Union-Find-data structure with representatives.
 * 
 * @author AlgoDat-Team
 */
public class UnionFindSet<T> {

    // You can use this map to store the representant for each element of the Union
    // find structure
    private HashMap<T, T> element2representative;

    public UnionFindSet() {
	element2representative = new HashMap<>();
    }

    /**
     * Takes a collection of n elements and adds n disjoint partitions each with one
     * element in it.
     * 
     * @param set
     *            The set to be partitioned.
     */
    public void add(List<T> elements) {
	// TODO Homework 2.1
    //Erstelle n Partitionen aus n Elementen in der Liste elements
    for (T element : elements)
	    this.element2representative.put(element, element);
    }
    /**
     * Creates one disjoint partition with the element in it and adds it to the
     * Union-Find data structure
     * 
     * @param element
     *            The element to put in the partition.
     */
    public void add(T element) {
	// TODO Homework 2.1
	//Erstelle neue Partition
	this.element2representative.put(element, element);
    }

    /**
     * Retrieves the partition which contains the wanted element.
     * 
     * A partition is identified by a single, representative element. This function
     * returns the representative of the partition that contains x.
     * 
     * @param x
     *            The element whose partition we want to know
     * @return The representative element of the partition
     */
    public T getRepresentative(T x) {
	// TODO Homework 2.1
	//Suche die Partition mit x und gib deren Repraesentanten zurueck
	return this.element2representative.get(x);
    }

    /**
     * Joins two partitions. First it retrieves the partitions containing the given
     * elements. Removes one of the joined partitions from <code>partitions</code>.
     * 
     * @param x
     *            One element whose partition is to be joined.
     * @param y
     *            The other element whose partition is to be joined.
     */
    public void union(T x, T y) {
	// TODO Homework 2.1
	//Wenn Partitionen gleich bzw. Repraesentanten, dann fertig
	if (this.getRepresentative(x).equals(this.getRepresentative(y))) {
	    return;
	}

	/**
	 * Wenn x und y nicht den gleichen Repraesentanten/Partitionen haben, gehe die Hashmap durch
	 * und suche das Element, das den gleichen Repraesentanten hat wie y. Ersetze
	 * den Repraesentanten von diesem Element bzw. von y mit dem von x.
	 */
		
	T repY = this.getRepresentative(y);
	T repX = this.getRepresentative(x);

	if (getRepresentative(x) != getRepresentative(y)) {
		//praise this this.
	    for (T u : this.element2representative.keySet()) {
		T repU = this.getRepresentative(u);

		if (repU == repY) {
		    this.element2representative.put(u, repX);
		}
	    }
	}

    }
}

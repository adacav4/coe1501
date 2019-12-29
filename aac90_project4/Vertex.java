import java.util.*;

public class Vertex {
	private LinkedList<Edge> edges;		// Number of edges the vertex has
	private int vid; 	// Vertex ID

	// Constructor
	public Vertex(int id) {
		vid = id;
		edges = new LinkedList<Edge>();
	}

	// Get's vertex ID
	public int getVID() {
		return vid;
	}

	// Enumerates the vertex's edges
	public LinkedList<Edge> enumerateEdges() {
		return edges;
	}
}
public class Edge {
	private String type; 		// Type of wire
	private int bandwidth; 		// Bandwidth of edge
	private int length; 		// Length of edge
	private Vertex source; 		// Starting vertex
	private Vertex destination; // Destination vertex
	private double edgeTime; 	// Time to send info on this edge

	// Constructor
	public Edge(String t, int bw, int len, Vertex sc, Vertex dest) {
		type = t;
		bandwidth = bw;
		length = len;
		source = sc;
		destination = dest;

		// Get's edge time in nanoseconds for both materials
		if (type.equals("copper")) {
			edgeTime = length * ((double) 1/230000000) * Math.pow(10, 9);
		}
		else if (type.equals("optical")) {
			edgeTime = length * ((double) 1/200000000) * Math.pow(10, 9);
		}
	}

	// Getters
	public String getType() {
		return type;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public int getLength() {
		return length;
	}

	public Vertex getSource() {
		return source;
	}

	public Vertex getDestination() {
		return destination;
	}

	public double getEdgeTime() {
		return edgeTime;
	}

	// Setters
	public void setType(String newType) {
		type = newType;
	}

	public void setBandwidth(int newBw) {
		bandwidth = newBw;
	}

	public void setLength(int newLen) {
		length = newLen;
	}

	public void setSource(Vertex newSource) {
		source = newSource;
	}

	public void setDestination(Vertex newDest) {
		destination = newDest;
	}

	public void setEdgeTime(double newET ) {
		edgeTime = newET;
	}
}
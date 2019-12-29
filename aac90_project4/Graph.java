import java.util.*;
import java.io.*;

public class Graph {
	private int nVert; 								// Number of vertices
	private Vertex[] vertices; 						// Adjacency list
	private ArrayList<Edge> edges; 					// Keep track of all edges
	private ArrayList<Edge> minSpanTreeVertices;	// Stores the vertices in the min spanning tree
	private double lowAvgLatency; 					// Lowest average latency for min spanning tree
	private boolean copperConnected = true; 		// If copper-only connected

	// Constructor
	public Graph(String nFile) {
		minSpanTreeVertices = null;			// Initialize to null for now
		edges = new ArrayList<Edge>();	// Create new empty list for enumerating edges later

		if (nFile == null) {	// Invalid file
			return;
		}

		// Set up I/O for file reading
		File f = new File(nFile);	// New file
		Scanner s;	// Scanner for file reading
		
		try {	// Catch invalid file name
			s = new Scanner(f);
		} catch(FileNotFoundException e) {
			System.out.println("Invalid File name");
			return;
		}

		nVert = Integer.parseInt(s.nextLine());		// Read in number of vertices
		vertices = new Vertex[nVert]; 				// Initialize Adjacency list and load it with empty vertices
		for (int i = 0; i < vertices.length; i++){
			vertices[i] = new Vertex(i);
		}

		while (s.hasNextLine()) { 	// Load file into graph
			String input = s.nextLine();
			String[] connection = input.split(" ");		// Get each part of the network connection

			// Set up vertices
			Vertex source = vertices[Integer.parseInt(connection[0])];	// Gets starting vertex
			Vertex dest = vertices[Integer.parseInt(connection[1])];	// Gets destination vertex
			
			String type = connection[2];	// Gets material type of connection
			if (type.equals("optical")) {	// Check if wire is copper or not
				copperConnected = false;	// If even one wire is optical, it is not copper only
			}

			int bw = Integer.parseInt(connection[3]); 		// Maximum amount of data that can travel along this edge
			int len = Integer.parseInt(connection[4]); 	// Length of the edge

			// Set up forward and backward edges
			Edge sd = new Edge(type, bw, len, source, dest);	// Forward edge
			Edge ds = new Edge(type, bw, len, dest, source);	// Backwards edge
			source.enumerateEdges().addFirst(sd);		// Add to front of list
			dest.enumerateEdges().addFirst(ds);			// Add to front of list
			edges.add(sd); 	// Add forward edge to min spanning tree
		}
	}

	public void lowestLatency() {
		Scanner s = new Scanner(System.in);		// Set up new scanner for more user input
		Vertex start = null;		// Initialize to null for now
		Vertex end = null;			// Initialize to null for now

		// I/O for finding the lowest latency path
		System.out.print("Enter the ID of the first vertex: ");		// Catch invalid VID
		int vid1 = Integer.parseInt(s.nextLine());	// Initialize to 0
		while (vid1 < 0 || vid1 >= nVert) {
			System.out.println("\nInvalid vertex.\n");
			System.out.print("Enter the ID of thefirst vertex: ");		// Catch invalid VID
			vid1 = Integer.parseInt(s.nextLine());
		}
		start = vertices[vid1];	// Set starting vertex

		System.out.print("Enter the ID of the second vertex: ");	// Catch invalid VID
		int vid2 = Integer.parseInt(s.nextLine());;	// Initialize to 0
		while (vid2 < 0 || vid2 >= nVert || start.getVID() == vid2) {
			System.out.println("\nInvalid vertex.\n");
			System.out.print("Enter the ID of the second vertex: ");	// Catch invalid VID
			vid2 = Integer.parseInt(s.nextLine());
		}
		end = vertices[vid2];	// Set ending vertex

		// Store data in an object array to return different variable types all at once (helper method)
		Object[] pathInfo = shortestPath(start, end, "" + start.getVID(), 0L, -1);

		if (pathInfo == null) {	// If null, the graph could not return any path at all, so return
			return;
		}

		String p = (String) pathInfo[0];		// Get path string from pathInfo
		String dPath = "";		// Initialize the directed path to an empty string
		for (int i = 0; i < p.length(); i++) {	// Make the path string pretty for printing out
			if (i < p.length() - 1) {		// If not the end of the string, add an arrow pointing to the next vertex
				dPath += p.charAt(i) + " ---> ";
			}
			else {		// Just add the last vertex if the end of the string
				dPath += p.charAt(i);
			}
		}
		p = dPath;	// Set path as the new directed path
		double pathEdgeTime = (double) pathInfo[1]; // Set latency of path
		int pathBw = (int) pathInfo[2]; 	// Set bandwidth
		
		System.out.println("\nShortest path: " + p + "\nLatency: " + pathEdgeTime+ " ns\nBandwidth: " + pathBw + " Mbps\n");
	}

	// Helper method for method above to find the shortest path with the lowest latency
	private Object[] shortestPath(Vertex cur, Vertex dest, String path, double length, int bw) {
		if (cur == null || dest == null || path == null || length < 0.0) {		// If cur is null, return
			return null;
		}

		if (cur == dest) { 		// The destination vertex is found, so return the pathInfo back
			return new Object[] {path, length, bw};
		}

		double minL = -1.0; 	// Initialize to -1 for now
		String minP = "";		// Initialize to an empty string for now
		LinkedList<Edge> enumEdges = cur.enumerateEdges(); // Get a list of all the current vertex's edges

		for (Edge e : enumEdges) { 		// Loop through all the enumerated edges
			Vertex edgeDest = e.getDestination();	// Get the destination vertex of current edge
			if (path.contains("" + edgeDest.getVID())) {	// If already visited, go to the next vertex
				continue;
			}

			String newP = path + edgeDest.getVID(); // Append to the old path
			double newL = 0.0;		// Initialize to 0 for now
			if (e.getType() == null) {	// If the edge wire is null, there is no way to calculate length
				return null;
			}
			else {		// If there is a type, then append to newL
				newL = length + e.getEdgeTime();
			}

			int newBw = bw; 	// New bandwidth var
			if (bw == -1.0 || e.getBandwidth() < bw) {	// If less than old bandwidth or not been set yet, set the new bandwidth
				newBw = e.getBandwidth();
			}

			Object[] pathInfo = shortestPath(edgeDest, dest, newP, newL, newBw); // Recurse to the destination vertex
			if (pathInfo == null) {		// If pathInfo is null, go to the next edge
				continue;
			}

			String edgePath = (String) pathInfo[0];
			double pathL = (double) pathInfo[1];
			int pathBw = (int) pathInfo[2];

			if (minL == -1 || pathL < minL) { 	// Set new pathInfo
				minL = pathL;
				minP = edgePath;
				bw = pathBw;
			} 
			else if (pathL == minL && pathBw > bw) { 	// Same pathInfo but we have better bandwidth
				minL = pathL;
				minP = edgePath;
				bw = pathBw;
			}
		}

		if (minL > -1.0) { 		// Recursive case for when a path is found, so return pathInfo
			return new Object[] { minP, minL, bw };
		}

		return null; 	// No path found at all, so return null
	}

	// Sees if the graph is made up of copper only wires
	public void copperOnlyConnected() {
		// Determined in constructor when graph is created
		if (copperConnected) { 		// The graph is copper-only connected
			System.out.println("This graph is copper-only connected.\n");
		}
		else {		// The graph has at least one optical cable connection
			System.out.println("This graph is NOT copper-only connected.\n");
		}
	}

	// Determines if the graph still has a valid path(s) if any two vertices fail
	public void vertexFailConnectivitiy() {
		// For each vertex, look at all possible connecting vertices (nested for loop)
		Vertex f1 = null;	// Initialize to null
		Vertex f2 = null;	// Initialize to null
		for (int i = 0; i < nVert - 1; i++) {
			for (int j = i + 1; j < nVert; j++) {
				Vertex start = null;		// Starting vertex	
				f1 = vertices[i];	// First failed vertex
				f2 = vertices[j];	// Second failed vertex
				boolean[] visited = new boolean[nVert];		// Keep track of visited vertices

				visited[f1.getVID()] = true;		// Don't visit these two vertices
				visited[f2.getVID()] = true;		// since they both failed

				if (i == 0) { 		// First vertex failed, so don't start search at 0
					start = vertices[0];		// Initialize starting vertex (changes later, or fails)
					if (j != nVert - 1) {
						start = vertices[j + 1];	// Assign start to vertex i's (j + 1) vertex
					}
					else if (j - i != 1) {
						start = vertices[j - 1];	// Assign start to vertex i's (j - 1) vertex
					}
					else {		// No possible path
						System.out.println("There is no valid path when any two vertices fail.\n");
						return;
					}
				} 
				else { 
					start = vertices[0];		// If 0 didn't fail, start search from there
				}

				// Call helper method to search for a path (will return false if no path)
				findPath(start, f1, f2, visited);

				// Loop through visited vertices to see if there is a path
				for (int k = 0; k < visited.length; k++) {		// Loop through visited array
					if (visited[k] == false) {		// If helper method fails, then there is no path
						System.out.println("There is no valid path when any two vertices fail.\n");
						return;
					}
				}
			}
		}
		// Valid path is found
		System.out.println("There is a valid path when any two vertices fail.\n");
		return;
	}

	// Helper method for method above to search for a connection if two vertices fail
	private void findPath(Vertex cur, Vertex start, Vertex end, boolean[] visited) {
		if (cur == null || start == null || end == null || visited == null) {	// If node is invalid, return
			return;
		}

		if (visited[cur.getVID()] == true){ // If already visited 
			return;
		}	
		LinkedList<Edge> enumEdges = cur.enumerateEdges();	// Get a list of edges for the current vertex
		visited[cur.getVID()] = true; 	// Set current vertex as visited
		
		// DFS through all edges to find a valid path
		for (Edge e : enumEdges) { 		// Enhanced for loop for all the edges of the current vertex
			Vertex dest = e.getDestination(); 		// Find the destination vertex for the current edge
			if (visited[dest.getVID()] == true) {	// If already visited, skip the vertex
				continue;
			}
			findPath(dest, start, end, visited); // Recurse to each vertex
		}
		return;
	}

	// Gets the lowest average latency spanning tree
	public void lowAvgLatencySTree(){
		// Use Kruskals alg to find the pairs of vertices that form the min spanning tree
		lowAvgLatency = kruskals() / minSpanTreeVertices.size(); 	// Get lowest average latency for min spanning tree
																	// (weight of min spanning tree / number of edges in min spanning tree)
		System.out.println("Lowest Average Latency Spanning Tree Edges:\n");
		
		for (Edge e : minSpanTreeVertices) {	// Loop through edges in the spanning tree and print
			System.out.print("[" + e.getSource().getVID() + " , " + e.getDestination().getVID() + "]\t");
		}
		System.out.println();
		System.out.println("\nThe average latency of the spanning tree is: " + lowAvgLatency + " ns.\n");
	}

	// Kruskal's algorithm from textbook adapted for this project (helper method for method above)
	private double kruskals() {
		int[] v = new int[nVert];			// List of vertices
        byte[] rank = new byte[nVert];		// List of ranks associated with those vertices
        for (int i = 0; i < nVert; i++) {	// Initialize the VID's and ranks for each vertex
            v[i] = i;
            rank[i] = 0;
        }

		minSpanTreeVertices = new ArrayList<Edge>(); 	// List of edges in min spanning tree (empty for now)
		double w = 0.0; 	// Weight of min spanning tree initialized to 0
		int minW = 0; 		// The current minimum weight (looping through graph to find the min weight)

		// Loop through the entire graph to find the minimum weight of the min spanning tree
        while (minW != edges.size() - 1 && minSpanTreeVertices.size() < nVert - 1) {
            Edge e = edges.get(minW);		// Find the edge with the minimum weight
            int source = e.getSource().getVID();
            int dest = e.getDestination().getVID();
            
            if (!connected(source, dest, v)) { 		// If not connected, union the two vetices
                union(source, dest, v, rank);		// Union the two vertices into the min spanning tree
                minSpanTreeVertices.add(e);
                w += e.getEdgeTime();
            }
			
			minW++; // Incrementing this and looping back looks at the next edge
        }
		return w;
	}

	// Taken from textbook and adapted for this project (helper method for kruskals())
    private void union(int p, int q, int[] v, byte[] rank) {
        int pID = find(p, v);
        int qID = find(q, v);
        
        if (pID == qID) {	// If ID's match, it's already in union with each other
        	return;
        }

        // Set ID's based off of the higher ID number when putting two vertices in union
        if (rank[pID] < rank[qID]) {
        	v[pID] = qID;
        }
        else if (rank[pID] > rank[qID]) {
        	v[qID] = pID;
        }
        else {
            v[qID] = pID;
            rank[pID]++;
        }
    }

	/// Taken from textbook and adapted for this project (helper method for kruskals())
	private boolean connected(int p, int q, int[] v) {
        // If returns the same ID, they are connected
        return find(p, v) == find(q, v);
    }

	// Taken from textbook and adapted for this project (helper method for kruskals())
	private int find(int p, int[] v) {
        // Loops through vertices to find a match, and then returns the ID number of the found vertex
        while (p != v[p]) {
            v[p] = v[v[p]];  
            p = v[p];
        }
        return p;
    }
}
import java.util.*;

public class NetworkAnalysis{
	public static void main(String[] args){
		if (args.length != 1) {		// Check for invalid number of arguments
			System.out.println("Invalid arguments");
		}
		else {
			Graph g = new Graph(args[0]);		// New graph object with file
			Scanner s = new Scanner(System.in);		// Scanner for input

			System.out.println("Welcome to the Network Analyzer\n");
			while (true) {		// Loops until user quits

				// I/O for menu
				System.out.println("1. Find the graph's lowest latency path");
				System.out.println("2. Determine whether or not the graph's is copper-only connected");
				System.out.println("3. Find the graph's lowest average latency spanning tree");
				System.out.println("4. Determine if the graph remains connected if any two vertices fail");
				System.out.println("5. Quit");
				
				System.out.print("\nEnter a number 1-5: ");
				String input = s.nextLine();	// Get the user input

				System.out.println();
				if (input.equals("1")) {
					g.lowestLatency();				// Get lowest latency path
				}
				else if (input.equals("2")) {
					g.copperOnlyConnected();		// If copper only connected or not
				}
				else if (input.equals("3")) {
					g.lowAvgLatencySTree();			// Get the lowest average latency Spanning tree
				}
				else if (input.equals("4")) {
					g.vertexFailConnectivitiy();	// If vertecies fail, is it still connected
				}
				else if (input.equals("5")){			// Quit
					System.out.println("Exiting the Network Analyzer\n");
					System.exit(0);
				}
				else{
					System.out.println("Invalid option! Choose again.\n");
				}
			}
		}
	}
}
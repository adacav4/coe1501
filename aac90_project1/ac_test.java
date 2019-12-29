import java.util.*;
import java.io.*;

public class ac_test {
	public static Scanner f;	// File Scanner
	public static Scanner s;	// User input Scanner
	public static File histFile;
	public static FileWriter fw;
	public static DLB<Character, String> dict;
	public static DLB<Character, String> hist;
	public static String[] results;
	public static StringBuilder typedWord;

	public static void main(String[] args) throws IOException {
		String input = "";
		String chosenWord;
		boolean notFinished = true;
		double beforeTime = 0;
		double afterTime = 0;
		double totalTime = 0;
		int count = 0;
		boolean wordsFound = false;
		dict = TrieMethods.genDict();	// Generate the dictionary using genDict()
		hist = new DLB<Character, String>('~', "~");	// create new DLB with random params for root node
		typedWord = new StringBuilder();
		results = new String[5];

		histFile = new File("user_history.txt");	// Loads user history file if it exists
		fw = new FileWriter(histFile, true);
		f = new Scanner(histFile);			// Scanner to load user history into DLB
		s = new Scanner(System.in);			// User input

		while (f.hasNextLine()) {
			TrieMethods.setWord(hist, f.nextLine());	// Loads history text file
		}

		System.out.print("Enter a character: ");
		while (!(input.equals("!"))) {
			count++;
			input = s.nextLine();
			if (input.equals("1")) {
				System.out.println("WORD COMPLETED: " + results[0]);
				TrieMethods.setWord(hist, results[0]);	// Adds completed word to user history trie (same for other options)
				fw.write(results[0] + "\n");		// Writes completed word to user_history.txt (same for other options)
				typedWord.setLength(0);				// Clears the StringBuilder (same for other options)
				wordsFound = true;		// Used to mark word as completed and found (same for other options)
			}
			else if (input.equals("2")) {
				System.out.println("WORD COMPLETED: " + results[1]);
				TrieMethods.setWord(hist, results[1]);
				fw.write(results[1] + "\n");
				typedWord.setLength(0);
				wordsFound = true;
			}
			else if (input.equals("3")) {
				System.out.println("WORD COMPLETED: " + results[2]);
				TrieMethods.setWord(hist, results[2]);
				fw.write(results[2] + "\n");
				typedWord.setLength(0);
				wordsFound = true;
			}
			else if (input.equals("4")) {
				System.out.println("WORD COMPLETED: " + results[3]);
				TrieMethods.setWord(hist, results[3]);
				fw.write(results[3] + "\n");
				typedWord.setLength(0);
				wordsFound = true;
			}
			else if (input.equals("5")) {
				System.out.println("WORD COMPLETED: " + results[4]);
				TrieMethods.setWord(hist, results[4]);
				fw.write(results[4] + "\n");
				typedWord.setLength(0);
				wordsFound = true;
			}
			else if (input.equals("$")) {
				System.out.println("WORD COMPLETED: " + typedWord.toString());
				TrieMethods.setWord(hist, results.toString());
				fw.write(typedWord.toString() + "\n");
				typedWord.setLength(0);
				wordsFound = true;
			}
			else if (input.equals("!")) {
				TrieMethods.setWord(hist, results.toString());
				fw.write(typedWord.toString() + "\n");
				typedWord.setLength(0);
				break;
			}
			// Prevents it from appending other characters to typedWord
			if (!input.equals("1") && !input.equals("2") && !input.equals("3") && !input.equals("4") && !input.equals("5") && !input.equals("$")) {
				typedWord.append(input);
			}
			beforeTime = System.nanoTime();
			searchTries();
			afterTime = System.nanoTime();
			totalTime += (afterTime - beforeTime);
			System.out.println("(" + (afterTime - beforeTime) / 1000000000.0 + " s)\n");
			for (int i = 0; i < 5; i++) {
				if (results[i] != null) {	// Prevents null predictions from printing
					System.out.print("[" + (i + 1) + "]: " + results[i] + "\t");
					wordsFound = true;
				}
			}
			if (!wordsFound) {	// If the word is not found yet, and trie predictions run out, print this
				System.out.println("No words found. Continue to type your word\n");
			}
			System.out.print("\nEnter a character: ");
			wordsFound = false;	// Resets variable for next word (if found, otherwise there is no change)
		}
		fw.flush();
		fw.close();
		System.out.println("\nAverage time: " + ((totalTime / (double) count)) / 1000000000 + " s\nBye!");
		System.exit(0);
	}

	// Method to search through both the user history trie and the dictionary trie and store predictions in results
	private static void searchTries() {
		int cur = 5;		// Keeps track of how many words have been found
		boolean foundNode = false;		// True if found the node to search
		Node<Character, String> u = hist.getRoot();		// Gets user history DLB
		
		// Searching through user history (first priority)
		for (int i = 0; i < typedWord.length(); i++){		// Iterate through each char to find the node with key = last char of word
			while (u.hasKey() && typedWord.charAt(i) != u.getKey() && u.hasRight()) {
				u = u.getRight();	// Look through the right node list until node is found
			}
			if (u.hasKey() && u.getKey() == typedWord.charAt(i) && u.hasChild()) {		// Set u to child node (found the node)
				u = u.getChild();
				if (i == typedWord.length() - 1) {
					foundNode = true;	// Now that we found the node we can find the words that branch off this node
				}
			}
		}
		if (u.hasKey() && foundNode) {	// If found the node to search, then search hist trie for words. If not, skip on to dict DLB
			cur = TrieMethods.findWords(u, cur, results);
		}
		foundNode = false;		// Reset value for dictionary trie

		// Searching through dictionary (second priority)
		// See comments for user history above, however stop once you reach a total of 5 words (from both hist and dict tries)
		Node<Character, String> d = dict.getRoot();		// Gets dictionary DLB
		for (int i = 0; i < typedWord.length(); i++) {
			while (d.hasKey() && typedWord.charAt(i) != d.getKey() && d.hasRight()) {
				d = d.getRight();
			}
			if (d.hasKey() && d.getKey() == typedWord.charAt(i) && d.hasChild()) {	
				d = d.getChild();
				if (i == typedWord.length() - 1) {
					foundNode = true;	// Now that we found the node we can find the words that branch off this node
				}
			}
		}
		if (d.hasKey() && foundNode) {
			cur = TrieMethods.findWords(d, cur, results);	// If found the node to search, then search dict trie for words.
		}
		for (int i = 0; i < cur; i++) {
			results[5 - cur + i] = null;	// If we didn't find any words, cur = 5, so then it just iterates through the results array
		}									// If we found words, iterate from i = 0 to cur = 0 so nothing happens
	}
}
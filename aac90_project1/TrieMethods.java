// Methods for specifically DLB<Character, String> type only
// And a method to add the dictionary DLB for ac_test.java

import java.util.*;
import java.io.*;

public class TrieMethods {
	public static Scanner f;
	public static File dictionary;

	// Generates the Dictionary DLB
	public static DLB<Character, String> genDict() {
		DLB<Character, String> dTrie = new DLB<Character, String>('~', "~");	// Creates a new root node with arbitrary key/val
		try {	
			dictionary = new File("dictionary.txt");		// Loads dictionary into File object
			f = new Scanner(dictionary);
		} catch(FileNotFoundException e) {
			System.out.println("File not found");	// If file not found, exit
			System.exit(1);
		}
		while (f.hasNextLine()) {			// Loads the dictionary words into the DLB line by line 
			setWord(dTrie, f.nextLine());
		}
		return dTrie;
	}

	// Sets the node passed in as a terminal node
	public static Node<Character, String> setLastNode(Node<Character, String> n, String word) {
		n.setKey('^');
		n.setVal(word);		// Sets the value of the terminal node as the word passed in for easy searching
		return n;
	}

	// Returns true if the node passed in is a terminal node and false if not
	public static boolean isLastNode(Node<Character, String> n) {
		if (n.getKey().equals('^')) {
			return true;
		}
		else {
			return false;
		}
	}

	// This method is looped over the length of the word to set each char of the word in the trie
	private static Node<Character, String> setChar(char c, Node<Character, String> cur) {
		if (!cur.hasKey()) {		// Set passed char as the key
			Node<Character, String> tempChild = new Node<Character, String>();		// Create new node as set as child node
			cur.setChild(tempChild);
			cur.setKey(c);
			cur = cur.getChild();	// Set cur as child
		}
		else if (cur.hasRight()) {		// If cur has a right node set cur to it
			cur = cur.getRight();
			cur = setChar(c, cur);		// Recurse to the right node
		}
		else if (cur.getKey() == c) {	// If cur key matches the char, set cur to the child node
			cur = cur.getChild();
		}
		else {
			Node<Character, String> tempRight = new Node<Character, String>();		// Create new node and set as right node
			cur.setRight(tempRight);
			cur = cur.getRight();
			cur = setChar(c, cur);
		}
		return cur;
	}

	// Iterates through the trie per char of the passed word
	public static void setWord(DLB<Character, String> trie, String word) {
		Node<Character, String> cur = trie.getRoot();	// Start at root node
		for (int i = 0; i < word.length(); i++) {
			cur = setChar(word.charAt(i), cur);
		}	
		setLastNode(cur, word);		// Adds the terminal node to the end of the string
	}

	// Recurses through the trie to find ALL words branching from prefix node passed in
	public static int findWords(Node<Character, String> n, int i, String[] results) {
		// Keep track of how many predictions found with int i
		if (i > 0 && n.hasKey()) {		//
			if (n.hasChild()) {
				i = findWords(n.getChild(), i, results);	// Recursive search of the child node
				if (i == 0) {		// Found 5 words, so no need to search anymore
					return i;
				}
			}
			if (isLastNode(n)) {
				results[results.length - i] = n.getVal(); // Add word to index of the results array
				i--;
			}	
			if (n.hasRight()) {
				i = findWords(n.getRight(), i, results);	// Recursive search of the right node
				if (i == 0) {
					return i;
				}
			}
		}
		return i;	// cur = 5 if no words found
	}
}
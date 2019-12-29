// DLB to store different makes and models

import java.text.StringCharacterIterator;

public class MakeModelDLB{
	private Node root;

	// Insert a make and model string into the DLB
	public boolean insert(String name, CarPriorityQueue carPQ){
		StringCharacterIterator i = new StringCharacterIterator(name);	// New iterator for make model name
		if (root == null) { 	// First entry into the DLB
			root = new Node(i.current());	// Set root = new node
			Node cur = root;	// Set a cur node as root
			i.next();
			// Create children until string is complete, since it's the first entry
			while (i.getIndex() < i.getEndIndex()) { 	// Iterate through the make and model string
				Node newNode = new Node(i.current());
				cur.setChild(newNode);
				cur = cur.getChild();
				i.next();
			}
			cur.setChild(new Node('^'));	// Set child of entered name as the terminal node
			if (carPQ != null){ 			// If carPQ is not null set the car in the terminal node
				cur = cur.getChild();
				cur.setPQ(carPQ);
			}
		}
		else {	// If there are previous entries in the DLB
			Node cur = root;	// Set cur = root
			while (i.getIndex() < i.getEndIndex()) { 	// Iterate through the make and model string
				while (i.current() != cur.getVal()) {
					if (cur.getSibling() == null) {	// Check all sibling nodes for a match 
						Node newNode = new Node(i.current());
						cur.setSibling(newNode);		// If no match found, set a newNode as its sibling
						cur = cur.getSibling();			// set cur to be the new sibling
						break;
					}
					cur = cur.getSibling();		// If match found, set cur as the sibling
				}
				if (cur.getChild() == null) {		// If no children, create a child node
					Node newNode = new Node(i.current());
					cur.setChild(newNode);
				}
				cur = cur.getChild();	// Set cur as the next child
				i.next();	
			}
			cur.setVal('^'); 		// After iterating through string, set the last node as the terminal node
			if (carPQ != null){ 	// If name was stored properly, set the car in the terminal node
				cur.setPQ(carPQ);
			}
		}
		return true;
	}

	// Check if a make and model string exists in the DLB
	public boolean exists(String name) {
		if (root == null) {		// If root is null, DLB is empty
			return false;
		}
		StringCharacterIterator i = new StringCharacterIterator(name);	// New iterator for make and model string
		Node cur = root;	// Set a new cur as root
		while (i.getIndex() < i.getEndIndex()) { 	// Iterate through the make and model string
			if (cur == null) { 		// If null, make and model is not in the DLB
				return false;
			}
			while (i.current() != cur.getVal()) { // Go through the siblings to see if there's a match
				if (cur.getSibling() == null) {
					return false; 			// There is no sibling so make and model is not in the DLB
				}
				cur = cur.getSibling();		// Otherwise get the sibling, if there is a match
			}
			cur = cur.getChild();	// Get next child
			i.next();
		}
		if (cur == null) { // If null, we did not find the make and model
			return false;
		} 
		else if (cur.getVal() == '^') {	// If terminal node, we found the make and model
			return true;
		}
		while(cur.getSibling() != null) { // Go through all the siblings to see if there is a terminal node
			if (cur.getVal() == '^') {	 // If we found the terminal node, we're done
				return true;
			} 
			cur = cur.getSibling();	// Otherwise get sibling
		}
		return false;
	}

	// Get the PQ stored at the terminal node of the entry (in the DLB)
	public CarPriorityQueue getPQ(String name){
		if (root == null) {		// If root is null, DLB is empty
			return null;
		}
		StringCharacterIterator i = new StringCharacterIterator(name);	// New iterator for make and model string
		Node cur = root;
		while (i.getIndex() < i.getEndIndex()) { 	// Iterate through the make and model string
			if (cur == null) { 		// If null, make and model is not in the DLB
				return null;
			}
			while (i.current() != cur.getVal()) { 	// Go through the siblings to see if there's a match
				if (cur.getSibling() == null) {
					return null; 					// There is no sibling so make and model is not in the DLB
				}
				cur = cur.getSibling();			// Otherwise, get the sibling, if theres a match
			}
			cur = cur.getChild();		// Get next child
			i.next();
		}
		if (cur == null) { 	// If null after iterating the make and model string, the make and model is not the DLB
			return null;
		}
		else if (cur.getVal() == '^') {    // After iterating, check if we're at a terminal node
			return cur.getPQ();			   // If so, get the PQ stored at the node
		}
		while (cur.getSibling() != null) { // Go through siblings to see if there is a terminal node there
			if (cur.getVal() == '^') {	   // After iterating, check if we're at a terminal node
				return cur.getPQ();		   // If so, get the PQ stored at the node
			}
			cur = cur.getSibling();	   // If not, keep looping until null
		}
		return cur.getPQ();
	}
}
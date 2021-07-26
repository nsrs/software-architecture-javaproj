package wwwordz.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * "A trie is data structure to store words efficiently using a tree. 
 * Each tree node represents prefix that may be a a complete word. 
 * The descendants of a node are indexed by a letter, 
 * representing a possible continuation of that prefix followed by that letter."
 * 
 * This class has a single field, which is the root node of the trie.
 * 
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/puzzle/Trie.html
 */
public class Trie implements Iterable<String> {
	Node root;

	/**
	 * Creates a Trie instance and initializes the root Node
	 * 
	 */
    public Trie() {
        this.root = new Node();
    }

    /**
     * Traverses the Trie to insert the given String in it,
     * 		starting from the root.
     * 
     * @param word - the word to put in this Trie
     */
    public void put(String word) {
        root.put(word, 0);
    }

    /**
     * Starts a search at the root node of the Trie,
     * 		and returns the resulting Search instance.
     * 
     * @return a Search instance
     */
    public Search startSearch() {
        return new Search(root);
    }
	
    /**
     * Traverses the Trie until it reaches a leaf node,
     * 		as it collects the characters kept in each
     * 		in a sort of Depth First Search, where nodes are selected randomly
     * 
     * @return the full word, collected during the process
     */
	public String getRandomLargeWord() {
		Node node = root;
		Random random = new Random();
		StringBuilder word = new StringBuilder();
		
		while(!node.children.isEmpty()) {
			Set<Character> children = node.children.keySet();
			List<Character> c = new ArrayList<Character>(children);
			char letter = c.get(random.nextInt(c.size()));
			word.append(letter);
			
			node = node.children.get(letter);
		}
		
		return word.toString();
	}

	/**
	 * Creates and returns an Iterator for the Trie's nodes.
	 * 
	 * @return an Iterator for the Trie's nodes
	 * 
	 * @see wwwordz.puzzle.Trie.NodeIterator
	 */
	public Iterator<String> iterator() {
		return new NodeIterator() ;
	}
	
	/**
	 * A nested class whose objects keep the position of a Trie search,
	 * 		i.e. one of its nodes (which is what its only field contains).
	 * 
	 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/puzzle/Trie.Search.html
	 */
	public class Search {
        Node node;

        /**
         * Creates a new instance out of a node of a Trie,
         * 		initializing this instance's field <i>node</i> with
         * 		a reference to that object.
         * 
         * @param node - the node to be referenced to from this Search object
         */
        public Search(Node node) {
            this.node = node;
        }

        /**
         * Creates a new instances out of another Search object's node,
         * 		initializing this instance's field <i>node</i> with
         * 		a reference to the given object's field.
         * 
         * @param search - the Search instance to "copy" from
         */
        public Search(Search search) {
            this.node = search.node;
        }

        /**
         * Checks if this object's node has a child node 
         * 		with the same given character,
         * 		also changing the current state of the search 
         * 		to that node if so.
         * 
         * @param letter - the character to be checked
         * 
         * @return a boolean value, the outcome of said verification
         */
        boolean continueWith(char letter) {
        	if (this.node.children.containsKey(letter)) {
        		this.node = this.node.children.get(letter);
        		return true;
        	} else {
        		return false;
        	}
        }

        /**
         * Verifies if this object's node is at a point of the Trie
         * 		where it is marked as containing the last character
         * 		of a word (i.e. the node is marked as a word).
         * 
         * @return the boolean value representing if the node
         * 			is a word
         */
        boolean isWord() {
            if(this.node.isWord) {
                return true;
            } else {
                return false;
            }
        }
    }
	
	/**
	 * A node of a Trie, which may or may not contain other nodes
	 * 		as its children, kept in a HashMap field for such purpose,
	 * 		each identified by a character.<br>
	 * A node may also be at a point of the Trie where the concatenation
	 * 		of its parents' characters forms a word, so a boolean field
	 * 		is kept to store that case. By default, nodes are not words.
	 *
	 */
	class Node extends HashMap<Character,Node> {
        private static final long serialVersionUID = 1L;
        HashMap<Character,Node> children;
        Boolean isWord;

        /**
         * Creates an instance of this class and initializes all of its fields.
         * 
         */
        public Node() {
        	this.children = new HashMap<Character,Node>();
            this.isWord = false;
        }

        /**
         * Searches this instance's children Collection for the character at
         * 		the given position of the passed word, recursively calling
         * 		the method for said node (creating it if required).
         * It stops when the word's characters have all been "put" into
         * 		the Trie, marking the last node as the end of a word.
         * 
         * @param word - the word to put into the Trie
         * @param position - the index of word's character being put
         */
        public void put(String word, int position) {
            if(word.length() > position) {
                char key = word.charAt(position);
                if (this.children.containsKey(key)) {
                    this.children.get(key).put(word, position + 1);
                } else {
                    Node child = new Node();
                    this.children.put(key, child);
                    child.put(word, position + 1);
                }
            } else if (word.length() == position) {
                this.isWord = true;
            }
        }
    }
	
	/**
	 * An iterator for the nodes of the Trie.
	 * Based from the BTree example on the course's website,
	 * 		on the topic of Concurrency.
	 * It contains a Thread instance to allow concurrent iterations
	 * 		over the Trie.
	 * 
	 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/puzzle/Trie.NodeIterator.html
	 */
	public class NodeIterator implements Iterator<String>, Runnable {
		Thread thread;
		boolean terminated;
		String nextWord;
		
		/**
		 * Creates an instance and starts a new thread.
		 * 
		 */
		NodeIterator() {
			thread = new Thread(this,"Node iterator");
			thread.start();
        }

		/**
		 * Begins iterating from the root of the Trie,
		 * 		building a String with the characters
		 * 		it collects from visited nodes.
		 * 
		 */
		public void run() {
			terminated = false;

			visitValues(root, new StringBuilder());

			synchronized (this) {
				terminated = true;
				handshake();
			}
		}

		/**
		 * Checks if the current point of the iteration is
		 * 		at a leaf node
		 * 
		 * @return a boolean value that represents if the
		 * 		current point of iteration is at a leaf node
		 */
		public boolean hasNext() {
			synchronized (this) {
				if(! terminated)
					handshake();
			}
			return nextWord != null;
		}

		/**
		 * Advances to the next child node, updating the word
		 * 		with the following character
		 * 
		 * @return the next word "built" from the iteration
		 */
		public String next() {
			String word = nextWord;

			synchronized (this) {
				nextWord = null;
			}
			return word;
		}
        
		/**
		 * Visits all child nodes of the passed node,
		 * 		recursively iterating over every one
		 * 		below it.
		 * It collects any character from parent nodes,
		 * 		and also obtains the current node is a word,
		 * 		so that the iterator can pass that information
		 * 	    as well.
		 * 
		 * @param node - the node whose children will be visited
		 * @param word - the word prefix of previously visited
		 * 				 nodes
		 */
		private void visitValues(Node node, StringBuilder word) {
			if(!node.children.isEmpty()) {
				Set<Character> children = node.children.keySet();
				for(char child: children) {										
					visitValues(node.children.get(child), word.append(child));
					
					if(node.children.get(child).isWord) {
						synchronized (this) {
			                if(nextWord != null)
			                    handshake();
			                nextWord = word.toString();
			                handshake();
			            }
					}
					
					word.deleteCharAt(word.length() - 1);
				}
			}
		}
        
		/**
		 * Intermediary between concurrent threads, 
		 * 		to control thread locks.
		 * 
		 */
		private void handshake() {
            notify();
            try {
                wait();
            } catch (InterruptedException cause) {
                throw new RuntimeException("Unexpected interruption while waiting",cause);
            }
        }       
	}
}
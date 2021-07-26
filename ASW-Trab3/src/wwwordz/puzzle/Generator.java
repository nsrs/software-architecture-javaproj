package wwwordz.puzzle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;

import wwwordz.shared.*;
import wwwordz.shared.Puzzle.Solution;
import wwwordz.shared.Table.Cell;

/**
 * "A puzzle generator. Creates a puzzle with many 
 *  scrambled words contained in a dictionary."<br>
 *
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/puzzle/Generator.html
 */
public class Generator {
	private static final int CHAR_INDEX_MAX  = 26,
			  		  		 MATRIX_SIZE 	 = 4;
	private List<Table.Cell> selectedCells;
	private StringBuilder    word;

	/**
	 * Currently empty constructor.
	 * 
	 */
	public Generator() { }
	
	/**
	 * Generates a Puzzle object of high quality, 
	 * 		with at least one random "large" word.
	 * 
	 * @return a Puzzle instance of high quality
	 */
	public Puzzle generate() {
		Puzzle puzzle = new Puzzle();
		Table table = new Table();
		
		highQualityTable(table);
		
		puzzle.setTable(table);
		puzzle.setSolutions(getSolutions(table));
		
		return puzzle;
	}
	
	/**
	 * Generates a random Puzzle instance,
	 * 		whose table's cells have completely random characters,
	 * 		with no imposed "connection".
	 * 
	 * @return a completely random Puzzle instance 
	 */
	public Puzzle random() {
		Puzzle puzzle = new Puzzle();
		Table table = new Table();
		
		randomizeLetters(table);
	
		puzzle.setTable(table);
		puzzle.setSolutions(getSolutions(table));

		return puzzle;
	}
	
	/**
	 * Obtains all the solutions of a table and returns
	 * 		a list with no repeated solutions.<br>
	 * 
	 * It iterates over every cell of the table, 
	 * 		creating an instance of Trie.Search and then recursively
	 * 		doing the same for adjacent cells (a.k.a. its neighbors),
	 * 		as in a Breadth First Search.
	 * 
	 * @param table - the Table instance to search solutions in
	 * 
	 * @return a list of unique solutions to the given Table
	 */
	public List<Puzzle.Solution> getSolutions(Table table) {
		Table.Cell cell;
		word							  = new StringBuilder();
		selectedCells					  = new ArrayList<Table.Cell>();
		Set<Puzzle.Solution> solutions    = new HashSet<Puzzle.Solution>();
		
		
		for(Iterator<Table.Cell> it = table.iterator(); it.hasNext();) {
			Trie.Search search	= Dictionary.getInstance().startSearch();
			cell = it.next();
			search.continueWith(cell.getLetter());
			solutions.addAll(checkNeighbors(table,cell,search));
			word.deleteCharAt(word.length() - 1);
			selectedCells.remove(selectedCells.size() - 1);
		}
		
		selectedCells = null;
		word 		  = null;
		
		List<Puzzle.Solution> solutionList = new ArrayList<Puzzle.Solution>();
		
		for(Puzzle.Solution solution: solutions) {
			if(!contains(solutionList, solution.getWord())) {
				solutionList.add(solution);
			}
		}
		
		return solutionList;
	}
	
	/**
	 * Checks if a word already belongs to a list of solutions of a Puzzle.
	 * Required to replace incorrect, strange behavior happening with the 
	 * 		usage of pre-defined, similar methods.
	 * 
	 * @param solutions - the list of Puzzle.Solution instances
	 * @param word - the word whose existence will be verified
	 * 
	 * @return a boolean representing if the given word 
	 * 			is present or not on the list
	 */
	static private boolean contains(List<Solution> solutions, String word) {
		for(Solution solution:solutions)
			if(solution.getWord().equals(word))
				return true;
		return false;
	}
	
	/**
	 * Searches for empty, non-selected, adjacent cells of a given cell
	 * 		of a table. It also passes a Trie.Search object that's used
	 * 		to check if the current node is a word, in which case it will
	 * 		add it to the Set of solutions 
	 * 		that the method returns in the end.<br><br>
	 * 
	 * It uses the class fields <i>word</i>, a StringBuilder instance that
	 * 		keeps updating according to current state of the search
	 * 		through the Trie, and the <i>selectedCells</i> List of cells
	 * 		that are already being "used" in a word, thus preventing the
	 * 		search to check the same cell twice without backtracking.<br><br>
	 * 
	 * Although a Set is being used, its property of unique instances is not
	 * 		being used because it was not performing correctly, even with
	 * 		the proper, overriden <i>equals()</i> method of Table.Solution;
	 * 		however, it was kept to point out such fact (it can be replaced
	 * 		in a posterior phase).
	 * 
	 * @param table - the Table instance to search solutions in
	 * @param cell - the current Table.Cell instance where the search is at
	 * @param currentsearch - the Trie.Search object associated with a node
	 * 						  in the Dictionary's Trie where the search is at
	 * 
	 * @return a Set of solutions (may contain repeated solutions)
	 */
	private Set<Puzzle.Solution> checkNeighbors(Table table,
												Table.Cell cell,
												Trie.Search currentsearch) 
	{
		Set<Puzzle.Solution> solutions = new HashSet<Puzzle.Solution>();
		List<Cell> neighbors = table.getNeighbors(cell);
		
		word.append(cell.getLetter());
		selectedCells.add(cell);
		
		if(currentsearch.isWord()) {
			solutions.add(new Puzzle.Solution(word.toString(),
											  new ArrayList<Table.Cell>
					  						  (selectedCells)));
		}
		
		
		
		for(Table.Cell neighbor: neighbors) {
			Trie.Search search = Dictionary.getInstance().trie
								 .new Search(currentsearch);

			if(!selectedCells.contains(neighbor) 
			&& search.continueWith(neighbor.getLetter())) {
				solutions.addAll(checkNeighbors(table, neighbor, search));
				word.deleteCharAt(word.length() - 1);
				selectedCells.remove(selectedCells.size() - 1);
			}
		}

		return solutions;
	}
	
	/**
	 * Generates, one by one, random large words from the Trie,
	 * 		and attempts to put it in random empty cells of the Table,
	 * 	    so as to have the large word become one of the possible solutions;
	 *      it will change word if it cannot find empty, neighbor cells,
	 *		and will stop when the Table object 
	 *		has its cells completely filled.<br><br>
	 *
	 * Uses the class field <i>selectedCells</i> to mark which cells
	 * 		were already filled, or, in other, which are not empty.
	 * 		It was previously used to mark only cells used in a word,
	 * 		so that the process would use not only empty cells, but also
	 * 		unused cells with the same character. This proved useless,
	 * 		therefore that feature has been removed.
	 * 
	 * @param table -  the Table instance to change
	 */
	private void highQualityTable(Table table) {
		List<Cell> neighbors;
		List<Cell> empty = new ArrayList<Table.Cell>();
		selectedCells    = new ArrayList<Table.Cell>();
		Random random    = new Random();
		Table.Cell cell  = table.getCell(random.nextInt(MATRIX_SIZE)+1,
										 random.nextInt(MATRIX_SIZE)+1);

		empty.addAll(table.getEmptyCells());
		
		while(empty.size() != 0) {
			String largeWord = Dictionary.getInstance().getRandomLargeWord();
			
			for(int k = 0; k < largeWord.length(); k++) {
				selectedCells.add(cell);
				empty.remove(cell);
				cell.setLetter(largeWord.charAt(k));
				
				neighbors = table.getNeighbors(cell);
				
				if(!selectedCells.containsAll(neighbors)) { 
					while(!empty.contains(cell)) {
						cell = neighbors.get(random.nextInt(neighbors.size()));
					}
				} else {
					while(!empty.contains(cell) && empty.size() != 0) {
						cell = empty.get(random.nextInt(empty.size()));
					}
					break;
				}
			}
		}
		
		selectedCells = null;
	}
	
	/**
	 * Randomizes the characters of every cell,
	 * 		 of a given Table instance.
	 * 
	 * @param table - the Table instance to change
	 */
	private void randomizeLetters(Table table) {
		Random random = new Random();
		char randomletter;
		
		for(int i = 1; i <= MATRIX_SIZE; i++) {
			for(int j = 1; j <= MATRIX_SIZE; j++) {
				if (table.getCell(i,j).isEmpty()) {
					randomletter = (char) ((int)'A' + random.nextInt(CHAR_INDEX_MAX));
					table.setLetter(i,  j, randomletter);
				}
			}
		}
	}
}

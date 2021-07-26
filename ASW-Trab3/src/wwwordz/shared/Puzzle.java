package wwwordz.shared;

import java.io.Serializable;
import java.util.List;

/**
 * "A puzzle, containing a table and list of solutions. 
 * A table is a square grid of letters and a solution 
 * 		is a word contained in the grid, 
 * 		where consecutive letters are in neighboring cells on the grid
 * 		 and the letter in each cell is used only once."
 * 
 *  @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Puzzle.html
 */
public class Puzzle implements Serializable {
	private static final long serialVersionUID = 1L;
	Table table;
	List<Solution> solutions;
	
	/**
	 * Empty constructor
	 * 
	 */
	public Puzzle() {}

	/**
	 * Returns this Puzzle instance's Table.
	 * 
	 * @return a Table object
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Changes this Puzzle's table to the given one.
	 * 
	 * @param table - the replacing Table instance
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Returns the Solution list of this instance.
	 * 
	 * @return a list of this Puzzle's solutions
	 */
	public List<Solution> getSolutions() {
		return solutions;
	}

	/**
	 * Assigns a list of solutions for this Puzzle.
	 * 
	 * @param solutions - the new list of solutions
	 */
	public void setSolutions(List<Solution> solutions) {
		this.solutions = solutions;
	}
	
	
	/**
	 * A Solution is composed of a word, which can be found
	 * 		in the enclosing Puzzle's table,
	 * 		and a list of Cell instances in an order such that
	 * 		each of the cells' letters form the word.
	 * 
	 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Puzzle.Solution.html
	 */
	public static class Solution implements Serializable {
		private static final long serialVersionUID = 1L;
		String word;
		List<Table.Cell> cells;
		
		/**
		 * Empty constructor.
		 */
		public Solution() {}
		
		/**
		 * Creates a Solution instance out of the given
		 * 		word and list of cells, which will set its fields.
		 * 
		 * @param word - the full word of this Solution
		 * @param cells - the list of cells where the word is at
		 */
		public Solution(String word, List<Table.Cell> cells) {
			this.word  = word;
			this.cells = cells;
		}

		/**
		 * Returns the <i>word</i> field of this Solution.
		 * 
		 * @return a String, which is the word that this SOlution
		 * 		instance addresses to
		 */
		public String getWord() {
			return word;
		}

		/**
		 * Returns the list of Cell instances whose letters
		 * 		make up the word of this Solution.
		 * 
		 * @return a list of Cells that contain the Solution's word
		 */
		public List<Table.Cell> getCells() {
			return cells;
		}
		
		/**
		 * Calculates the points of this Solution instance.<br>
		 * 		Words with a 3 letters are worth 1 point.<br>
		 * 		Words with more than 3 letters are worth
		 * 			twice the value of a word whose length
		 * 			is smaller by 1 character, plus 1.
		 * 
		 * @return a number, the number of points this Solution
		 * 			is worth
		 */
		public int getPoints() {
			int length = this.getWord().length();
			int points = 1;
			
			for(int i = length; i > 3; i--) { 
				points += 1 + 2 * points;
			}
			return points;
		}
	}
}

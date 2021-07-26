package wwwordz.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * "A table composed of a collection of cells 
 * 		indexed by row and column positions."<br><br>
 * 
 * The <i>table</i>table, a Cell matrix, stores the actual
 * 		table's information.
 *
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Table.html
 */
public class Table implements Iterable<Table.Cell>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MATRIX_SIZE = 4;
	Cell[][] table;
	
	/**
	 * Creates a Table instance whose <i>table</i> matrix is set
	 * 		to have only empty cells.
	 * 
	 */
	public Table() {
		this.table = new Cell[MATRIX_SIZE + 2][MATRIX_SIZE + 2]; 
		for(int i = 1; i <= MATRIX_SIZE; i++) {
			for(int j = 1; j <= MATRIX_SIZE; j++) {
				this.table[i][j] =  new Cell(i, j);
			}
		}
	}
	
	/**
	 * Creates a Table instance whose <i>table</i> matrix is set
	 * 		to have every line to contain the characters (in order)
	 * 		of a String, given a String array with the size of
	 * 		the matrix's number of lines.
	 * 
	 * @param data - the String array with the same size as the table's
	 * 				 number of lines
	 */
	public Table(String[] data) {
		this.table = new Cell[MATRIX_SIZE + 2][MATRIX_SIZE + 2];
		for(int i = 1; i <= MATRIX_SIZE; i++) {
			for(int j = 1; j <= MATRIX_SIZE; j++) {
				this.table[i][j] = new Cell(i, j, (data[i - 1].charAt(j - 1)));
			}
		}
	}

	/**
	 * Retrieves the letter kept in a Cell instance on a given
	 * 		row and column of <i>table</i>.
	 * 
	 * @param row - the cell's row index
	 * @param column - the cell's columns index
	 * 
	 * @return the character of the point Cell instance
	 */
	public char getLetter(int row, int column) {
		return this.table[row][column].getLetter();
	}
	
	/**
	 * Changes the letter stored in a Cell instance on a given
	 * 		row and column of <i>table</i>.
	 * 
	 * @param row - the cell's row index
	 * @param column - the cell's columns index
	 * @param letter - the new character for the cell to store
	 * 
	 */
	public void setLetter(int row, int column, char letter) {
		this.table[row][column].setLetter(letter);
	}
	
	/**
	 * Iterates over all of the table's cells ands
	 * 		adds every empty cell to a list, which
	 * 		is returned at the end of the procedure.
	 * 
	 * @return the complete list of empty cells of the table
	 */
	public List<Cell> getEmptyCells() {
		List<Cell> cells = new ArrayList<Cell>();
		for(int i = 1; i <= MATRIX_SIZE; i++) {
			for(int j = 1; j <= MATRIX_SIZE; j++) {
				if(this.table[i][j].isEmpty()) {
					cells.add(this.table[i][j]);
				}
			}
		}
		return cells;
	}
	
	/**
	 * Searches all of the actual adjacent cells of a
	 * 		given Cell instance, and adds the actual
	 * 		neighbors, i.e. cells that are not border
	 * 		cells, to a list of Cells.
	 * 
	 * @param cell - the Cell whose neighbors will be
	 * 				 retrieved
	 * 
	 * @return the list of neighbors of the given Cell
	 */
	public List<Cell> getNeighbors(Cell cell) {
		List<Cell> cells = new ArrayList<Cell>();
		Cell neighbor;
		for(int i = cell.row - 1; i <= cell.row + 1; i++) {
			for(int j = cell.column - 1; j <= cell.column + 1; j++) {
				neighbor = this.table[i][j];
				if(!cell.equals(neighbor) && neighbor != null) {
					cells.add(neighbor);
				}
			}
		}
		return cells;
	}
	
	/**
	 * Retrieves a Cell instance, kept at a given position in the
	 * 		<i>table</i> matrix.
	 * 
	 * @param row - the row of the matrix to point at
	 * @param column - the column of the matrix to point at
	 * 
	 * @return the Cell instance corresponding to the given
	 * 		row and column
	 */
	public Cell getCell(int row, int column) {
		return this.table[row][column];
	}

	/**
	 * Returns an iterator for the table's cells.
	 * 
	 * @return an Iterator instance for Cells
	 */
	public Iterator<Cell> iterator() {
		return new CellIterator(table);
	}
	

	/**
	 * @Override
	 * toString() method for Table, returning a String
	 * 		that shows the table's cells as an array.
	 * 
	 * @return a String representation of this instance of Table
	 */
	public String toString() {
		return "Table [table=" + Arrays.toString(table) + "]";
	}

	/**
	 * @Override
	 * hashCode() method for Table
	 * 
	 * @return an integer hashcode of this instance
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(table);
		return result;
	}

	/**
	 * @Override
	 * equals() method for Table<br>
	 * A different Table instance whose Cells hold the
	 * 		same characters in the same positions of this one
	 * 		is equal.
	 * 
	 * @param obj - an Object type to be compared to this
	 * 				Table instance
	 * 
	 * @return a boolean value, representing if the passed Object
	 * 			is equal to this instance
	 * 			
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (!Arrays.deepEquals(table, other.table))
			return false;
		return true;
	}


	/** 
	 * "A cell in the enclosing table".
	 * Contains row and column indices, its position on the Table instance,
	 * 		and the letter it "may" hold.
	 * A Cell may also be null, if it is a border cell, which is not relevant
	 * 		to the actual game.
	 * 
	 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Table.Cell.html
	 */
	public static class Cell implements Serializable {
		private static final long serialVersionUID = 1L;
		int row;
		int column;
		char letter;

		/**
		 * Empty constructor
		 * 
		 */
		Cell() {}		

		/**
		 * Creates a Cell instance with the given character.
		 * 
		 * @param row - the row to assign this Cell to on the enclosing
		 * 				Table instance
		 * @param column - the column to assign this Cell to on the enclosing
		 * 				Table instance
		 * @param letter - the letter this Cell will contain
		 */
		Cell(int row, int column, char letter) {
			this.row      = row;
			this.column   = column;
			this.letter   = letter;
		}

		/**
		 * Creates an empty Cell i.e. a Cell with no letter.<br>
		 * An empty cell contains the '\0' character.
		 * 
		 * @param row - the row to assign this Cell to on the enclosing
		 * 				Table instance
		 * @param column - the column to assign this Cell to on the enclosing
		 * 				Table instance
		 */
		Cell(int row, int column) {
			this.row      = row;
			this.column   = column;
			this.letter   = '\0';
		}

		/**
		 * Checks if the Cell his empty, i.e. is empty.
		 * 
		 * @return a boolean value representing if the Cell is
		 * 			has a letter
		 */
		public boolean isEmpty() {
			if(this.letter == '\0') {
				return true;
			} else {
				return false;
			}
		}
		
		/**
		 * Retrieves this Cell's stored letter.
		 * 
		 * @return the <i>letter</i> field of this instance
		 */
		public char getLetter() {
			return letter;
		}

		/**
		 * Changes this Cell object's letter to the given one.
		 * 
		 * @param letter - the new character this Cell will contain
		 */
		public void setLetter(char letter) {
			this.letter = letter;
		}
		
		/**
		 * @Override
		 * toString() method for Cell, returning a String
		 * 		that shows this instances fields.
		 * 
		 * @return a String representation of this instance of Cell
		 */
		public String toString() {
			return "Cell [row=" + row + ", column=" + column + ", letter=" + letter + "]";
		}

		/**
		 * @Override
		 * hashCode() method for Cell
		 * 
		 * @return an integer hashcode of this instance
		 */
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + column;
			result = prime * result + letter;
			result = prime * result + row;
			return result;
		}

		/**
		 * @Override
		 * equals() method for Table<br>
		 * A Cell instance can only be the same to another if it has exactly
		 * 		the same fields.
		 * 
		 * @param obj - an Object type to be compared to this
		 * 				Cell instance
		 * 
		 * @return a boolean value, representing if the passed Object
		 * 			is equal to this instance
		 * 			
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Cell other = (Cell) obj;
			if (column != other.column)
				return false;
			if (letter != other.letter)
				return false;
			if (row != other.row)
				return false;
			return true;
		}
	}
	
	/**
	 * "An iterator over cells in this table."<br>
	 * Fields <i>row</i> and <i>column</i> are used to point
	 * 		to any Cell in this table during the iteration.
	 *
	 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/Table.CellIterator.html
	 */
	public class CellIterator implements Iterator<Table.Cell> {
			int row;
			int column;
			Cell table[][];
			
			/**
			 * Creates an instance of this class by setting the initial row
			 * 		to the first of the matrix, 
			 * 		and the column to the left border column of the matrix,
			 * 	    considering that the iteration will begin by asking for
			 * 		the next cell, the actual first 
			 * 		(i.e. the Cell in position (1,1) in the table Matrix).
			 */
			CellIterator(Cell t[][]) {
				this.row    = 1;
				this.column = 0;
				this.table  = t;
			}
			
			/**
			 * Checks if there is a non-border Cell next to the current one, 
			 * 		whether it is on the column to the right of this one,
			 * 		or on the first position of the next row.
			 * 
			 * @return a boolean value, representing if the current Cell
			 * 		is proceeded by another
			 */
			public boolean hasNext() {
				if(table[row][column+1] == null && table[row+1][1] == null) {
					return false;
				} else {
					return true;
				}
			}
			
			/**
			 * Retrieves the Cell instance that follows the current one.
			 * It also updates the Iterator's fields accordingly.
			 * 
			 * @return a Cell instance that follows the current one
			 */
			public Table.Cell next() {
				if (column + 1 <= MATRIX_SIZE) {
					return table[row][++column];
				} else {
					column = 1;
					return table[++row][1];
				}
			}

			/**
			 * Not to be used right now.
			 * Currently empty method.
			 */
			public void remove() { }
	}
}



package edu.brown.cs.student.main.creator;

import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.util.ArrayList;
import java.util.List;

/** A Creator class implementing CreatFromRow interface for Integers. */
public class IntCreator implements CreatorFromRow<List<Integer>> {
  private List<List<Integer>> rows;
  private int column;

  /** This is the Creator class implementing the CreatorFromRow interface. */
  public IntCreator() {
    this.rows = new ArrayList<List<Integer>>();
  }

  /**
   * This method, implemented from CreatorFromRow interface, returns the created object, which in
   * this case is a List of Integers. It also stores the number of columns. This is created to test
   * generality of the CSVParser class.
   *
   * @param row converted into Integers
   * @return the row passed into the parameter
   */
  @Override
  public List<Integer> create(List<String> row) {
    List<Integer> intRow = new ArrayList<Integer>();
    try { // parses String into Integer
      for (String s : row) {
        int ID = Integer.parseInt(s);
        intRow.add(ID);
      }
    } catch (NumberFormatException n) {
      System.out.println("Testing NumberFormatException");
    }
    this.rows.add(intRow);
    this.column = row.size();
    return intRow;
  }

  /**
   * This method returns the number of columns.
   *
   * @return number of columns
   */
  @Override
  public int getCol() {
    return this.column;
  }

  /**
   * This method returns the row at the provided index.
   *
   * @param row index that we are trying to access from List of List of Rows
   * @return the List of Integers at the indexed row
   */
  @Override
  public List<Integer> getRow(int row) {
    return this.rows.get(row);
  }

  /**
   * This method checks the
   *
   * @param target The target string the program is looking for
   * @param columnIdentifier Which column of the row to access
   * @param row The index which the row should be retrieved from the list
   * @return Boolean value indicating whether the target String is found or not
   * @throws ArrayIndexOutOfBoundsException when there is a malformed row so the columnIdentifier
   *     cannot access tha index at a particular row.
   */
  @Override
  public boolean targetRow(String target, int columnIdentifier, int row)
      throws ArrayIndexOutOfBoundsException {
    int targetInt = 0;
    if (columnIdentifier < this.column) {
      try {
        targetInt = Integer.parseInt(target);
      } catch (NumberFormatException n) {
        System.out.println("Testing NumberFormatException");
      }
      if (this.rows.get(row).get(columnIdentifier).equals(targetInt)) {
        return true;
      }
    }
    return false;
  }
}

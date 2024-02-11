package edu.brown.cs.student.main.creator;

import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.util.ArrayList;
import java.util.List;

/** This is the Creator class implementing the CreatorFromRow interface. */
public class StringCreator implements CreatorFromRow<List<String>> {
  private List<List<String>> rows;
  private int column;

  /**
   * This is the constructor for the Creator class. It instantiates an ArrayList of List of Strings
   * to keep track of rows.
   */
  public StringCreator() {
    this.rows = new ArrayList<List<String>>();
  }

  /**
   * This method, implemented from CreatorFromRow interface, returns the created object, which in
   * this case is a List of Strings. It also stores the number of columns.
   *
   * @param row of the CSV file
   * @return the row passed into the parameter
   */
  @Override
  public List<String> create(List<String> row) {
    this.rows.add(row);
    this.column = row.size();
    return row;
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
   * @param row index of the row
   * @return the row at the provided index
   */
  @Override
  public List<String> getRow(int row) {
    return this.rows.get(row);
  }

  /**
   * This method checks if the inputted target String exists at the given columnIdentifier and row
   * and returns a boolean value according to the result. When searching for target, case is
   * ignored.
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
      throws FactoryFailureException {
    try {
      if (columnIdentifier < this.column) {
        if (this.rows.get(row).get(columnIdentifier).equalsIgnoreCase(target)) {
          return true;
        }
      }
      return false;
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new FactoryFailureException("There was a malformed row.", this.rows.get(row));
    }
  }
}

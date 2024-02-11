package edu.brown.cs.student.main.parser;

import edu.brown.cs.student.main.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 *
 * <p>Your parser class constructor should take a second parameter of this generic interface type.
 */
public interface CreatorFromRow<T> {
  /**
   * This is the general create method based on the row of CSV file. The specific implementation can
   * depend on the class implementing the interface.
   *
   * @param row List of Strings representing row
   * @return General Object T
   * @throws FactoryFailureException when there is an error creating the Object
   */
  T create(List<String> row) throws FactoryFailureException;

  /**
   * Returns if a target String is found at a given row and column. Specific implementation can
   * depend on the class implementing the interface.
   *
   * @param target String to find
   * @param columnIdentifier Column ID number
   * @param row Row number
   * @return boolean value indicating whether target String exists there or not
   * @throws FactoryFailureException when there is a malformed row
   */
  boolean targetRow(String target, int columnIdentifier, int row) throws FactoryFailureException;

  /**
   * This method returns the row at the provided index.
   *
   * @param row index of the row
   * @return the row at the provided index
   */
  T getRow(int row);

  /**
   * This method returns the number of columns.
   *
   * @return number of columns
   */
  int getCol();
}

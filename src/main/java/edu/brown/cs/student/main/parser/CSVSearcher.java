package edu.brown.cs.student.main.parser;

import edu.brown.cs.student.main.FactoryFailureException;
import java.io.IOException;
import java.util.List;

/**
 * This is the CSVSearcher class responsible for searching through the parsed CSV file to find a
 * target Object. This class is generalizable with different types of Objects.
 *
 * @param <T> General Object for CSVSearcher
 */
public class CSVSearcher<T> {
  private CSVParser<T> parser;
  private List<T> createdList;
  private CreatorFromRow<T> creator;

  /**
   * Constructor for CSVSearcher.
   *
   * @param parser CSVParser created beforehand
   * @param creator Class implementing CreatorFromRow
   */
  public CSVSearcher(CSVParser<T> parser, CreatorFromRow<T> creator) {
    this.parser = parser;
    this.creator = creator;
  }

  /**
   * This method handles the search for the target String. First, it parses through the CSV file,
   * and adjusts the search index according to whether there is a header present or not. It also
   * checks for any specific column IDs to search for in both Integer and String form, and if there
   * are none, it performs search across all columns. If the target String is found, it returns a
   * boolean value true. If not, it returns false.
   *
   * @param toSearch target String to search for
   * @param headerPresent boolean value indicating whether there is a header in the file
   * @param columnIsNum boolean value indicating whether all columnIDs are Integers. If not, it also
   *     converts any String column IDs into Integers.
   * @param columnIDInteger List of Integers containing column IDs
   * @param columnIDString List of Strings containing column IDs
   * @return Returns boolean value indicating if the target String is found or not
   * @throws IOException when there is an issue with calling parse
   * @throws FactoryFailureException when there is an issue calling parse
   * @throws ArrayIndexOutOfBoundsException when there is a malformed row
   */
  public boolean searchHelper(
      String toSearch,
      boolean headerPresent,
      boolean columnIsNum,
      List<Integer> columnIDInteger,
      List<String> columnIDString)
      throws IOException, FactoryFailureException, ArrayIndexOutOfBoundsException {

    this.createdList = this.parser.parse();
    boolean found = false;

    // adjust header appropriately
    int index = 0;
    if (headerPresent) {
      index += 1;
    }

    if (!columnIsNum) { // if the column identifier is a string
      for (int addID = 0; addID < this.creator.getCol(); addID++) {
        for (String s : columnIDString) {
          if (this.creator.targetRow(s, addID, 0) && !columnIDInteger.contains(addID)) {
            columnIDInteger.add(addID);
          }
        }
      }
    }

    for (int row = index; row < this.createdList.size(); row++) {
      // There were no column identifiers given
      if (columnIDInteger.isEmpty()) {
        for (int col = 0; col < this.creator.getCol(); col++) {
          if (this.creator.targetRow(toSearch, col, row)) {
            System.out.println(this.creator.getRow(row));
            found = true;
          }
        }
      } else {
        // searching through the columnIDInteger List
        for (Integer integer : columnIDInteger) {
          if (this.creator.targetRow(toSearch, integer, row)) {
            System.out.println(this.creator.getRow(row));
            found = true;
          }
        }
      }
    }
    return found;
  }
}

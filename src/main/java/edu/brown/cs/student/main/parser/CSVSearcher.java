package edu.brown.cs.student.main.parser;

import edu.brown.cs.student.main.FactoryFailureException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is the CSVSearcher class responsible for searching through the parsed CSV file to find a
 * target Object. This class is generalizable with different types of Objects.
 *
 * @param <T> General Object for CSVSearcher
 */
public class CSVSearcher<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private CSVParser<T> parser;
  private List<T> createdList;
  private CreatorFromRow<T> creator;

  /**
   * Constructor for CSVSearcher.
   *
   * @param creator Class implementing CreatorFromRow
   */
  public CSVSearcher(CreatorFromRow<T> creator) {
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
      CSVParser<T> parser,
      String toSearch,
      boolean headerPresent,
      boolean columnIsNum,
      List<Integer> columnIDInteger,
      List<String> columnIDString)
      throws IOException, FactoryFailureException, ArrayIndexOutOfBoundsException {
    this.parser = parser;
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

  public List<List<String>> searchCSV(
      String toSearch, boolean headerPresent, String columnIDString, List<List<String>> parsedData)
      throws IOException, FactoryFailureException, ArrayIndexOutOfBoundsException {
    List<List<String>> found = new ArrayList<List<String>>();

    // adjust header appropriately
    List<Integer> columnIDIntegerList = new ArrayList<Integer>();
    List<String> columnIDStringList = Arrays.asList(regexSplitCSVRow.split(columnIDString));
    int index = 0;
    if (headerPresent) {
      index += 1;
    }
    this.convertColumnStringToList(columnIDIntegerList, columnIDStringList);
    for (int row = index; row < parsedData.size(); row++) {
      this.storeFound(toSearch, found, columnIDIntegerList, row);
    }
    return found;
  }

  // NOTE: This method adds to the list of found rows, s
  private List<List<String>> storeFound(
      String toSearch, List<List<String>> found, List<Integer> columnIDIntegerList, int row)
      throws FactoryFailureException {
    if (columnIDIntegerList.isEmpty()) {
      for (int col = 0; col < this.creator.getCol(); col++) {
        if (this.creator.targetRow(toSearch, col, row)) {
          found.add((List<String>) this.creator.getRow(row));
        }
      }
    } else {
      // searching through the columnIDInteger List
      for (Integer integer : columnIDIntegerList) {
        if (this.creator.targetRow(toSearch, integer, row)) {
          found.add((List<String>) this.creator.getRow(row));
        }
      }
    }
    return found;
  }

  private void convertColumnStringToList(
      List<Integer> columnIDIntegerList, List<String> columnIDStringList)
      throws FactoryFailureException {
    for (String s : columnIDStringList) {
      try {
        int ID = Integer.parseInt(s); // convert column identifier to Integer
        if (!columnIDIntegerList.contains(ID)) {
          columnIDIntegerList.add(ID);
        }
      } catch (NumberFormatException e) {
        for (int addID = 0; addID < this.creator.getCol(); addID++) {
          if (this.creator.targetRow(s, addID, 0) && !columnIDIntegerList.contains(addID)) {
            columnIDIntegerList.add(addID);
          }
        }
      }
    }
  }
}

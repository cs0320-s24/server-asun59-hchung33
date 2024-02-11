package edu.brown.cs.student.main.parser;

import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.creator.StringCreator;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is the CSVUtility class containing CSVParser, CSVSearcher, and REPL program.
 *
 * @param <T> General Object for CSVUtility
 */
public class CSVUtility<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private String path;
  private CSVSearcher<List<String>> searcher;
  private CSVParser<List<String>> parser;
  private CreatorFromRow<List<String>> toCreate;

  /**
   * This is the constructor for CSVUtility
   *
   * @throws IOException when there is an issue reading lines in REPL
   * @throws FactoryFailureException when there is an issue in REPL
   * @throws ArrayIndexOutOfBoundsException when there is a malformed row
   */
  public CSVUtility() throws IOException, FactoryFailureException, ArrayIndexOutOfBoundsException {}

  /**
   * This method handles the REPL program. This is continuously called in Main to accept inputs from
   * users such as target String, whether header is present or not, potential column IDs. Afterward,
   * it calls search, which calls the searchHelper method in CSVSearcher.
   *
   * @param bReader BufferedReader created in Main that reads from System.in
   * @param path String indicating the file path to look for
   * @throws IOException when there is an issue reading from System.in
   * @throws FactoryFailureException when there is an issue with parse and search
   * @throws ArrayIndexOutOfBoundsException when there is a malformed row
   */
  public void REPL(BufferedReader bReader, String path)
      throws IOException, FactoryFailureException, ArrayIndexOutOfBoundsException {
    CreatorFromRow<List<String>> creator = new StringCreator();
    if (path.equals("exit")) {
      System.exit(0);
    }
    this.path = path;
    this.toCreate = creator;
    this.parse(this.path);
    System.out.println("To parse another file, enter parse. Press any key to search.");
    path = bReader.readLine();
    this.checkExit(path);
    if (!path.equalsIgnoreCase("parse")) { // if the user wants to search
      System.out.println("Enter target String."); // target String set
      String toSearch = bReader.readLine();
      System.out.println("Indicate whether header is present or not: true or false.");
      boolean headerPresent = false;
      String headerResponse = bReader.readLine();
      this.checkExit(headerResponse);
      if (headerResponse.equalsIgnoreCase("true")) { // header status set
        headerPresent = true;
      }
      System.out.println("Enter column identifier number or name, separated by columns.");
      path = bReader.readLine();
      this.checkExit(path);
      List<String> columnIDString =
          Arrays.asList(regexSplitCSVRow.split(path)); // parse column identifiers
      columnIDString.replaceAll(CSVParser::postprocess);
      List<Integer> columnIDInteger = new ArrayList<Integer>();
      boolean columnIsNum = true;
      for (String s : columnIDString) {
        try {
          int ID = Integer.parseInt(s); // convert column identifier to Integer
          if (!columnIDInteger.contains(ID)) {
            columnIDInteger.add(ID);
          }
        } catch (NumberFormatException e) {
          columnIsNum =
              false; // if not in number form, set as String to search for matching column names
        }
      }
      if (!this.search(toSearch, columnIDString, columnIDInteger, headerPresent, columnIsNum)) {
        System.out.println("Could not find target string!");
      }
    }
  }

  /**
   * This is a helper method that checks if the inputted String is equal to "exit". If it is equal,
   * the program prints out that it is terminating and cleanly exits.
   *
   * @param string String to check if it is equal to "exit"
   */
  public void checkExit(String string) {
    if (string.equals("exit")) {
      System.out.println("Terminating program!");
      System.exit(0);
    }
  }

  /**
   * This is the parse method that instantiates the CSVParser. If the file path does not contain
   * /data, this indicates we are not within the right directory. Therefore, it alerts the user that
   * it cannot access the given file and parse it as a String. Otherwise, it successfully parses the
   * file. If the file is not found at the given path, then it parses as a String using StringReader
   *
   * @param path String representing file path
   */
  public void parse(String path) {
    try {
      if (!path.contains("/data")) { // not within the correct directory
        Reader sReader = new StringReader(path);
        this.parser = new CSVParser<List<String>>(sReader, this.toCreate);
        System.out.println("Unable to access file. Parsed as a string!");
      } else {
        Reader fReader = new FileReader(path); // in the correct directory
        this.parser = new CSVParser<List<String>>(fReader, this.toCreate);
        System.out.println("Finished parsing your file!");
      }
    } catch (FileNotFoundException f) { // if file does not exist, parse as String
      Reader sReader = new StringReader(path);
      this.parser = new CSVParser<List<String>>(sReader, this.toCreate);
      System.out.println("File not found. Parsed as a string!");
    }
  }

  /**
   * This is the method that instantiates the CSVSearcher.
   *
   * @param toSearch target String
   * @param columnIDString List of Strings containing column IDs
   * @param columnIdentifier List of Integers containing column IDs
   * @param headerPresent boolean value indicating whether there is a header in the file
   * @param columnIsNum boolean value indicating whether all columnIDs are Integers. If not, * it
   *     also converts any String column IDs into Integers.
   * @return boolean value indicating whether the target String was found or not
   * @throws IOException when there is an issue searching and calling parse
   * @throws FactoryFailureException when there is an issue searching
   */
  public boolean search(
      String toSearch,
      List<String> columnIDString,
      List<Integer> columnIdentifier,
      boolean headerPresent,
      boolean columnIsNum)
      throws IOException, FactoryFailureException {
    this.searcher = new CSVSearcher<List<String>>(this.parser, this.toCreate);
    return this.searcher.searchHelper(
        toSearch, headerPresent, columnIsNum, columnIdentifier, columnIDString);
  }
}

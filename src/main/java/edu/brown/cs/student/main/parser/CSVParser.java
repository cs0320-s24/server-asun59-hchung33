package edu.brown.cs.student.main.parser;

import edu.brown.cs.student.main.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is the CSVParser class that is responsible for parsing the CSV file and returning a List of
 * created Objects. This class is generalizable with different types of Objects.
 *
 * @param <T> General Object for CSVParser
 */
public class CSVParser<T> {
  BufferedReader reader;
  CreatorFromRow<T> toCreate;
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * Constructor for CSVParser.
   *
   * @param reader a reader that reads from a csv file
   * @param toCreate Creator class that implements CreatorFromRow interface
   */
  public CSVParser(Reader reader, CreatorFromRow<T> toCreate) {
    this.reader = new BufferedReader(reader);
    this.toCreate = toCreate;
  }

  /**
   * Method that creates an ArrayList of generic type object. Reads individual lines of the CSV file
   * until it reaches EOF. Then, it calls creator on CreatorFromRow object and adds it to the
   * ArrayList of objects. Then, it returns the createdList.
   *
   * @return The created list of objects
   * @throws IOException when there is an issue reading line or when there is an issue while calling
   *     create
   * @throws FactoryFailureException when there is an issue creating Objects
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    List<T> createdList = new ArrayList<T>();
    String line = this.reader.readLine();
    while (line != null) {
      List<String> row = Arrays.asList(regexSplitCSVRow.split(line));
      row.replaceAll(CSVParser::postprocess);
      createdList.add(this.toCreate.create(row));
      line = this.reader.readLine();
    }
    return createdList;
  }

  /**
   * This is a public static method to post process any beginning quotes, ending quotes,
   * double-double-quotes. This cleans up String modified by the regex expression.
   *
   * @param arg String to process
   * @return processed String
   */
  public static String postprocess(String arg) {
    return arg
        // Remove extra spaces at beginning and end of the line
        .trim()
        // Remove a beginning quote, if present
        .replaceAll("^\"", "")
        // Remove an ending quote, if present
        .replaceAll("\"$", "")
        // Replace double-double-quotes with double-quotes
        .replaceAll("\"\"", "\"");
  }
}

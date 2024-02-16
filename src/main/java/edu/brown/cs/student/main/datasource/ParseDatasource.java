package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.creator.StringCreator;
import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This is the ParseDatasource that maintains all data for all CSVHandlers.
 */
public class ParseDatasource {
  List<List<String>> parsed;
  private Map<String, List<List<String>>> responseMap;
  public CreatorFromRow<List<String>> stringCreator;

  /**
   * This is the constructor for the ParseDatasource class.
   */
  public ParseDatasource() {}

  /**
   * This method parses a provided file from its path by creating an instance of
   * CSVParser.
   * @param path File path provided by the user to parse
   * @throws FactoryFailureException If there are issues while parsing the file path
   * @throws IOException If there are any issues with the format of the path
   */
  public void parse(String path) throws FactoryFailureException, IOException {
    this.stringCreator = new StringCreator();
    FileReader reader = new FileReader(path);
    CSVParser<List<String>> parser = new CSVParser<List<String>>(reader, stringCreator);
    this.parsed = parser.parse();
  }

  /**
   * This returns the CreatorFromRow instance
   * @return CreatorFromRow instance
   */
  public CreatorFromRow<List<String>> getCreator() {
    return this.stringCreator;
  }

  /**
   * This returns the List of List of String parsed from the CSV file
   * @return Parsed List of List of String from the CSV file
   */
  public List<List<String>> getParsed() {
    return this.parsed;
  }

  /**
   * This stores the responseMap in LoadCSVHandler to access later in SearchCSVHandler
   * and ViewCSVHandler.
   * @param responseMap HashMap storing the Parsed CSV Data
   */
  public void setMap(Map<String, List<List<String>>> responseMap) {
    this.responseMap = responseMap;
  }

  /**
   * This returns the responseMap stored from LoadCSVHandler.
   * @return
   */
  public Map<String, List<List<String>>> getMap() {
    return this.responseMap;
  }
}

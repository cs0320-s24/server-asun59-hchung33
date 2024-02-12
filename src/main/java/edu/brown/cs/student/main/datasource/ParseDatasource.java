package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.creator.StringCreator;
import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ParseDatasource implements Datasource {
  List<List<String>> parsed;
  private Map<String, List<List<String>>> responseMap;

  public ParseDatasource() {
    // parse shit
  }

  @Override
  public void parse(String path) {
    CreatorFromRow<List<String>> stringCreator = new StringCreator();
    try {
      FileReader reader = new FileReader(path);
      CSVParser<List<String>> parser = new CSVParser<List<String>>(reader, stringCreator);
      this.parsed = parser.parse();
    } catch (FactoryFailureException | IOException e) {
      System.err.println("Error: " + e);
    }
  }

  public List<List<String>> getParsed() {
    return this.parsed;
  }

  public void setMap(Map<String, List<List<String>>> responseMap) {
    System.out.println(responseMap);
    this.responseMap = responseMap;
  }

  public Map<String, List<List<String>>> getMap() {
    return this.responseMap;
  }
}

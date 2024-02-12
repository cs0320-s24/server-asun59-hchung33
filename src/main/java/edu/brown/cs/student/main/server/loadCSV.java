package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.creator.StringCreator;
import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class loadCSV {
  public loadCSV(String path) {
    try {
      Reader fReader = new FileReader(path);
      CreatorFromRow<List<String>> creator = new StringCreator();
      CSVParser<List<String>> parser = new CSVParser<List<String>>(fReader, creator);
      // List of Strings in loadCSV
    } catch (FileNotFoundException e) {
      System.err.println("FileNotFound");
    }
  }
}

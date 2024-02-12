package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.FactoryFailureException;
import java.io.IOException;

public interface Datasource {
  public void parse(String path) throws FactoryFailureException, IOException;
}

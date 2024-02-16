package edu.brown.cs.student.backendUnitTesting;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertEquals;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.creator.StringCreator;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
import edu.brown.cs.student.main.datasource.Constants;
import edu.brown.cs.student.main.datasource.DatasourceException;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;


public class unitTests {
   BroadbandDatasource broadbandDatasource = new BroadbandDatasource();
   CacheBroadbandDatasource cacheBroadbandDatasource=
       new CacheBroadbandDatasource(this.broadbandDatasource);
   ParseDatasource parseDatasource= new ParseDatasource();
  /**
   * Testing new code in datasource package
   */
  @Test
  public void testBroadbandDatasource(){
    // Testing get state IDs returns the correct list of states mapped to ID
    List<List<String>> stateIDs = this.broadbandDatasource.getStatesIDs();
    assertEquals (List.of("Alabama","01"), stateIDs.get(1));
    assertEquals (List.of("Delaware","10"),stateIDs.get(10));
    assertEquals (List.of("Kansas","20"),stateIDs.get(19));
    assertEquals (List.of("Montana","30"),stateIDs.get(27));
    assertEquals (List.of("Oklahoma","40"),stateIDs.get(37));
    assertEquals (List.of("Wyoming","56"),stateIDs.get(51));
    // Testing get county IDs returns the correct list of counties mapped to ID
    List<List<String>> countyIds = this.broadbandDatasource.getCountyIDs();
    assertEquals (List.of("Sebastian County, Arkansas", "05", "131"), countyIds.get(1));
    assertEquals (List.of("Morgan County, Colorado", "08", "087"),countyIds.get(140));
    assertEquals (List.of("Sierra County, California", "06", "091"),countyIds.get(400));
    // Testing connect can connect and throw properly
    // Malformed URL
    assertThrows(
        MalformedURLException.class,
        () -> {
          this.broadbandDatasource.connect(new URL("invalid", "invalid", "invalid"));
        });
    // Valid formed but not real URL
    String nonSuccessURL = "http://example.com/non-success";
    assertThrows(DatasourceException.class, () -> {
        this.broadbandDatasource.connect(new URL(nonSuccessURL));
    });
    // Valid URL
    String successURL = "https://httpbin.org/status/200";
    assertDoesNotThrow(() -> {
      this.broadbandDatasource.connect(new URL(successURL));
    });
  }
  @Test
  public void testCacheBroadbandDatasource(){
    //TODO: dont know how to test make cache

    // Test getStateID gets the correct state ID from state ID map created
    assertEquals("41",this.cacheBroadbandDatasource.getStateID("Oregon"));
    assertEquals("01",this.cacheBroadbandDatasource.getStateID("Alabama"));
    assertEquals("36",this.cacheBroadbandDatasource.getStateID("New York"));

    // Test getCountyID gets the correct county ID from county ID map created
    assertEquals("137",this.cacheBroadbandDatasource.getCountyID("Stone County Arkansas"));
    assertEquals("437",this.cacheBroadbandDatasource.getCountyID("Swisher County Texas"));
    assertEquals("169",this.cacheBroadbandDatasource.getCountyID("Saline County Kansas"));
  }

  @Test
  public void testParseDatasource(){
    // Ensures parse works and throws correctly

  }


  /**
   * Testing new/adjusted version of search
   */

  package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.creator.StringCreator;
import edu.brown.cs.student.main.parser.CSVParser;
import edu.brown.cs.student.main.parser.CreatorFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;


    public void parse(String path) throws FactoryFailureException, IOException {
      this.stringCreator = new StringCreator();
      FileReader reader = new FileReader(path);
      CSVParser<List<String>> parser = new CSVParser<List<String>>(reader, stringCreator);
      this.parsed = parser.parse();
    }

    public CreatorFromRow<List<String>> getCreator() {
      return this.stringCreator;
    }

    public List<List<String>> getParsed() {
      return this.parsed;
    }

    public void setMap(Map<String, List<List<String>>> responseMap) {
      this.responseMap = responseMap;
    }

    public Map<String, List<List<String>>> getMap() {
      return this.responseMap;
    }
  }




}

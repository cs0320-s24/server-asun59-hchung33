package edu.brown.cs.student.backendUnitTesting;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertEquals;

import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
import edu.brown.cs.student.main.datasource.DatasourceException;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Test;

public class unitTests {
  BroadbandDatasource broadbandDatasource = new BroadbandDatasource();
  CacheBroadbandDatasource cacheBroadbandDatasource =
      new CacheBroadbandDatasource(this.broadbandDatasource);
  ParseDatasource parseDatasource = new ParseDatasource();
  /** Testing new code in datasource package */
  @Test
  public void testBroadbandDatasource() {
    // Testing get state IDs returns the correct list of states mapped to ID
    List<List<String>> stateIDs = this.broadbandDatasource.getStatesIDs();
    assertEquals(List.of("Alabama", "01"), stateIDs.get(1));
    assertEquals(List.of("Delaware", "10"), stateIDs.get(10));
    assertEquals(List.of("Kansas", "20"), stateIDs.get(19));
    assertEquals(List.of("Montana", "30"), stateIDs.get(27));
    assertEquals(List.of("Oklahoma", "40"), stateIDs.get(37));
    assertEquals(List.of("Wyoming", "56"), stateIDs.get(51));
    // Testing get county IDs returns the correct list of counties mapped to ID
    List<List<String>> countyIds = this.broadbandDatasource.getCountyIDs();
    assertEquals(List.of("Sebastian County, Arkansas", "05", "131"), countyIds.get(1));
    assertEquals(List.of("Morgan County, Colorado", "08", "087"), countyIds.get(140));
    assertEquals(List.of("Sierra County, California", "06", "091"), countyIds.get(400));
    // Testing connect can connect and throw properly
    // Malformed URL
    assertThrows(
        MalformedURLException.class,
        () -> {
          this.broadbandDatasource.connect(new URL("invalid", "invalid", "invalid"));
        });
    // Valid formed but not real URL
    String nonSuccessURL = "http://example.com/non-success";
    assertThrows(
        DatasourceException.class,
        () -> {
          this.broadbandDatasource.connect(new URL(nonSuccessURL));
        });
    // Valid URL
    String successURL = "https://httpbin.org/status/200";
    assertDoesNotThrow(
        () -> {
          this.broadbandDatasource.connect(new URL(successURL));
        });
  }

  /** Testing cacheBoradband data source */
  @Test
  public void testCacheBroadbandDatasource() {
    // Test getStateID gets the correct state ID from state ID map created
    assertEquals("41", this.cacheBroadbandDatasource.getStateID("Oregon"));
    assertEquals("01", this.cacheBroadbandDatasource.getStateID("Alabama"));
    assertEquals("36", this.cacheBroadbandDatasource.getStateID("New York"));

    // Test getCountyID gets the correct county ID from county ID map created
    assertEquals("137", this.cacheBroadbandDatasource.getCountyID("Stone County Arkansas"));
    assertEquals("437", this.cacheBroadbandDatasource.getCountyID("Swisher County Texas"));
    assertEquals("169", this.cacheBroadbandDatasource.getCountyID("Saline County Kansas"));
  }

  /**
   * Testing parse datasource
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  @Test
  public void testParseDatasource() throws IOException, FactoryFailureException {
    // Ensures parse works and throws correctly
    // Throws when invalid file path
    assertThrows(
        IOException.class,
        () -> {
          this.parseDatasource.parse("invalid!");
        });
    // Valid Parsing
    this.parseDatasource.parse("data/house/house.csv");

    assertEquals(
        List.of(
            List.of("Julie's House", "2014", "Red"),
            List.of("Grace's House", "2017", "Green"),
            List.of("Steele's House", "2017", "Blue"),
            List.of("Kassie's House", "2018", "Yellow")),
        this.parseDatasource.getParsed());
  }
}

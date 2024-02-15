package edu.brown.cs.student.backendUnitTesting;

import static org.testng.AssertJUnit.assertEquals;

import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;


public class unitTests {
  /**
   * Testing new code in datasource package
   */
  @Test
  public void testBroadbandDatasource(){
    BroadbandDatasource broadbandDatasource = new BroadbandDatasource();
    // Testing get state IDs returns the correct list of states mapped to ID
    List<List<String>> stateIDs = broadbandDatasource.getStatesIDs();
    assertEquals (List.of("Alabama","01"), stateIDs.get(1));
//    assertEquals (stateIDs.get(10),List.of("Delaware","10"));
//    assertEquals (stateIDs.get(20),List.of("Kansas","20"));
//    assertEquals (stateIDs.get(30),List.of("Montana","30"));
//    assertEquals (stateIDs.get(40),List.of("Oklahoma","40"));
//    assertEquals (stateIDs.get(56),List.of("Wyoming","56"));

  }

  /**
   * Testing new/adjusted version of search
   */

}

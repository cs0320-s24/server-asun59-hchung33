package edu.brown.cs.student.Mock;

import edu.brown.cs.student.main.datasource.BroadbandData;
import edu.brown.cs.student.main.datasource.BroadbandInterface;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This class implements the interface. It allows us to get mock data and prevents us from needing
 * to call the api many times for testing
 */
public class MockBroadbandDatasource implements BroadbandInterface {
  private String data;

  /**
   * Mock class constructor
   *
   * @param data
   */
  public MockBroadbandDatasource(String data) {
    this.data = data;
  }

  /**
   * Override method that returns the data for mock data
   *
   * @param cache CacheBroadbandDatasource that stores recent queries or performs expensive
   *     computation
   * @param stateID ID of the state being queried
   * @param countyID ID of the county being queried
   * @return
   * @throws ExecutionException
   */
  @Override
  public BroadbandData broadbandDataProxy(
      CacheBroadbandDatasource cache, String stateID, String countyID) throws ExecutionException {
    return new BroadbandData(this.data);
  }

  /**
   * Override method for interface
   *
   * @return
   */
  @Override
  public List<List<String>> getStatesIDs() {
    return new ArrayList<List<String>>();
  }

  /**
   * Override method for interface
   *
   * @return
   */
  @Override
  public List<List<String>> getCountyIDs() {
    return new ArrayList<List<String>>();
  }
  /**
   * Override method for interface
   *
   * @return
   */
  @Override
  public BroadbandData getInternetData(String stateID, String countyID) {
    return new BroadbandData(this.data);
  }
}

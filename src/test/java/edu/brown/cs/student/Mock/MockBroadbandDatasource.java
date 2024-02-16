package edu.brown.cs.student.Mock;

import edu.brown.cs.student.main.datasource.BroadbandData;
import edu.brown.cs.student.main.datasource.BroadbandInterface;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MockBroadbandDatasource implements BroadbandInterface {
  private String data;

  public MockBroadbandDatasource(String data) {
    this.data = data;
  }

  @Override
  public BroadbandData broadbandDataProxy(
      CacheBroadbandDatasource cache, String stateID, String countyID) throws ExecutionException {
    return new BroadbandData(this.data);
  }

  @Override
  public List<List<String>> getStatesIDs() {
    return new ArrayList<List<String>>();
  }

  @Override
  public List<List<String>> getCountyIDs() {
    return new ArrayList<List<String>>();
  }

  @Override
  public BroadbandData getInternetData(String stateID, String countyID) {
    return new BroadbandData(this.data);
  }

  public void setNUll() {
    this.data = null;
  }
}

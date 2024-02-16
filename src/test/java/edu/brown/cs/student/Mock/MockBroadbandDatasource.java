package edu.brown.cs.student.Mock;

import edu.brown.cs.student.main.datasource.BroadbandData;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MockBroadbandDatasource implements BroadbandData {
  private List<String> data;

  public MockBroadbandDatasource(List<String> data) {
    this.data = data;
  }

  public List<String> broadbandDataProxy(
      CacheBroadbandDatasource cache, String stateID, String countyID) throws ExecutionException {
    return this.data;
  }
  public void setNUll(){
    this.data= null;
  }
}

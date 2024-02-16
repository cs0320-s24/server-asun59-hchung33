package edu.brown.cs.student.main.datasource;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface BroadbandInterface {
  BroadbandData broadbandDataProxy(CacheBroadbandDatasource cache, String stateID, String countyID)
      throws ExecutionException;

  List<List<String>> getStatesIDs();

  List<List<String>> getCountyIDs();

  BroadbandData getInternetData(String stateID, String countyID);
}

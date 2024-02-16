package edu.brown.cs.student.main.datasource;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface BroadbandData {
    List<String> broadbandDataProxy(
            CacheBroadbandDatasource cache, String stateID, String countyID) throws ExecutionException;
}

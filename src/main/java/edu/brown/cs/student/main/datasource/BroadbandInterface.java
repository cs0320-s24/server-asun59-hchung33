package edu.brown.cs.student.main.datasource;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This is the interface implemented by BroadbandDatasource and MockBroadbandDatasource.
 */
public interface BroadbandInterface {

  /**
   * This is a method that handles caching.
   * @param cache CacheBroadbandDatasource that stores recent queries or performs expensive
   *     computation
   * @param stateID ID of the state being queried
   * @param countyID ID of the county being queried
   * @return List of String which is the broadband data
   * @throws ExecutionException When there is an error while retrieving the county data
   */
  BroadbandData broadbandDataProxy(CacheBroadbandDatasource cache, String stateID, String countyID)
      throws ExecutionException;

  /**
   * This access the ACS API to get the list of list of strings that contain State name and its
   * respective codes. This information is stored so that when the user queries for a specific
   * state, we can retrieve the state code to make the query for broadband percentage data.
   *
   * @return List of List of Strings containing StateIDs
   */
  List<List<String>> getStatesIDs();

  /**
   * This access the ACS API to get the list of list of strings that contain County name and its
   * respective codes. This information is stored so that when the user queries for a specific
   * county, we can retrieve the county code to make the query for broadband percentage data.
   *
   * @return List of List of Strings containing CountyIDs
   */
  List<List<String>> getCountyIDs();

  /**
   * This queries the broadband percentage data by taking in stateID and countyID, making the API
   * request, and converting the response into Json.
   *
   * @param stateID ID for the state
   * @param countyID ID for the county
   * @return List of List of Strings which is the resulting data
   */
  BroadbandData getInternetData(String stateID, String countyID);
}

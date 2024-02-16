package edu.brown.cs.student.main.datasource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents the ACS datasource, rather than incorporating it directly into your
 * server’s handler
 */
public class CacheBroadbandDatasource {
  Map<String, String> stateMap;
  Map<String, String> countyMap;
  BroadbandInterface original;
  LoadingCache<String, BroadbandData> cache;

  /**
   * This is the Proxy class that serves as an intermediary between the Server and the
   * Handler. Here, we get both HashMaps containing state to ID and county to ID.
   * @param original This is BroadbandDatasource implementing BroadbandInterface
   */
  public CacheBroadbandDatasource(BroadbandInterface original) {
    this.original = original;
    this.stateMap = new HashMap<>();
    this.countyMap = new HashMap<>();
    this.stateToMap(original.getStatesIDs());
    this.countyToMap(original.getCountyIDs());
  }

  /**
   * This method sets up the LoadingCache from Google's Guava library.
   * The maximum size is 100 entries, and the expiration policy is determined
   * by external developers through a Constants class. It initially accesses
   * String key from the LoadingCache, and if it cannot find the corresponding
   * BroadbandData, then it makes an API query by calling getInternetData.
   */
  public void makeCache() {
    LoadingCache<String, BroadbandData> cache =
        CacheBuilder.newBuilder()
            .maximumSize(Constants.MAX_SIZE)
            .expireAfterWrite(Constants.EXPIRE_TIME_IN_SECONDS, Constants.TIME_SECOND)
            .build(
                new CacheLoader<>() {
                  public BroadbandData load(String key) {
                    return original.getInternetData(key.substring(0, 2), key.substring(2));
                  }
                });
    this.cache = cache;
  }

  /**
   * This method returns the LoadingCache.
   * @return LoadingCache
   */
  public LoadingCache<String, BroadbandData> getCache() {
    return this.cache;
  }

  /**
   * This method returns the ID of the inputted state.
   * @param name Name of the state the user wish to get the ID of
   * @return The ID of the state
   */
  public String getStateID(String name) {
    return this.stateMap.get(name);
  }

  /**
   * This method returns the ID of the inputted County.
   * @param stateCounty Name of the county and state of the county we want to get the ID of
   * @return The ID of the county
   */
  public String getCountyID(String stateCounty) {
    return this.countyMap.get(stateCounty);
  }

  /**
   * This method converts the List of List of Strings containing states and their corresponding
   * IDs into a HashMap of state to ID.
   * @param states List of List of Strings containing states and their corresponding IDs
   *               from API query
   */
  private void stateToMap(List<List<String>> states) {
    for (List<String> s : states) {
      this.stateMap.put(s.get(0), s.get(1));
    }
  }

  /**
   * This method converts the List of List of Strings containing counties and their corresponding
   * IDs into a HashMap county to ID.
   * @param county List of List of Strings containing counties and their corresponding IDs
   *               from API query
   */
  private void countyToMap(List<List<String>> county) {
    for (int i = 1; i < county.size(); i++) {
      List<String> s = county.get(i);
      String wholeLoc = s.get(0).split(", ")[0] + " " + s.get(0).split(", ")[1];
      this.countyMap.put(wholeLoc, s.get(2));
    }
  }
}

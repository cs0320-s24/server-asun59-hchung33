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
  Map<String, String> countMap;
  BroadbandDatasource original;
  LoadingCache<String, List<String>> cache;

  public CacheBroadbandDatasource(BroadbandDatasource original) {
    this.original = original;
    this.stateMap = new HashMap<>();
    this.countyMap = new HashMap<>();
    this.stateToMap(original.getStatesIDs());
    this.countyToMap(original.getCountyIDs());
  }

  public void makeCache() {
    LoadingCache<String, List<String>> cache =
        CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Constants.EXPIRE_TIME_IN_SECONDS, Constants.TIME_SECOND)
            .build(
                new CacheLoader<>() {
                  public List<String> load(String key) {
                    return original.getInternetData(key.substring(0, 2), key.substring(2)).get(1);
                  }
                });
    this.cache = cache;
  }

  public LoadingCache<String, List<String>> getCache() {
    return this.cache;
  }

  public String getStateID(String name) {
    return this.stateMap.get(name);
  }

  public String getCountyID(String stateCounty) {
    return this.countyMap.get(stateCounty);
  }

  private void stateToMap(List<List<String>> states) {
    for (List<String> s : states) {
      this.stateMap.put(s.get(0), s.get(1));
    }
  }

  private void countyToMap(List<List<String>> county) {
    for (int i = 1; i < county.size(); i++) {
      List<String> s = county.get(i);
      String wholeLoc = s.get(0).split(", ")[0] + " " + s.get(0).split(", ")[1];
      this.countyMap.put(wholeLoc, s.get(2));
    }
  }
}

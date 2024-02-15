package edu.brown.cs.student.main.datasource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class that represents the ACS datasource, rather than incorporating it directly into your
 * server’s handler TODO: I THINK I MESSED UP PARSING SO WE NEED TO FIX THAT. OOPS.
 */
public class CacheBroadbandDatasource {
  Map<String, String> stateMap;
  Map<String, String> countyMap;
  BroadbandDatasource original;
  LoadingCache<String, List<String>> cache;

  public CacheBroadbandDatasource(BroadbandDatasource original) {
    this.original = original;
    this.stateMap = new HashMap<String, String>();
    this.countyMap = new HashMap<String, String>();
    this.stateToMap(original.getStates());
    this.countyToMap(original.getCountyIDs());
  }

  public void makeCache() {
    LoadingCache<String, List<String>> cache =
        CacheBuilder.newBuilder()
            .maximumSize(100)
                .expireAfterWrite(Constants.EXPIRE_TIME_IN_SECONDS, Constants.TIME_SECOND)
            .build(
                new CacheLoader<String, List<String>>() {
                  public List<String> load(String key) { // no checked exception
                    return original.getWifiData(key.substring(0, 2), key.substring(2)).get(1);
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

  public String getCountyID(String name) {
    return this.countyMap.get(name);
  }

  private void stateToMap(List<List<String>> states) {
    for (List<String> s : states) {
      this.stateMap.put(s.get(0), s.get(1));
    }
  }

  private void countyToMap(List<List<String>> county) {
    for (int i = 1; i < county.size(); i++) {
      List<String> s = county.get(i);
      if (this.stateMap.get(s.get(0).split(", ")[1]).equals(s.get(1))) {
        this.countyMap.put(s.get(0).split(", ")[0], s.get(2));
      }
    }
  }
}

package edu.brown.cs.student.main.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents the ACS datasource, rather than incorporating it directly into your
 * server’s handler TODO: Have braodband return ACS data source TODO: Fill out this class
 */
public class CachingBroadbandDatasource {
  Map<String, String> stateMap;
  Map<String, String> countyMap;

  public CachingBroadbandDatasource(BroadbandDatasource original) {
    this.stateMap = new HashMap<String, String>();
    this.countyMap = new HashMap<String, String>();
    this.stateToMap(original.getStates());
    this.countyToMap(original.getCountyIDs());
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

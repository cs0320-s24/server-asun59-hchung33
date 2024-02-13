package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
  private static final String API_Key = "808169d08601aed7dba214b43be6999b1909e403";
  private final BroadbandDatasource state;
  private List<List<String>> statesIDs;
  private List<List<String>> countyIDs;

  public BroadbandHandler(BroadbandDatasource state) {
    this.state = state;
    this.statesIDs = this.state.getStates();
    this.countyIDs = this.state.getCountyIDs();
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, String> errorJson = new HashMap<>();
    // Serialize the error message to a JSON string
    Moshi moshiError = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapterError =
        moshiError.adapter(
            Types.newParameterizedType(Map.class, String.class, String.class)); // ERROR HANDLING

    Moshi moshiReturn = new Moshi.Builder().build();
    Map<String, String> responseMap = new HashMap<>();
    JsonAdapter<Map<String, String>> adapterReturn =
        moshiReturn.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    // get census data
    try {
      String state = request.queryParams("state");
      String county = request.queryParams("county");
      if (state == null || county == null) {
        // Bad request! Send an error response.
        responseMap.put("error_type", "missing_parameter");
        return adapterError.toJson(responseMap);
      }
      // get state ID
      String stateID = this.getID(state, this.statesIDs);
      String countyID = this.getID(county, this.countyIDs);
      List<List<String>> wifiData = this.state.getWifiData(stateID, countyID);
      System.out.println(wifiData);

      // get wifi data
      // Time and date data retrieved
      responseMap.put("Time", "1:00");
      responseMap.put("Date", "2:00");
      // State and county data retrieved
      responseMap.put("State", state);
      System.out.println(state);
      responseMap.put("County", county);
      return adapterReturn.toJson(responseMap);
    } catch (Exception e) {
      errorJson.put("error", e.getMessage());
      return adapterError.toJson(errorJson);
    }
  }

  /**
   * Helper method that searches through list of states to get the target state ID
   *
   * @param
   * @return
   */
  private String getID(String place, List<List<String>> iterate) {
    for (int r = 0; r < iterate.size(); r++) {
      if (iterate.get(r).get(0).equalsIgnoreCase(place)) {
        return iterate.get(r).get(1);
      }
    }
    return "fuck";
  }
}

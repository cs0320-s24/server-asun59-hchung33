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

  public BroadbandHandler(BroadbandDatasource state) {
    this.state = state;
    this.statesIDs = this.state.getStates();
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

    String state = request.queryParams("state");
    String year = request.queryParams("year");
    String county = request.queryParams("county");

    String variable = "S2802_C03_022E";

    if (state == null || year == null || county == null) {
      // Bad request! Send an error response.
      responseMap.put("error_type", "missing_parameter");
      return adapterError.toJson(responseMap);
    }
    // get census data
    try {
      // TODO: get state ID, Data, time
      // get state ID
      String stateID = this.getStateId(state);
      if (statesIDs.equals(-1)) {
        errorJson.put("error", "Invalid State");
        return adapterError.toJson(errorJson);
      }

      // get wifi data
      // Time and date data retrieved
      responseMap.put("Time", "1:00");
      responseMap.put("Date", "2:00");
      // State and county data retrieved
      responseMap.put("State", stateID);
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
   * @param state
   * @return
   */
  private String getStateId(String state) {
    for (int r = 0; r < this.statesIDs.size(); r++) {
      if (this.statesIDs.get(r).get(0).equalsIgnoreCase(state)) {
        return this.statesIDs.get(r).get(1);
      }
    }
    return "fuck";
  }
}

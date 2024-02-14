package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.CachingBroadbandDatasource;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
  private List<List<String>> countyID;
  private CachingBroadbandDatasource proxy;

  public BroadbandHandler(BroadbandDatasource state) {
    this.state = state;
    this.statesIDs = state.getStates();
    this.countyID = state.getCountyIDs();
    this.proxy = new CachingBroadbandDatasource(state);
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, String> errorJson = new HashMap<>();
    // Serialize the error message to a JSON string
    Moshi moshiError = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapterError =
        moshiError.adapter(
            Types.newParameterizedType(Map.class, String.class, String.class)); // ERROR HANDLING
    Map<String, String> errorResponse = new HashMap<>();

    Moshi moshiReturn = new Moshi.Builder().build();
    List<List<String>> responseList = new ArrayList<>();
    Type listListStringType =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapterReturn = moshiReturn.adapter(listListStringType);

    // get census data
    try {
      String state = request.queryParams("state");
      String county = request.queryParams("county");
      if (state == null || county == null) {
        // Bad request! Send an error response.
        errorResponse.put("error_type", "missing_parameter");
        return adapterError.toJson(errorResponse);
      }

      // getting stateID using my hashMap
      String stateID = this.proxy.getStateID(state);
      String countyID = this.proxy.getCountyID(county);
      // get state ID using broadbandhandler parser
      //      String stateID = this.getStateID(state);
      //      String countyID = this.getCountyID(county, state);

      // get wifi data ORIGINAL
      //      List<List<String>> wifiData = this.state.getWifiData(stateID, countyID);

      // TESTING VERSION DELETE ME IF FAILS
      List<String> wifiData = this.state.TESTCACHE(stateID, countyID);

      //      System.out.println(wifiData);
      // Get the current date and time
      LocalDateTime currentDateTime = LocalDateTime.now();
      // You can also format the date and time using a specific pattern
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String formattedDateTime = currentDateTime.format(formatter);

      responseList.add(
          new ArrayList<>(
              Arrays.asList(
                  "Date and time data retrieved: " + formattedDateTime,
                  "state: " + state,
                  "county: " + county)));
      responseList.add(wifiData);
      return adapterReturn.toJson(responseList);
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
  private String getStateID(String state) {
    for (int r = 0; r < this.statesIDs.size(); r++) {
      if (this.statesIDs.get(r).get(0).equalsIgnoreCase(state)) {
        return this.statesIDs.get(r).get(1);
      }
    }
    return "Can't find state ID";
  }

  // THESE R NO LONGER USED BC I HAVE A HASHMAP IN CACHINGBROADBAND WHICH I GUESS..IS A PROXY?
  private String getCountyID(String county, String state) {
    //    System.out.println(county + ", " + state);
    //    System.out.println(this.countyIDs.get(1).get(0));
    for (int r = 0; r < this.countyID.size(); r++) {
      if (this.countyID.get(r).get(0).equalsIgnoreCase(county + ", " + state)) {
        System.out.println("found county");
        return this.countyID.get(r).get(2);
      }
    }
    return "Can't find county ID";
  }
}

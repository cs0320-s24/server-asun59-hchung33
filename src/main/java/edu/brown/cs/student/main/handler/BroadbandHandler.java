package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
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
  private final BroadbandDatasource state;
  private CacheBroadbandDatasource proxy;

  public BroadbandHandler(BroadbandDatasource state) {
    this.state = state;
    this.proxy = new CacheBroadbandDatasource(state);
    this.proxy.makeCache();
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
    // Serialize the ACS data response
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

      // getting stateID and countyID using hashMap
      String stateID = this.proxy.getStateID(state);
      String countyID = this.proxy.getLocID(county + " " + state);
      List<String> wifiData = this.state.broadbandDataProxy(this.proxy, stateID, countyID);
      // Get the current date and time
      LocalDateTime currentDateTime = LocalDateTime.now();
      // Formatting time and date
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
}

package edu.brown.cs.student.main.handler;

import com.google.common.cache.LoadingCache;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.BroadbandData;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.CacheBroadbandDatasource;
import edu.brown.cs.student.main.datasource.DatasourceException;
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

/**
 * This is the BroadbandHandler that serves as an endpoint for getting information on households
 * with broadband access.
 */
public class BroadbandHandler implements Route {
  private final BroadbandDatasource state;
  private CacheBroadbandDatasource proxy;

  /**
   * This is the constructor for the BroadbandHandler class. This takes in a BroadbandDatasource,
   * creates a CacheBroadbandDatasource, and sets up the cache.
   *
   * @param state BroadbandDatasource that tracks data
   */
  public BroadbandHandler(BroadbandDatasource state) {
    this.state = state;
    this.proxy = new CacheBroadbandDatasource(state);
    this.proxy.makeCache();
  }

  /**
   * This handles the request to this endpoint. It takes search queries for a state and a county,
   * and ultimately returns a Json response that provides the user's desired data.
   *
   * @param request Request of the user
   * @param response Response to the user
   * @return Json Object
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, String> errorJson = new HashMap<>();
    // Serialize the error message to a JSON string
    Moshi moshiError = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapterError =
        moshiError.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
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
      if (request.queryParams("state") == null
          || request.queryParams("county") == null
          || request.queryParams("state").isBlank()
          || request.queryParams("county").isBlank()) {
        throw new Exception("Invalid Query");
      }
      String state = request.queryParams("state");
      String county = request.queryParams("county");
      // getting stateID and countyID using hashMap
      String stateID = this.proxy.getStateID(state);
      String countyID = this.proxy.getCountyID(county + " " + state);
      BroadbandData wifiData = this.state.broadbandDataProxy(this.proxy, stateID, countyID);

      String status = "success";
      if (wifiData == null) {
        status = "failure";
        throw new DatasourceException("could not retrieve data");
      }
      // Get the current date and time
      LocalDateTime currentDateTime = LocalDateTime.now();
      // Formatting time and date
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String formattedDateTime = currentDateTime.format(formatter);
      responseList.add(
          new ArrayList<>(
              Arrays.asList(
                  "type: " + status,
                  "Date and time data retrieved: " + formattedDateTime,
                  "state: " + state,
                  "county: " + county,
                  "Percentage: " + wifiData.data())));
      return adapterReturn.toJson(responseList);
    } catch (Exception e) {
      errorJson.put("error", e.getMessage());
      return adapterError.toJson(errorJson);
    }
  }

  /**
   * This is a getter for LoadingCache.
   *
   * @return LoadingCache
   */
  public LoadingCache<String, BroadbandData> getCache() {
    return this.proxy.getCache();
  }
}

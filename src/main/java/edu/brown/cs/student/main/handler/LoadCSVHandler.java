package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the LoadCSVHandler that serves as an endpoint for loading a specified CSV file to the
 * server.
 */
public class LoadCSVHandler implements Route {
  private final ParseDatasource state;
  private List<List<String>> parsed;

  /**
   * This is the constructor for LoadCSVHandler, which takes in a ParseDatasource.
   *
   * @param state ParseDatasource that keeps track of data across CSVHandlers
   */
  public LoadCSVHandler(ParseDatasource state) {
    this.state = state;
  }

  /**
   * This handles the request to this endpoint. It takes a file path and loads the file to the
   * server.
   *
   * @param request Request of the user
   * @param response Response to the user
   * @return Json Object that represents success or failure loading file
   */
  @Override
  public Object handle(Request request, Response response) {
    // return message
    Map<String, String> returnJson = new HashMap<>();
    // Serialize the error message to a JSON string
    Moshi moshiReturn = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapterReturn =
        moshiReturn.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
    try {
      // Add to response map
      Map<String, List<List<String>>> responseMap = new HashMap<>();
      if (request.queryParams("path") == null || request.queryParams("path").isBlank()) {
        throw new Exception("Invalid Query");
      }
      String path = request.queryParams("path");
      this.state.parse(path);
      this.parsed = this.state.getParsed();
      responseMap.put(path, this.parsed);
      this.state.setMap(responseMap);
    } catch (Exception e) {
      returnJson.put("error", e.getMessage());
      return adapterReturn.toJson(returnJson);
    }
    returnJson.put("Success", "File loaded");
    return adapterReturn.toJson(returnJson);
  }
}

package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the ViewCSVHandler that serves as an endpoint for displaying a specified CSV file to the
 * server.
 */
public class ViewCSVHandler implements Route {
  private final ParseDatasource state;

  /**
   * This is the constructor for ViewCSVHandler, which takes in a ParseDatasource.
   *
   * @param state ParseDatasource that keeps track of data across CSVHandlers
   */
  public ViewCSVHandler(ParseDatasource state) {
    this.state = state;
  }

  /**
   * This handles the request to this endpoint. It gets the parsedData stored from LoadCSVHandler
   * and returns as a Json Object.
   *
   * @param request Request of the user
   * @param response Response to the user
   * @return Json Object of the parsed Data.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Serialize the error message to a JSON string
    Moshi moshi = new Moshi.Builder().build();
    Map<String, String> errorJson = new HashMap<>();
    Moshi moshiError = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapterError =
        moshiError.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
    try {
      if (request.queryParams("path") == null || request.queryParams("path").isBlank()) {
        throw new Exception("Invalid Query");
      }
      String path = request.queryParams("path");
      if (this.state.getMap() != null && this.state.getMap().containsKey(path)) {
        // Get the parsed data
        List<List<String>> parsedData = this.state.getParsed();
        // Serialize the parsed data to a JSON string
        Type listListStringType =
            Types.newParameterizedType(
                List.class, Types.newParameterizedType(List.class, String.class));
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListStringType);
        return adapter.toJson(parsedData);
      }
    } catch (Exception e) {
      errorJson.put("error", e.getMessage());
      return adapterError.toJson(errorJson);
    }
    errorJson.put("error", "File can not be viewed");
    return adapterError.toJson(errorJson);
  }
}

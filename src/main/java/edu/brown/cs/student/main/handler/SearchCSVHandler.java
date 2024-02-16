package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import edu.brown.cs.student.main.parser.CSVSearcher;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the SearchCSVHandler that serves as an endpoint for searching through
 * a specific CSV file specified by the user.
 */
public class SearchCSVHandler implements Route {
  private final ParseDatasource state;

  /**
   * This is the constructor for SearchCSVHandler, which takes in a ParseDatasource.
   * @param state ParseDatasource that keeps track of data across CSVHandlers
   */
  public SearchCSVHandler(ParseDatasource state) {
    this.state = state;
  }

  /**
   * This handles the request to this endpoint. It takes in required inputs to the
   * CSVSearcher such as path, toSearch, headerPresent, and columnIDString, and performs
   * search by calling searchCSV. Then, it returns the found rows as a Json Object.
   * @param request Request of the user
   * @param response Response to the user
   * @return Json Object of the found rows
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
      if (request.queryParams("path") == null
          || request.queryParams("toSearch") == null
          || request.queryParams("headerPresent") == null
          || request.queryParams("columnIDString") == null
          || request.queryParams("path").isBlank()
          || request.queryParams("toSearch").isBlank()
          || request.queryParams("headerPresent").isBlank()
          || request.queryParams("columnIDString").isBlank()) {
        throw new Exception("Invalid Query");
      }
      String path = request.queryParams("path");
      String toSearch = request.queryParams("toSearch");
      String headerPresent = request.queryParams("headerPresent");
      String columnIDString =
          request.queryParams("columnIDString"); // only one header will be in list
      if (this.state.getMap() != null && this.state.getMap().containsKey(path)) {
        // Get the parsed data
        List<List<String>> parsedData = this.state.getParsed();
        CSVSearcher<List<String>> searcher = new CSVSearcher<List<String>>(this.state.getCreator());
        boolean headerPresentBool = Boolean.parseBoolean(headerPresent);
        List<List<String>> foundRows =
            searcher.searchCSV(toSearch, headerPresentBool, columnIDString, parsedData);
        // Serialize the output
        Type listListStringType =
            Types.newParameterizedType(
                List.class, Types.newParameterizedType(List.class, String.class));
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListStringType);
        return adapter.toJson(foundRows);
      }
    } catch (Exception e) {
      errorJson.put("error", e.getMessage());
      return adapterError.toJson(errorJson);
    }
    errorJson.put("error", "File can not be searched");
    return adapterError.toJson(errorJson);
  }
}

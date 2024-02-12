package edu.brown.cs.student.main.handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import edu.brown.cs.student.main.parser.CSVSearcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {
  private final ParseDatasource state;

  public SearchCSVHandler(ParseDatasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();

    // Error message
    Map<String, String> errorJson = new HashMap<>();
    // Serialize the error message to a JSON string
    Moshi moshiError = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapterError =
        moshiError.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

    try {
      String path = request.queryParams("path");
      // TODO: get user input based on search
      String toSearch = request.queryParams("toSearch");
      String headerPresent = request.queryParams("headerPresent");
      String columnIsNum = request.queryParams("columnIsNum");
      String columnIDInteger = request.queryParams("columnIDInteger"); // only one header in list
      String columnIDString = request.queryParams("columnIDString"); // only one header will be in list

      if (this.state.getMap().containsKey(path)) {
        // Get the parsed data
        List<List<String>> parsedData = this.state.getParsed();
        // TODO: change search to jsut take in parsed data
        // TODO: search based on input
        // TODO: use most to print out output



      }
    } catch (Exception e) {
      errorJson.put("error", e.getMessage());
      return adapterError.toJson(errorJson);
    }
    errorJson.put("error", "File can not be searched");
    return adapterError.toJson(errorJson);
  }
}

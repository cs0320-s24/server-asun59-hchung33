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

public class ViewCSVHandler implements Route {
  private final ParseDatasource state;

  public ViewCSVHandler(ParseDatasource state) {
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
        moshiError.adapter(
            Types.newParameterizedType(
                Map.class, String.class, String.class)); // might not need two?

    try {
      String path = request.queryParams("path");
      if (this.state.getMap().containsKey(path)) {
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

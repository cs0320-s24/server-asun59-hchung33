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

public class LoadCSVHandler implements Route {
  private final ParseDatasource state;
  private List<List<String>> parsed;

  public LoadCSVHandler(ParseDatasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapType =
        Types.newParameterizedType(
            Map.class,
            String.class,
            Types.newParameterizedType(
                List.class, Types.newParameterizedType(List.class, String.class)));
    JsonAdapter<Map<String, List<List<String>>>> adapter = moshi.adapter(mapType);

    Map<String, List<List<String>>> responseMap = new HashMap<>();

    try {
      String path = request.queryParams("path");
      this.state.parse(path);
      this.parsed = this.state.getParsed();
      System.out.println(this.parsed);
      responseMap.put(path, this.parsed);
      this.state.setMap(responseMap);
    } catch (Exception e) {
     //TODO: add error message
    }

    return adapter.toJson(responseMap);
  }
}

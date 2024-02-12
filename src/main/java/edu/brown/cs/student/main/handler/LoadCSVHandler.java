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
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> mapAdapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String path = request.queryParams("path");
      this.state.parse(path);
      this.parsed = this.state.getParsed();
      responseMap.put(path, this.parsed);
      System.out.println(this.parsed);
    } catch (Exception e) {
      responseMap.put("result", "error");
      responseMap.put("error", e.getMessage());
    }
    return mapAdapter.toJson(responseMap);
  }
}

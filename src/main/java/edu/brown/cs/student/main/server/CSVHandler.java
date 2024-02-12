package edu.brown.cs.student.main.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import spark.Request;
import spark.Response;
import spark.Route;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CSVHandler implements Route {
  private final ParseDatasource state;
  public CSVHandler(ParseDatasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response){
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> mapAdapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String path = request.queryParams("path");
      responseMap.put("path", path);

      ParseDatasource parse = new ParseDatasource();
    } catch (Exception e) {
      responseMap.put("result", "error");
      responseMap.put("error", e.getMessage());
    }
    return mapAdapter.toJson(responseMap);
  }


}

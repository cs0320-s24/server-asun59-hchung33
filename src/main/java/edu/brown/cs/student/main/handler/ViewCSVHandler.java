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
  private List<List<String>> parsed;

  public ViewCSVHandler(ParseDatasource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> mapAdapter = moshi.adapter(mapStringObject);
    Map<String, List<List<String>>> responseMap = new HashMap<>();
    try {
      String path = request.queryParams("path");
      this.state.parse(path);
      this.parsed = this.state.getParsed();
      responseMap.put(path, this.parsed);
      this.state.setMap(responseMap);
      System.out.println(this.parsed);
    } catch (Exception e) {
      //      responseMap.put("result", "error");
      //      responseMap.put("error", e.getMessage());
    }
    String error = "Invalid file path";
    return error;

    ///




//    // Use a Moshi adapter for List<Map<String, Object>> assuming that's what you want
//    Moshi moshi = new Moshi.Builder().build();
//    Type listStringObject = Types.newParameterizedType(List.class, String.class);
//    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
//    Map<String, Object> responseMap = new HashMap<>();
//    try {
//      String path = request.queryParams("path");
//      System.out.println(this.state.getMap().containsKey(path));
//      if (this.state.getMap().containsKey(path)) {
//        List<List<String>> output = this.state.getMap().get(path);
//        System.out.println("hskdbfkshdf");
//        return adapter.toJson(output);
//      }
//    } catch (Exception e) {
//      responseMap.put("result", "error");
//      responseMap.put("error", e.getMessage());
//    }
//    String error = "Invalid file path";
//    return error;
  }
}

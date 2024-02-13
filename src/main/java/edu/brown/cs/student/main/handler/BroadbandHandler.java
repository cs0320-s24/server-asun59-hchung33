package edu.brown.cs.student.main.handler;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.BroadbandDatasource.StateResponse;
import edu.brown.cs.student.main.datasource.ParseDatasource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
    private final static String API_Key= "808169d08601aed7dba214b43be6999b1909e403";
    private final BroadbandDatasource state;
    public BroadbandHandler(BroadbandDatasource state){
        this.state= state;
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, String> errorJson = new HashMap<>();
        // Serialize the error message to a JSON string
        Moshi moshiError = new Moshi.Builder().build();
        JsonAdapter<Map<String, String>> adapterError =
                moshiError.adapter(
                        Types.newParameterizedType(
                                Map.class, String.class, String.class)); // ERROR HANDLING

        Moshi moshiReturn = new Moshi.Builder().build();
        Map<String, String> responseMap = new HashMap<>();
        JsonAdapter<Map<String, String>> adapterReturn =
                moshiReturn.adapter(Types.newParameterizedType(Map.class, String.class, String.class));

        String state = request.queryParams("state");
        String year = request.queryParams("year");
        String county = request.queryParams("county");

        String variable = "S2802_C03_022E";

        if(state == null || year == null || county==null) {
            // Bad request! Send an error response.
            responseMap.put("error_type", "missing_parameter");
            return adapterError.toJson(responseMap);
        }
        // get census data
        try {
            // get state ID
            StateResponse stateID = this.state.getStateIDPublic();
            // Get all data
            int yearInt = Integer.parseInt(year);
//          StateResponse wifiData = this.state.getStateIDPublic();
//          responseMap.put("type", "success");
//          return adapterReturn.toJson(responseMap);
        } catch (Exception e) {
            errorJson.put("error", e.getMessage());
            return adapterError.toJson(errorJson);
        }
        errorJson.put("error", "Can not retrieve data");
        return adapterError.toJson(errorJson);
    }
}

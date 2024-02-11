package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CSVHandler {
    private final CSVDatasource state;

    public CSVHandler(CSVDatasource state){
        this.state = state;
    }


}

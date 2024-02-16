package edu.brown.cs.student.backendIntegrationTesting;

import static org.testng.AssertJUnit.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import edu.brown.cs.student.main.handler.BroadbandHandler;
import edu.brown.cs.student.main.handler.LoadCSVHandler;
import edu.brown.cs.student.main.handler.SearchCSVHandler;
import edu.brown.cs.student.main.handler.ViewCSVHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestAPIHandlers {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }
  // How do i make this final?
  BroadbandDatasource ACSData = new BroadbandDatasource();
  ParseDatasource CSVData = new ParseDatasource();
  JsonAdapter<Map<String, String>> mapAdapter;
  JsonAdapter<List<List<String>>> listAdapter;

  @BeforeEach
  public void setup() {
    Moshi moshi = new Moshi.Builder().build();
    this.mapAdapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
    Moshi moshi2 = new Moshi.Builder().build();
    this.listAdapter =
        moshi2.adapter(
            Types.newParameterizedType(
                List.class, Types.newParameterizedType(List.class, String.class)));
    // Re-initialize state, etc. for _every_ test method run
    this.ACSData = new BroadbandDatasource();
    this.CSVData = new ParseDatasource();

    ParseDatasource datasource = new ParseDatasource();
    Spark.get("loadCSVHandler", new LoadCSVHandler(datasource));

    Spark.get("viewCSVHandler", new ViewCSVHandler(datasource));

    Spark.get("searchCSVHandler", new SearchCSVHandler(datasource));

    BroadbandDatasource broadbandDatasource = new BroadbandDatasource();
    Spark.get("broadbandDatasource", new BroadbandHandler(broadbandDatasource));

    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The default method is "GET", which is what we're using here.
    clientConnection.setRequestMethod("GET");
    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testLoadAPI() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest("loadCSVHandler?" + "path=data/house/house.csv");
    // API connection works
    assertEquals(200, clientConnection1.getResponseCode());
    // Expected response
    Map<String, String> response =
        this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals("File loaded", response.get("Success"));
    clientConnection1.disconnect();

    // Correct Error message for invalid File path
    HttpURLConnection clientConnection2 = tryRequest("loadCSVHandler?" + "path=FAKE");
    assertEquals(200, clientConnection2.getResponseCode());
    // Expected response
    response = this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals("FAKE (No such file or directory)", response.get("error"));
    clientConnection2.disconnect();

    // Correct Error message when no query entered
    HttpURLConnection clientConnection3 = tryRequest("loadCSVHandler?" + "MissingQuery!");
    assertEquals(200, clientConnection3.getResponseCode());
    // Expected response
    response = this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
    assertEquals("Invalid Query", response.get("error"));
    clientConnection3.disconnect();

    // Correct Error message when incorrect query entered
    HttpURLConnection clientConnection4 = tryRequest("loadCSVHandler?" + "path");
    assertEquals(200, clientConnection4.getResponseCode());
    // Expected response
    response = this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));
    assertEquals("Invalid Query", response.get("error"));
    clientConnection4.disconnect();
  }

  @Test
  public void testViewAPI() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest("viewCSVHandler?" + "path=data/house/house.csv");
    // API connection works
    assertEquals(200, clientConnection1.getResponseCode());
    // Test error because no file loaded
    Map<String, String> response =
        this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals("File can not be viewed", response.get("error"));
    clientConnection1.disconnect();

    // Valid view and output because file loaded
    HttpURLConnection loadConnect = tryRequest("loadCSVHandler?path=data/house/house.csv");
    assertEquals(200, loadConnect.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("viewCSVHandler?path=data/house/house.csv");
    assertEquals(200, clientConnection2.getResponseCode());
    List<List<String>> listResponse =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals(
        listResponse,
        List.of(
            List.of("Julie's House", "2014", "Red"),
            List.of("Grace's House", "2017", "Green"),
            List.of("Steele's House", "2017", "Blue"),
            List.of("Kassie's House", "2018", "Yellow")));

    // Invalid view after trying to view different File
    HttpURLConnection clientConnection3 =
        tryRequest("viewCSVHandler?path=data/census/income_by_race.csv");
    assertEquals(200, clientConnection2.getResponseCode());
    response = this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
    assertEquals("File can not be viewed", response.get("error"));
    clientConnection3.disconnect();
    loadConnect.disconnect();
    clientConnection2.disconnect();

    // Invalid because bad query
    HttpURLConnection clientConnection4 = tryRequest("viewCSVHandler?WRONG");
    assertEquals(200, clientConnection4.getResponseCode());
    response = this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));
    assertEquals("Invalid Query", response.get("error"));
    clientConnection4.disconnect();
  }

  @Test
  public void testSearchHandler() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest(
            "searchCSVHandler?"
                + "path=data/census/dol_ri_earnings_disparity.csv"
                + "&toSearch=Black&headerPresent=true&columnIDString=1");
    // API connection works
    assertEquals(200, clientConnection1.getResponseCode());
    // Test error because no file loaded
    Map<String, String> response =
        this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals("File can not be searched", response.get("error"));
    clientConnection1.disconnect();
  }
}

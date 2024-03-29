package edu.brown.cs.student.backendIntegrationTesting;

import static org.testng.AssertJUnit.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.Mock.MockBroadbandDatasource;
import edu.brown.cs.student.main.datasource.*;
import edu.brown.cs.student.main.handler.BroadbandHandler;
import edu.brown.cs.student.main.handler.LoadCSVHandler;
import edu.brown.cs.student.main.handler.SearchCSVHandler;
import edu.brown.cs.student.main.handler.ViewCSVHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.*;
import spark.Spark;

public class TestAPIHandlers {

  /** Set up all variables and servers before running integration test */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  BroadbandDatasource ACSData = new BroadbandDatasource();
  ParseDatasource CSVData = new ParseDatasource();
  JsonAdapter<Map<String, String>> mapAdapter;
  JsonAdapter<List<List<String>>> listAdapter;

  BroadbandHandler broadbandHandler;
  /** Starting servers before testing */
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
    this.broadbandHandler = new BroadbandHandler(broadbandDatasource);
    Spark.get("broadbandDatasource", this.broadbandHandler);

    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** Clean up after calls */
  @AfterEach
  public void tearUp() {
    Spark.unmap("loadCSVHandler");
    Spark.awaitStop();
    Spark.unmap("viewCSVHandler");
    Spark.awaitStop();
    Spark.unmap("searchCSVHandler");
    Spark.awaitStop();
    Spark.unmap("broadbandDatasource");
    Spark.awaitStop();
  }

  /**
   * Helper method to make HTTP calls
   *
   * @param apiCall
   * @return the client connection status
   * @throws IOException
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The default method is "GET", which is what we're using here.
    clientConnection.setRequestMethod("GET");
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Testing CSV server load handler
   *
   * @throws IOException
   */
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

  /**
   * Testing the CSV Server view handler
   *
   * @throws IOException
   */
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

  /**
   * Testing the CSV server search handler
   *
   * @throws IOException
   */
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

    // File searched with header and colID
    HttpURLConnection loadConnect =
        tryRequest("loadCSVHandler?path=data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnect.getResponseCode());
    HttpURLConnection clientConnection2 =
        tryRequest(
            "searchCSVHandler?"
                + "path=data/census/dol_ri_earnings_disparity.csv"
                + "&toSearch=Black&headerPresent=true&columnIDString=1");
    assertEquals(200, clientConnection2.getResponseCode());
    List<List<String>> listResponse =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals(
        listResponse.get(1), List.of("RI", "Black", "$770.26", "30424.80376", "$0.73", "6%"));

    // File searched with no header
    HttpURLConnection loadConnect2 =
        tryRequest("loadCSVHandler?path=data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnect2.getResponseCode());
    HttpURLConnection clientConnection3 =
        tryRequest(
            "searchCSVHandler?"
                + "path=data/census/dol_ri_earnings_disparity.csv"
                + "&toSearch=Data%20Type&headerPresent=false&columnIDString=1");
    assertEquals(200, clientConnection2.getResponseCode());
    listResponse =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
    assertEquals(
        listResponse.get(1),
        List.of(
            "State",
            "Data Type",
            "Average Weekly Earnings",
            "Number of Workers",
            "Earnings Disparity",
            "Employed Percent"));

    // File searched with no header or col
    HttpURLConnection loadConnect3 =
        tryRequest("loadCSVHandler?path=data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnect3.getResponseCode());
    HttpURLConnection clientConnection4 =
        tryRequest(
            "searchCSVHandler?"
                + "path=data/census/dol_ri_earnings_disparity.csv"
                + "&toSearch=Data%20Type&headerPresent=false&columnIDString=hello");
    assertEquals(200, clientConnection3.getResponseCode());
    listResponse =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));
    assertEquals(
        listResponse.get(1),
        List.of(
            "State",
            "Data Type",
            "Average Weekly Earnings",
            "Number of Workers",
            "Earnings Disparity",
            "Employed Percent"));

    // File searched with target not existing
    HttpURLConnection loadConnect4 =
        tryRequest("loadCSVHandler?path=data/census/dol_ri_earnings_disparity.csv");
    assertEquals(200, loadConnect4.getResponseCode());
    HttpURLConnection clientConnection5 =
        tryRequest(
            "searchCSVHandler?"
                + "path=data/census/dol_ri_earnings_disparity.csv"
                + "&toSearch=NOTREAL&headerPresent=false&columnIDString=hello");
    assertEquals(200, clientConnection5.getResponseCode());
    listResponse =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection5.getInputStream()));
    assertEquals(1, listResponse.size());
  }

  /**
   * Testing ACS API broadbandhandler
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandHandler() throws IOException {
    // Basic census call case
    HttpURLConnection clientConnection1 =
        tryRequest("broadbandDatasource?" + "state=Arkansas&county=Sebastian%20County");
    assertEquals(200, clientConnection1.getResponseCode());
    List<List<String>> response =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals("state: Arkansas", response.get(0).get(2));
    assertEquals("county: Sebastian County", response.get(0).get(3));
    assertEquals("Percentage: 80.0", response.get(0).get(4));

    clientConnection1.disconnect();

    // County overlaps with another states
    HttpURLConnection clientConnection2 =
        tryRequest("broadbandDatasource?" + "state=California&county=Kings%20County");
    assertEquals(200, clientConnection2.getResponseCode());
    response = this.listAdapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals("state: California", response.get(0).get(2));
    assertEquals("county: Kings County", response.get(0).get(3));
    assertEquals("Percentage: 83.5", response.get(0).get(4));
    clientConnection2.disconnect();

    // Invalid state
    HttpURLConnection clientConnection3 =
        tryRequest("broadbandDatasource?" + "state=YEEHAW&county=Kings%20County");
    assertEquals(200, clientConnection3.getResponseCode());
    Map<String, String> responseMap =
        this.mapAdapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
    assertEquals(
        "java.lang.RuntimeException: "
            + "edu.brown.cs.student.main.datasource.DatasourceException: "
            + "unexpected: API connection not success status null",
        responseMap.get("error"));
    clientConnection3.disconnect();
  }

  /**
   * Testing ACS API using mock calls
   *
   * @throws IOException
   */
  @Test
  public void testMock() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest("broadbandDatasource?" + "state=Arkansas&county=Sebastian%20County");
    assertEquals(200, clientConnection1.getResponseCode());
    List<List<String>> response =
        this.listAdapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    // Mock data initialization
    BroadbandInterface mock = new MockBroadbandDatasource("80.0");
    String mockData = mock.getInternetData("05", "131").data();

    // Get the current date and time
    LocalDateTime currentDateTime = LocalDateTime.now();
    // Formatting time and date
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = currentDateTime.format(formatter);

    List<List<String>> mockList = new ArrayList<>();
    mockList.add(
        new ArrayList<>(
            Arrays.asList(
                "type: " + "success",
                "Date and time data retrieved: " + formattedDateTime,
                "state: " + "Arkansas",
                "county: " + "Sebastian County",
                "Percentage: " + mockData)));

    assertEquals(this.listAdapter.toJson(mockList), this.listAdapter.toJson(response));
    clientConnection1.disconnect();
  }

  /**
   * Testing each functions correctly
   *
   * @param <V>
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public <V> void testCache() throws IOException, InterruptedException {
    // Test values will delete after max time reached
    HttpURLConnection clientConnectionSleep =
        tryRequest("broadbandDatasource?" + "state=New%20York&county=Kings%20County");
    assertEquals(200, clientConnectionSleep.getResponseCode());
    clientConnectionSleep.disconnect();
    Thread.sleep(5000);
    assertEquals(new HashMap<>(this.broadbandHandler.getCache().asMap()), Collections.emptyMap());

    // Basic census call case checking cache
    HttpURLConnection clientConnection1 =
        tryRequest("broadbandDatasource?" + "state=Arkansas&county=Sebastian%20County");
    assertEquals(200, clientConnection1.getResponseCode());

    assertEquals("80.0", this.broadbandHandler.getCache().asMap().get("05131").data());

    clientConnection1.disconnect();

    HttpURLConnection clientConnection2 =
        tryRequest("broadbandDatasource?" + "state=California&county=Kings%20County");
    assertEquals(200, clientConnection2.getResponseCode());
    assertEquals("80.0", this.broadbandHandler.getCache().asMap().get("05131").data());
    assertEquals("83.5", this.broadbandHandler.getCache().asMap().get("06031").data());

    clientConnection2.disconnect();

    // Test values will delete once size reaches max (in this case 3)
    HttpURLConnection clientConnection4 =
        tryRequest("broadbandDatasource?" + "state=Arkansas&county=Stone%20County");
    assertEquals(200, clientConnection4.getResponseCode());
    clientConnection4.disconnect();
    // most recent value deleted
    HttpURLConnection clientConnection3 =
        tryRequest("broadbandDatasource?" + "state=New%20York&county=Kings%20County");
    assertEquals(200, clientConnection3.getResponseCode());
    clientConnection3.disconnect();
    assertEquals(3, this.broadbandHandler.getCache().asMap().values().size());
  }
}

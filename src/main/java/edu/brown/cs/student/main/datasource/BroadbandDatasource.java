package edu.brown.cs.student.main.datasource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import okio.Buffer;

public class BroadbandDatasource implements BroadbandData {

  /**
   *  A datasource for statics on broadband use across country via ACS API. This class uses the
   *  API to return results. It has no caching in itself, and is focused on working
   *  with the real API.
   */
  public BroadbandDatasource() {}

  /**
   * This access the ACS API to get the list of list of strings that contain
   * State name and its respective codes. This information is stored so that
   * when the user queries for a specific state, we can retrieve the state code
   * to make the query for broadband percentage data.
   * @return List of List of Strings containing StateIDs
   */
  public List<List<String>> getStatesIDs() {
    try {
      // Make web API call to get state IDs
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      // Serialize output
      Type listListStringType =
          Types.newParameterizedType(
              List.class, Types.newParameterizedType(List.class, String.class));
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListStringType);

      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      if (body == null || body.isEmpty()) {
        throw new DatasourceException("Malformed response from ACS");
      }
      return body;
    } catch (DatasourceException e) {
      throw new RuntimeException(e);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This is a method that handles caching. When the user queries for a specific
   * state and country, it retries that information from the Cache, and if the
   * query does not exist in the Cache (as in it has not been recently queried),
   * it queries the API in an expensive manner.
   * @param cache CacheBroadbandDatasource that stores recent queries or
   *              performs expensive computation
   * @param stateID ID of the state being queried
   * @param countyID ID of the county being queried
   * @return List of String which is the broadband data
   * @throws ExecutionException When there is an error while retrieving the county data
   */
  public List<String> broadbandDataProxy(
      CacheBroadbandDatasource cache, String stateID, String countyID) throws ExecutionException {
    return cache.getCache().get(stateID + countyID);
  }

  /**
   * This access the ACS API to get the list of list of strings that contain
   * County name and its respective codes. This information is stored so that
   * when the user queries for a specific county, we can retrieve the county code
   * to make the query for broadband percentage data.
   * @return
   */
  public List<List<String>> getCountyIDs() {
    try {
      // Make web API call to get the county IDs
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      // Serialize output
      Type listListStringType =
          Types.newParameterizedType(
              List.class, Types.newParameterizedType(List.class, String.class));
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listListStringType);
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      if (body == null || body.isEmpty()) {
        throw new DatasourceException("Malformed response from ACS");
      }
      return body;
    } catch (DatasourceException e) {
      throw new RuntimeException(e);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This queries the broadband percentage data by taking in stateID and countyID, making the
   * API request, and converting the response into Json.
   * @param stateID ID for the state
   * @param countyID ID for the county
   * @return List of List of Strings which is the resulting data
   */
  public List<List<String>> getInternetData(String stateID, String countyID) {
    try {
      // Make web API call to get internet data
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyID
                  + "&in=state:"
                  + stateID);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      // Serialize output
      Type listType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      if (body == null || body.isEmpty()) {
        throw new DatasourceException("Malformed response from ACS");
      }
      return body;
    } catch (DatasourceException e) {
      throw new RuntimeException(e);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed. NOTE: Made public for testing!!!!
   */
  public HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }
}

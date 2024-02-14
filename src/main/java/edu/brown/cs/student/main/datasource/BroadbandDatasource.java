package edu.brown.cs.student.main.datasource;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.concurrent.TimeUnit;
import okio.Buffer;

public class BroadbandDatasource implements Datasource {
  LoadingCache<String, List<List<String>>> cache;

  public BroadbandDatasource() {
    //    this.createCache();
    this.cache = this.altCache();
  }

  public List<List<String>> getStates() {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();

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
   * This method creates the cache from Guava Library, which stores maximum 100 query data. TODO:
   * decide on a real explusion policy
   */
  public void createCache() {
    Cache<String, List<List<String>>> cache =
        CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(30, TimeUnit.SECONDS).build();
    //    this.cache = cache;
  }

  public LoadingCache<String, List<List<String>>> altCache() {
    LoadingCache<String, List<List<String>>> cache =
        CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(
                new CacheLoader<String, List<List<String>>>() {
                  public List<List<String>> load(String key) { // no checked exception
                    return getWifiData(key.substring(0, 2), key.substring(2));
                  }
                });
    return cache;
  }

  public List<List<String>> TESTCACHE(String stateID, String countyID) throws ExecutionException {
    System.out.println(this.cache.asMap());
    return this.cache.get(stateID + countyID);
  }

  public List<List<String>> getCountyIDs() {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
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

  // need rto wrap
  public List<List<String>> getWifiData(String stateID, String countyID) {
    try {
      System.out.println("in g" + "et wifi data");
      System.out.println(stateID);
      System.out.println(countyID);

      //      if (this.cache.asMap().containsKey(stateID + countyID)) {
      //        System.out.println(this.cache.asMap());
      //        System.out.println("I will be getting from cache" + stateID + countyID);
      //        return this.cache.asMap().get(stateID + countyID);
      //      }

      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyID
                  + "&in=state:"
                  + stateID);
      System.out.println("should work");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();

      Type listType = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      System.out.println(body);

      if (body == null || body.isEmpty()) {
        throw new DatasourceException("Malformed response from ACS");
      }

      //      this.cache.get(stateID + countyID);
      // Store into cache since we couldn't find it before
      //      this.cache.put(stateID + countyID, body);
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
   * needed.
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
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

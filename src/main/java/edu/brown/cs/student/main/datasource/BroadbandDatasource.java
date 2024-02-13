package edu.brown.cs.student.main.datasource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.FactoryFailureException;
import okio.Buffer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class BroadbandDatasource implements Datasource {
  public BroadbandDatasource(){

  }
  private static void getWifiData(){

  }


  public StateResponse getStateIDPublic(){
    return getStateID();
  }
  public static StateResponse getStateID(){
    try{
      URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();

      JsonAdapter<StateResponse> adapter = moshi.adapter(StateResponse.class).nonNull();
      StateResponse body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      if(body == null || body.properties() == null || body.properties().NAME() == null)
        throw new DatasourceException("Malformed response from ACS");
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
   * Private helper method; throws IOException so different callers
   * can handle differently if needed.
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if(! (urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if(clientConnection.getResponseCode() != 200)
      throw new DatasourceException("unexpected: API connection not success status "+clientConnection.getResponseMessage());
    return clientConnection;
  }

  public record StateResponse(StateResponseProperties properties) { }
  // Note: case matters! "gridID" will get populated with null, because "gridID" != "gridId"
  public record StateResponseProperties(String NAME, String stateID) {}
}

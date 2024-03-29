package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.datasource.BroadbandDatasource;
import edu.brown.cs.student.main.datasource.ParseDatasource;
import edu.brown.cs.student.main.handler.BroadbandHandler;
import edu.brown.cs.student.main.handler.LoadCSVHandler;
import edu.brown.cs.student.main.handler.SearchCSVHandler;
import edu.brown.cs.student.main.handler.ViewCSVHandler;
import spark.Spark;

/** This is the main class for Server. */
public class Server {
  static final int port = 5300;

  /**
   * This is the constructor for the server. This runs the port, and sets up each Handler to receive
   * user input at each endpoint. Following endpoints are set here: LoadCSVHandler, ViewCSVHandler,
   * SearchCSVHandler, and BroadbandHandler. Afterward, Spark waits for the server.
   */
  public Server() {
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    ParseDatasource datasource = new ParseDatasource();
    LoadCSVHandler loadCSVHandler = new LoadCSVHandler(datasource);
    Spark.get("loadCSVHandler", loadCSVHandler);

    ViewCSVHandler viewCSVHandler = new ViewCSVHandler(datasource);
    Spark.get("viewCSVHandler", viewCSVHandler);

    SearchCSVHandler searchCSVHandler = new SearchCSVHandler(datasource);
    Spark.get("searchCSVHandler", searchCSVHandler);

    BroadbandDatasource broadbandDatasource = new BroadbandDatasource();
    BroadbandHandler broadbandHandler = new BroadbandHandler(broadbandDatasource);
    Spark.get("broadbandDatasource", broadbandHandler);

    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * This is main method for the program. It constructs the Server and prints an informative message
   * indicating that server has started at port number above.
   *
   * @param args Arguments for the Server
   */
  public static void main(String[] args) {
    new Server();
    System.out.println("Server started at http://localhost:" + port);
  }
}

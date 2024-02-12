package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.datasource.ParseDatasource;
import edu.brown.cs.student.main.handler.LoadCSVHandler;
import edu.brown.cs.student.main.handler.ViewCSVHandler;
import spark.Spark;

public class Server {
  static final int port = 5300;

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

    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    new Server();
    System.out.println("Server started at http://localhost:" + port);
  }
}

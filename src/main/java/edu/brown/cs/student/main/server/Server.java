package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.datasource.ParseDatasource;
import spark.Spark;

public class Server {
  static final int port = 3232;

  public Server() {
    Spark.port(port);
      after(
              (request, response) -> {
                  response.header("Access-Control-Allow-Origin", "*");
                  response.header("Access-Control-Allow-Methods", "*");
              });

      CSVHandler handler = new CSVHandler(new ParseDatasource());
      Spark.get("csv", handler);



      Spark.init();
      Spark.awaitInitialization();

  }

  public static void main(String[] args) {


  }
}

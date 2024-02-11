package edu.brown.cs.student.main.server;

import spark.Spark;

import static spark.Spark.after;

public class Server {
  static final int port = 3232;

  public Server(){
    Spark.port(port);
  }
  public static void main(String[] args) {
    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

  }
}

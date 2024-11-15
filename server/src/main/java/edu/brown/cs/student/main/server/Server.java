package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.handlers.ClearPinsHandler;
import edu.brown.cs.student.main.server.handlers.ListPinsHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import spark.Filter;
import spark.Spark;

public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    // Allow cross-origin requests
    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    StorageInterface firebaseUtils;
    firebaseUtils = new FirebaseUtilities();

    // Set up routes for handling pins
    Spark.get("/add-pin", new AddPinHandler(firebaseUtils));
    Spark.get("/list-pins", new ListPinsHandler(firebaseUtils));
    Spark.get("/clear-pins", new ClearPinsHandler(firebaseUtils));

    Spark.notFound(
        (request, response) -> {
          response.status(404);
          return "404 Not Found - The requested endpoint does not exist.";
        });

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  public static void main(String[] args) {
    setUpServer();
  }
}

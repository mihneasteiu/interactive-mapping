package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.handlers.ClearPinsHandler;
import edu.brown.cs.student.main.server.handlers.GetAreaHandler;
import edu.brown.cs.student.main.server.handlers.GetDataHandler;
import edu.brown.cs.student.main.server.handlers.ListPinsHandler;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.JSONParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/**
 * Main server class that sets up and runs the server for the project.
 * Utilizes the Spark web framework to create and handle HTTP requests.
 */
public class Server {

  /**
   * Sets up the server, configures routes, and starts the Spark server.
   * 
   * @throws FileNotFoundException if the required JSON data file is not found.
   */
  public static void setUpServer() throws FileNotFoundException {

    // Load the geo map data
    JSONParser myDataSource = new JSONParser("data/fullDownload.json");
    GeoMapCollection geomapCollection = myDataSource.getData();

    // Set server port
    int port = 3232;
    Spark.port(port);

    // Configure CORS headers to allow cross-origin requests
    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });
    
    // Initialize Firebase utilities
    StorageInterface firebaseUtils;
    try {
      firebaseUtils = new FirebaseUtilities();

      // Define routes for various handlers
      Spark.get("addPin", new AddPinHandler(firebaseUtils));
      Spark.get("getPins", new ListPinsHandler(firebaseUtils));
      Spark.get("clearPins", new ClearPinsHandler(firebaseUtils));
      Spark.get("getData", new GetDataHandler(geomapCollection));
      Spark.get("getArea", new GetAreaHandler(geomapCollection));

      // Initialize and start the Spark server
      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  /**
   * Main method to run the server.
   *
   * @param args Command line arguments (not used).
   * @throws FileNotFoundException if the required JSON data file is not found.
   */
  public static void main(String[] args) throws FileNotFoundException {
    setUpServer();
  }
}

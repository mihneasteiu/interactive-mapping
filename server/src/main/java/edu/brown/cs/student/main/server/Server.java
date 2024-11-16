package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.GetAreaHandler;
import edu.brown.cs.student.main.server.handlers.GetDataHandler;
import edu.brown.cs.student.main.server.parserParameterizedTypes.JSONParser;
import edu.brown.cs.student.main.server.parserParameterizedTypes.MapCollection.GeoMapCollection;
import java.io.FileNotFoundException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() throws FileNotFoundException {

    JSONParser myDataSource = new JSONParser("data/fullDownload.json");
    GeoMapCollection geomapCollection = myDataSource.getData();

    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });
    StorageInterface firebaseUtils;
    try {
      firebaseUtils = new FirebaseUtilities();

      Spark.get("addPin", new AddPinHandler(firebaseUtils));
      Spark.get("getPins", new ListPinsHandler(firebaseUtils));
      Spark.get("clearPins", new ClearPinsHandler(firebaseUtils));
      Spark.get("getData", new GetDataHandler(geomapCollection));
      Spark.get("getArea", new GetAreaHandler(geomapCollection));

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
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) throws FileNotFoundException {
    setUpServer();
  }
}

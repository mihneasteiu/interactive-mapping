package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler that retrieves all the pins stored in the system and returns them as a list of locations.
 * This handler fetches user pins from the storage system and processes them into a list of geographic coordinates.
 */
public class ListPinsHandler implements Route {

  /**
   * The storage handler responsible for interacting with the storage system.
   */
  public StorageInterface storageHandler;

  /**
   * Constructs a ListPinsHandler with the provided storage handler.
   *
   * @param storageHandler The storage handler used to interact with the data storage.
   */
  public ListPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the HTTP request to retrieve all stored pins. It fetches user pins from the storage system,
   * processes the coordinates, and returns them in a list format. The method returns a success response with 
   * the list of pins or an error message if an exception occurs.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A JSON response containing the list of pins or an error message.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Fetch all user pins from storage
      List<Map<String, Object>> users = this.storageHandler.getAllPins();
      List<List<String>> pins = new ArrayList<>();

      // Process each user and extract pin location if available
      for (Map<String, Object> user : users) {
        if (user.get("pin") == null) {
          continue; // Skip users without a pin
        }
        String pinLocation = user.get("pin").toString();
        List<String> location = Arrays.asList(pinLocation.split(","));
        pins.add(location);
      }

      // Add the list of pins to the response map
      responseMap.put("pins", pins);
    } catch (Exception e) {
      e.printStackTrace();
      // Handle errors and add an error message to the response
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    
    // Return the response as a JSON string
    return Utils.toMoshiJson(responseMap);
  }
}

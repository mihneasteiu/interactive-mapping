package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler to add a pin (location) for a user. This class processes the request,
 * validates the parameters, and stores the pin in a storage system.
 */
public class AddPinHandler implements Route {
  
  /**
   * The storage handler used to interact with the storage system.
   */
  public StorageInterface storageHandler;

  /**
   * Constructs an AddPinHandler with a given storage handler.
   *
   * @param storageHandler The storage handler to interact with the data storage.
   */
  public AddPinHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the HTTP request to add a pin for a user. It expects the user ID, latitude, 
   * and longitude as query parameters. If the parameters are valid, it stores the pin 
   * in the database and returns a success response. Otherwise, it returns an error message.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return The response in JSON format, indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Get query parameters from the request
      String uid = request.queryParams("uid");
      String ltd = request.queryParams("ltd");
      String lng = request.queryParams("lng");
      
      // Validate if all parameters are provided
      if (uid == null || ltd == null || lng == null) {
        throw new Exception("Please enter all parameters");
      }
      
      // Parse latitude and longitude
      double latitude;
      double longitude;
      try {
        latitude = Double.parseDouble(ltd);
        longitude = Double.parseDouble(lng);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Latitude and longitude must be valid numbers");
      }
      
      // Check if the latitude and longitude are within valid ranges
      if (latitude < -90 || latitude > 90) {
        throw new IllegalArgumentException("Latitude must be between -90 and 90");
      }
      if (longitude < -180 || longitude > 180) {
        throw new IllegalArgumentException("Longitude must be between -180 and 180");
      }
      
      // Prepare data to store the pin
      Map<String, Object> data = new HashMap<>();
      data.put("pin", ltd + "," + lng);
      data.put("userId", uid); // Store the user ID with the pin
      data.put("timestamp", System.currentTimeMillis()); // Add timestamp for ordering

      System.out.println("Adding coordinates: " + lng + ", " + ltd + " for user: " + uid);

      // Generate a unique pin ID and store the data in the storage system
      int pinCount = this.storageHandler.getCollection(uid, "pins").size();
      String pinId = "pins-" + pinCount;

      this.storageHandler.addDocument(uid, "pins", pinId, data);

      // Return success response
      responseMap.put("response_type", "success");
      responseMap.put("pin", "latitude: " + ltd + ", longitude: " + lng);
      responseMap.put("userId", uid);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    // Convert the response map to JSON format and return
    return Utils.toMoshiJson(responseMap);
  }
}
package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler to add a pin (location) for a user. This class processes the request,
 * validates the parameters, and stores the pin in a storage system.
 */
public class AddPinHandler implements Route {
  
  /**
   * The storage handler used to interact with the storage system.
   */
  public StorageInterface storageHandler;

  /**
   * Constructs an AddPinHandler with a given storage handler.
   *
   * @param storageHandler The storage handler to interact with the data storage.
   */
  public AddPinHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the HTTP request to add a pin for a user. It expects the user ID, latitude, 
   * and longitude as query parameters. If the parameters are valid, it stores the pin 
   * in the database and returns a success response. Otherwise, it returns an error message.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return The response in JSON format, indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Get query parameters from the request
      String uid = request.queryParams("uid");
      String ltd = request.queryParams("ltd");
      String lng = request.queryParams("lng");
      
      // Validate if all parameters are provided
      if (uid == null || ltd == null || lng == null) {
        throw new Exception("Please enter all parameters");
      }
      
      // Parse latitude and longitude
      double latitude;
      double longitude;
      try {
        latitude = Double.parseDouble(ltd);
        longitude = Double.parseDouble(lng);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Latitude and longitude must be valid numbers");
      }
      
      // Check if the latitude and longitude are within valid ranges
      if (latitude < -90 || latitude > 90) {
        throw new IllegalArgumentException("Latitude must be between -90 and 90");
      }
      if (longitude < -180 || longitude > 180) {
        throw new IllegalArgumentException("Longitude must be between -180 and 180");
      }
      
      // Prepare data to store the pin
      Map<String, Object> data = new HashMap<>();
      data.put("pin", ltd + "," + lng);
      data.put("userId", uid); // Store the user ID with the pin
      data.put("timestamp", System.currentTimeMillis()); // Add timestamp for ordering

      System.out.println("Adding coordinates: " + lng + ", " + ltd + " for user: " + uid);

      // Generate a unique pin ID and store the data in the storage system
      int pinCount = this.storageHandler.getCollection(uid, "pins").size();
      String pinId = "pins-" + pinCount;

      this.storageHandler.addDocument(uid, "pins", pinId, data);

      // Return success response
      responseMap.put("response_type", "success");
      responseMap.put("pin", "latitude: " + ltd + ", longitude: " + lng);
      responseMap.put("userId", uid);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    // Convert the response map to JSON format and return
    return Utils.toMoshiJson(responseMap);
  }
}

package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler to clear all pins (locations) associated with a user. This class processes the request,
 * verifies the user ID, and removes all pins stored for that user from the storage system.
 */
public class ClearPinsHandler implements Route {

  /**
   * The storage handler used to interact with the storage system.
   */
  public StorageInterface storageHandler;

  /**
   * Constructs a ClearPinsHandler with a given storage handler.
   *
   * @param storageHandler The storage handler to interact with the data storage.
   */
  public ClearPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the HTTP request to clear all pins for a user. It expects the user ID as a query 
   * parameter. If the user ID is valid, it clears all pins for that user and returns a success 
   * message. If there is an error, it returns a failure response with the error message.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return The response in JSON format, indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Get the user ID from the query parameters
      String uid = request.queryParams("uid");
      
      // Validate that the user ID is provided
      if (uid == null || uid.isEmpty()) {
        throw new IllegalArgumentException("User ID is required");
      }
      
      // Log the clearing action
      System.out.println("Clearing pins for user: " + uid);
      
      // Clear all pins associated with the user
      this.storageHandler.clearUser(uid);
      
      // Return success response
      responseMap.put("response_type", "success");
      responseMap.put("message", "All pins cleared for user: " + uid);
    } catch (Exception e) {
      e.printStackTrace();
      // Return failure response in case of an error
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    
    // Convert the response map to JSON format and return
    return Utils.toMoshiJson(responseMap);
  }
}

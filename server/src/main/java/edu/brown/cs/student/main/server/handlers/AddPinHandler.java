package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddPinHandler implements Route {
  public StorageInterface storageHandler;

  public AddPinHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");
      String ltd = request.queryParams("ltd");
      String lng = request.queryParams("lng");
      if (uid == null || ltd == null || lng == null) {
        throw new Exception("Please enter all parameters");
      }
      double latitude;
      double longitude;
      try {
        latitude = Double.parseDouble(ltd);
        longitude = Double.parseDouble(lng);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Latitude and longitude must be valid numbers");
      }
      // Check coordinate ranges
      if (latitude < -90 || latitude > 90) {
        throw new IllegalArgumentException("Latitude must be between -90 and 90");
      }
      if (longitude < -180 || longitude > 180) {
        throw new IllegalArgumentException("Longitude must be between -180 and 180");
      }
      Map<String, Object> data = new HashMap<>();
      data.put("pin", ltd + "," + lng);
      data.put("userId", uid); // Store the user ID with the pin
      data.put("timestamp", System.currentTimeMillis()); // Add timestamp for ordering

      System.out.println("adding coordinates: " + lng + ltd + " for user: " + uid);

      int pinCount = this.storageHandler.getCollection(uid, "pins").size();
      String pinId = "pins-" + pinCount;

      this.storageHandler.addDocument(uid, "pins", pinId, data);

      responseMap.put("response_type", "success");
      responseMap.put("pin", "latitude: " + ltd + ", longitude: " + lng);
      responseMap.put("userId", uid);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}

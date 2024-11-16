package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
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

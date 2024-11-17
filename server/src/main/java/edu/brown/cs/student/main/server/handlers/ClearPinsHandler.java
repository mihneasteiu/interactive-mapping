package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearPinsHandler implements Route {
  public StorageInterface storageHandler;

  public ClearPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");
      if (uid == null || uid.isEmpty()) {
        throw new IllegalArgumentException("User ID is required");
      }
      System.out.println("clearing pins for user: " + uid);
      this.storageHandler.clearUser(uid);
      responseMap.put("response_type", "success");
      responseMap.put("message", "All pins cleared for user: " + uid);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}

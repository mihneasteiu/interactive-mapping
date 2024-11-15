package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddPinHandler implements Route {
  private final StorageInterface storage;

  public AddPinHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");
      double latitude = Double.parseDouble(request.queryParams("latitude"));
      double longitude = Double.parseDouble(request.queryParams("longitude"));

      Map<String, Object> pinData = new HashMap<>();
      pinData.put("userId", uid);
      pinData.put("latitude", latitude);
      pinData.put("longitude", longitude);
      pinData.put("timestamp", System.currentTimeMillis());

      storage.addDocument("pins", uid + "_" + System.currentTimeMillis(), pinData);

      responseMap.put("status", "success");
    } catch (Exception e) {
      responseMap.put("status", "error");
      responseMap.put("message", e.getMessage());
    }
    return responseMap;
  }
}

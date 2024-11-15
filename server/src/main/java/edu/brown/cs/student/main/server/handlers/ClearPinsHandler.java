package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearPinsHandler implements Route {
  private final StorageInterface storage;

  public ClearPinsHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");

      storage.getCollection("pins");
      responseMap.put("status", "success");
    } catch (Exception e) {
      responseMap.put("status", "error");
      responseMap.put("message", e.getMessage());
    }
    return responseMap;
  }
}

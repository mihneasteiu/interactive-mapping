package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListPinsHandler implements Route {
  private final StorageInterface storage;

  public ListPinsHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      List<Map<String, Object>> pins = storage.getCollection("pins");
      responseMap.put("status", "success");
      responseMap.put("pins", pins);
    } catch (Exception e) {
      responseMap.put("status", "error");
      responseMap.put("message", e.getMessage());
    }
    return responseMap;
  }
}

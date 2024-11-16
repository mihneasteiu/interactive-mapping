package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListPinsHandler implements Route {
  public StorageInterface storageHandler;

  public ListPinsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      List<Map<String, Object>> users = this.storageHandler.getAllPins();
      List<List<String>> pins = new ArrayList<>();
      for (Map<String, Object> user : users) {
        if (user.get("pin") == null) {
          continue;
        }
        String pinLocation = user.get("pin").toString();
        List<String> location = Arrays.asList(pinLocation.split(","));
        pins.add(location);
      }
      responseMap.put("pins", pins);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}

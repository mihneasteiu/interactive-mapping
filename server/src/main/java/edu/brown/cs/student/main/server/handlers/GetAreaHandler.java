package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.parserParameterizedTypes.GeoMapAdapter;
import edu.brown.cs.student.main.server.parserParameterizedTypes.MapCollection.GeoMapCollection;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetAreaHandler implements Route {

  GeoMapCollection geomapCollection;
  GeoMapAdapter geoMapAdapter;

  public GetAreaHandler(GeoMapCollection geomapCollection) {
    this.geomapCollection = geomapCollection;
    this.geoMapAdapter = new GeoMapAdapter();
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String keyword = request.queryParams("key");
      if (keyword == null) {
        responseMap.put("response_type", "error");
        responseMap.put("error", "Missing keyword parameters.");
        return Utils.toMoshiJson(responseMap);
      }

      GeoMapCollection collectionResult = GeoFilter.filterByKeyword(this.geomapCollection, keyword);

      return this.geoMapAdapter.toJson(collectionResult);

    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "error");
      responseMap.put("error", e.getMessage());
      return Utils.toMoshiJson(responseMap);
    }
  }
}

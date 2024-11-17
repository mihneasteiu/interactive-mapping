package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.utils.GeoFilter;
import edu.brown.cs.student.main.server.utils.GeoMapAdapter;
import edu.brown.cs.student.main.server.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler to get geographic area information based on a keyword. This class processes the request,
 * filters a geographic map collection using the provided keyword, and returns the filtered data in JSON format.
 */
public class GetAreaHandler implements Route {

  /**
   * The collection of geographic map data.
   */
  GeoMapCollection geomapCollection;
  
  /**
   * The adapter used to convert the geographic map collection to JSON format.
   */
  GeoMapAdapter geoMapAdapter;

  /**
   * Constructs a GetAreaHandler with a given GeoMapCollection.
   *
   * @param geomapCollection The collection of geographic map data to be filtered.
   */
  public GetAreaHandler(GeoMapCollection geomapCollection) {
    this.geomapCollection = geomapCollection;
    this.geoMapAdapter = new GeoMapAdapter();
  }

  /**
   * Handles the HTTP request to retrieve area information filtered by a keyword. It expects the keyword 
   * as a query parameter. If the keyword is missing, it returns an error response. If successful, 
   * it filters the geographic data and returns the filtered results in JSON format.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return The filtered geographic map data in JSON format or an error message.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // Get the keyword from the query parameters
      String keyword = request.queryParams("key");
      
      // Check if the keyword is provided
      if (keyword == null) {
        // Return error response if the keyword is missing
        responseMap.put("response_type", "error");
        responseMap.put("error", "Missing keyword parameters.");
        return Utils.toMoshiJson(responseMap);
      }

      // Filter the map collection using the provided keyword
      GeoMapCollection collectionResult = GeoFilter.filterByKeyword(this.geomapCollection, keyword);

      // Return the filtered data in JSON format
      return this.geoMapAdapter.toJson(collectionResult);

    } catch (Exception e) {
      e.printStackTrace();
      // Return error response in case of an exception
      responseMap.put("response_type", "error");
      responseMap.put("error", e.getMessage());
      return Utils.toMoshiJson(responseMap);
    }
  }
}

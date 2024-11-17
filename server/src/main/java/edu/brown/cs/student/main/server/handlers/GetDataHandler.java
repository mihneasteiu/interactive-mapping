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
 * A handler to retrieve geographic data filtered by a bounding box. This class processes the request,
 * extracts the bounding box parameters (minLat, minLong, maxLat, maxLong), validates them, and returns
 * the filtered data in JSON format based on the provided coordinates.
 */
public class GetDataHandler implements Route {

  /**
   * The collection of geographic map data.
   */
  GeoMapCollection geomapCollection;
  
  /**
   * The adapter used to convert the geographic map collection to JSON format.
   */
  GeoMapAdapter geoMapAdapter;

  /**
   * Constructs a GetDataHandler with a given GeoMapCollection.
   *
   * @param geomapCollection The collection of geographic map data to be filtered.
   */
  public GetDataHandler(GeoMapCollection geomapCollection) {
    this.geomapCollection = geomapCollection;
    this.geoMapAdapter = new GeoMapAdapter();
  }

  /**
   * Handles the HTTP request to retrieve data within a specified bounding box. It expects the bounding
   * box coordinates (minLat, minLong, maxLat, maxLong) as query parameters. The method validates the 
   * coordinates and returns filtered data if the parameters are valid, or an error message if any issues 
   * are encountered.
   *
   * @param request The HTTP request object containing query parameters.
   * @param response The HTTP response object.
   * @return The filtered geographic map data in JSON format or an error message.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // Get query parameters
      String minLatStr = request.queryParams("minLat");
      String minLongStr = request.queryParams("minLong");
      String maxLatStr = request.queryParams("maxLat");
      String maxLongStr = request.queryParams("maxLong");

      // Check if any parameters are missing
      if (minLatStr == null || minLongStr == null || maxLatStr == null || maxLongStr == null) {
        responseMap.put("response_type", "error");
        responseMap.put(
            "error",
            "Missing required parameters. Please provide minLat, minLong, maxLat, and maxLong");
        return Utils.toMoshiJson(responseMap);
      }

      // Parse and validate coordinates
      Double minLat, minLong, maxLat, maxLong;
      try {
        minLat = Double.parseDouble(minLatStr);
        minLong = Double.parseDouble(minLongStr);
        maxLat = Double.parseDouble(maxLatStr);
        maxLong = Double.parseDouble(maxLongStr);
      } catch (NumberFormatException e) {
        responseMap.put("response_type", "error");
        responseMap.put(
            "error", "Invalid coordinate format. All coordinates must be valid numbers");
        return Utils.toMoshiJson(responseMap);
      }

      // Additional coordinate validation
      if (minLat < -90 || minLat > 90 || maxLat < -90 || maxLat > 90) {
        responseMap.put("response_type", "error");
        responseMap.put("error", "Latitude values must be between -90 and 90 degrees");
        return Utils.toMoshiJson(responseMap);
      }

      if (minLong < -180 || minLong > 180 || maxLong < -180 || maxLong > 180) {
        responseMap.put("response_type", "error");
        responseMap.put("error", "Longitude values must be between -180 and 180 degrees");
        return Utils.toMoshiJson(responseMap);
      }

      if (minLat > maxLat) {
        responseMap.put("response_type", "error");
        responseMap.put("error", "minLat must be less than or equal to maxLat");
        return Utils.toMoshiJson(responseMap);
      }

      if (minLong > maxLong) {
        responseMap.put("response_type", "error");
        responseMap.put("error", "minLong must be less than or equal to maxLong");
        return Utils.toMoshiJson(responseMap);
      }

      // Filter the map collection using the bounding box coordinates
      GeoMapCollection collectionResult =
          GeoFilter.filterByBoundingBox(this.geomapCollection, minLong, maxLong, minLat, maxLat);

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

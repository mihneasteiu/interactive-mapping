package edu.brown.cs.student.main.server.utils;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for filtering GeoMapCollection objects based on various criteria,
 * such as bounding box and keyword search.
 */
public class GeoFilter {
  
  /**
   * Filters a GeoMapCollection where ALL coordinates of the features are within the specified bounding box.
   * 
   * @param collection Original GeoMapCollection to filter.
   * @param minLon Minimum longitude of the bounding box.
   * @param maxLon Maximum longitude of the bounding box.
   * @param minLat Minimum latitude of the bounding box.
   * @param maxLat Maximum latitude of the bounding box.
   * @return A new GeoMapCollection containing only the features completely within the bounding box.
   */
  public static GeoMapCollection filterByBoundingBox(
      GeoMapCollection collection, double minLon, double maxLon, double minLat, double maxLat) {
    GeoMapCollection filteredCollection = new GeoMapCollection();
    filteredCollection.type = collection.type;
    filteredCollection.features = new ArrayList<>();

    for (GeoMap feature : collection.features) {
      if (isFeatureCompletelyInBoundingBox(feature, minLon, maxLon, minLat, maxLat)) {
        filteredCollection.features.add(feature);
      }
    }

    return filteredCollection;
  }

  /**
   * Filters a GeoMapCollection by a keyword found in the area description data.
   * 
   * @param collection Original GeoMapCollection to filter.
   * @param keyword The keyword to search for in the area description data.
   * @return A new GeoMapCollection containing only the features that contain the keyword.
   */
  public static GeoMapCollection filterByKeyword(GeoMapCollection collection, String keyword) {
    GeoMapCollection filteredCollection = new GeoMapCollection();
    filteredCollection.type = collection.type;
    filteredCollection.features = new ArrayList<>();

    for (GeoMap feature : collection.features) {
      if (containsKeyword(feature, keyword)) {
        filteredCollection.features.add(feature);
      }
    }

    return filteredCollection;
  }

  /**
   * Checks whether the GeoMap feature contains the specified keyword in its area description data.
   * 
   * @param feature The GeoMap feature to check.
   * @param keyword The keyword to search for.
   * @return True if the feature contains the keyword in its area description, false otherwise.
   */
  private static boolean containsKeyword(GeoMap feature, String keyword) {
    if (feature == null || feature.properties == null) {
      return false;
    }
    Map<String, String> dataMap = feature.properties.area_description_data;
    for (String description : dataMap.values()) {
      if (description.contains(keyword)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a GeoMap feature is completely within the specified bounding box.
   * 
   * @param feature The GeoMap feature to check.
   * @param minLon Minimum longitude of the bounding box.
   * @param maxLon Maximum longitude of the bounding box.
   * @param minLat Minimum latitude of the bounding box.
   * @param maxLat Maximum latitude of the bounding box.
   * @return True if all coordinates of the feature are within the bounding box, false otherwise.
   */
  private static boolean isFeatureCompletelyInBoundingBox(
      GeoMap feature, double minLon, double maxLon, double minLat, double maxLat) {
    Geometry geometry = feature.getGeometry();
    if (geometry == null) return false;
    List<List<List<List<Double>>>> coordinates = geometry.getCoordinates();

    // For each polygon in the multipolygon
    for (List<List<List<Double>>> polygon : coordinates) {
      // For each ring in the polygon (outer ring and holes)
      for (List<List<Double>> ring : polygon) {
        // For each coordinate pair in the ring
        for (List<Double> point : ring) {
          double lon = point.get(0);
          double lat = point.get(1);

          // If ANY point is outside bounds, return false
          if (!isPointInBoundingBox(lon, lat, minLon, maxLon, minLat, maxLat)) {
            return false;
          }
        }
      }
    }
    // All points were inside the bounding box
    return true;
  }

  /**
   * Checks if a given point (longitude, latitude) is within the specified bounding box.
   * 
   * @param lon The longitude of the point.
   * @param lat The latitude of the point.
   * @param minLon Minimum longitude of the bounding box.
   * @param maxLon Maximum longitude of the bounding box.
   * @param minLat Minimum latitude of the bounding box.
   * @param maxLat Maximum latitude of the bounding box.
   * @return True if the point is within the bounding box, false otherwise.
   */
  private static boolean isPointInBoundingBox(
      double lon, double lat, double minLon, double maxLon, double minLat, double maxLat) {
    return lon >= minLon && lon <= maxLon && lat >= minLat && lat <= maxLat;
  }
}

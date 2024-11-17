package edu.brown.cs.student.main.server.utils;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeoFilter {
  /**
   * Filters GeoMaps where ALL coordinates are within a given bounding box
   *
   * @param collection Original GeoMapCollection
   * @param minLon Minimum longitude
   * @param maxLon Maximum longitude
   * @param minLat Minimum latitude
   * @param maxLat Maximum latitude
   * @return New GeoMapCollection containing only features completely within the bounding box
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

  private static boolean isPointInBoundingBox(
      double lon, double lat, double minLon, double maxLon, double minLat, double maxLat) {
    return lon >= minLon && lon <= maxLon && lat >= minLat && lat <= maxLat;
  }
}

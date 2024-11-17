package edu.brown.cs.student.main.server.mapCollection.GeoMap.fields;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import java.util.List;

/**
 * Represents the geometry of a geographical map.
 * This class contains the coordinates that define the shape and structure of the geographical feature.
 * It extends the {@link GeoMap} class to be used within the context of geographical maps.
 */
public class Geometry extends GeoMap {

  /**
   * A list of coordinates that define the geometry of the geographical feature.
   * The coordinates are represented as a four-level nested list structure, where each level contains 
   * lists of Double values representing geographic points.
   */
  public List<List<List<List<Double>>>> coordinates;

  /**
   * Gets the coordinates defining the geometry of the geographical feature.
   * 
   * @return A list of coordinates that defines the geometry of the map.
   */
  public List<List<List<List<Double>>>> getCoordinates() {
    return this.coordinates;
  }
}

package edu.brown.cs.student.main.server.mapCollection;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import java.util.List;

/**
 * Represents a collection of geographical maps.
 * This class holds a type identifier and a list of GeoMap features.
 */
public class GeoMapCollection {

  /**
   * The type of the map collection. 
   * This could be used to categorize or identify the type of geographical maps.
   */
  public String type;

  /**
   * A list of GeoMap objects representing individual geographical maps.
   * Each GeoMap in the list represents a specific geographical feature or area.
   */
  public List<GeoMap> features;
}

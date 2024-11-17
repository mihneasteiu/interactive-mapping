package edu.brown.cs.student.main.server.mapCollection.GeoMap;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;

/**
 * Represents a single geographical map.
 * This class contains the type of the map, its geometric details, 
 * and associated properties.
 */
public class GeoMap {

  /**
   * The type of the geographical map.
   * This could represent the map's classification, such as "Point", "Polygon", etc.
   */
  public String type;

  /**
   * The geometry of the geographical map, represented by a Geometry object.
   * This defines the shape and structure of the geographical feature on the map.
   */
  public Geometry geometry;

  /**
   * The properties of the geographical map, represented by a Property object.
   * This contains key-value pairs that provide additional information about the map.
   */
  public Property properties;

  /**
   * Gets the geometry of this geographical map.
   * 
   * @return The Geometry object representing the map's shape and structure.
   */
  public Geometry getGeometry() {
    return this.geometry;
  }

  /**
   * Gets the properties of this geographical map.
   * 
   * @return The Property object containing additional information about the map.
   */
  public Property getProperty() {
    return this.properties;
  }
}

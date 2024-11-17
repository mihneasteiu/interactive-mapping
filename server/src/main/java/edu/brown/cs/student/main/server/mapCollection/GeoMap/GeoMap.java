package edu.brown.cs.student.main.server.mapCollection.GeoMap;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;

public class GeoMap {
  public String type;
  public Geometry geometry;
  public Property properties;

  public Geometry getGeometry() {
    return this.geometry;
  }

  public Property getProperty() {
    return this.properties;
  }
}

package edu.brown.cs.student.main.server.parserParameterizedTypes.MapCollection.GeoMap;

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

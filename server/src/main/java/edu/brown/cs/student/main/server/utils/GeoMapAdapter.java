package edu.brown.cs.student.main.server.utils;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A custom adapter for serializing and deserializing GeoMapCollection objects using Moshi.
 * This adapter supports converting GeoMapCollection objects to JSON and vice versa.
 */
public class GeoMapAdapter {

  private final Moshi moshi = new Moshi.Builder().build();
  
  // Type information for nested classes to help with Moshi serialization
  Type geometry = Types.newParameterizedType(Geometry.class, String.class, List.class);
  Type geomaptype = Types.newParameterizedType(GeoMap.class, String.class, geometry, Property.class);
  Type type = Types.newParameterizedType(GeoMapCollection.class, String.class, List.class, geomaptype);

  /**
   * Converts a GeoMapCollection object to its JSON representation.
   * 
   * @param map The GeoMapCollection object to convert.
   * @return The JSON string representation of the GeoMapCollection.
   */
  @ToJson
  public String toJson(GeoMapCollection map) {
    JsonAdapter<GeoMapCollection> adapter = moshi.adapter(type);
    return adapter.toJson(map);
  }

  /**
   * Converts a JSON string to a GeoMapCollection object.
   * 
   * @param map The JSON string to convert.
   * @return The resulting GeoMapCollection object.
   * @throws IOException If the JSON is malformed or cannot be parsed into a GeoMapCollection.
   */
  @FromJson
  public GeoMapCollection fromJson(String map) throws IOException {
    JsonAdapter<GeoMapCollection> adapter = moshi.adapter(type);
    return adapter.fromJson(map);
  }
}

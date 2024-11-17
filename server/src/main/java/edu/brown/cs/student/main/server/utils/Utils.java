package edu.brown.cs.student.main.server.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * A utility class providing helper methods for JSON serialization and other general purposes.
 */
public class Utils {

  /**
   * Converts a Map to a JSON string using the Moshi library.
   * 
   * @param map The Map to be converted to JSON.
   * @return The JSON representation of the Map as a string.
   */
  public static String toMoshiJson(Map<String, Object> map) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    return adapter.toJson(map);
  }
}

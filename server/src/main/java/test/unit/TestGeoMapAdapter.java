package test.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.utils.GeoMapAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the GeoMapAdapter class, testing JSON serialization and deserialization of
 * GeoMapCollection objects.
 */
public class TestGeoMapAdapter {

  // GeoMapAdapter instance for testing
  private final GeoMapAdapter geoMapAdapter = new GeoMapAdapter();

  // Moshi instance for JSON conversion
  private final Moshi moshi = new Moshi.Builder().build();

  /**
   * Tests the conversion of a GeoMapCollection to JSON.
   * <p>
   * This test checks if the GeoMapCollection is correctly serialized into JSON and verifies that the
   * expected properties are included in the resulting JSON string.
   */
  @Test
  public void testToJson() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Convert GeoMapCollection to JSON
    String json = geoMapAdapter.toJson(collection);

    // Assert that JSON string contains expected properties
    assertTrue(json.contains("FeatureCollection"));
    assertTrue(json.contains("neighborhood1"));
    assertTrue(json.contains("keyword1"));
  }

  /**
   * Tests the deserialization of a GeoMapCollection from JSON.
   * <p>
   * This test serializes a GeoMapCollection to JSON and then deserializes it back to a GeoMapCollection.
   * It checks if the deserialized collection matches the original collection.
   *
   * @throws IOException if an I/O error occurs during JSON processing
   */
  @Test
  public void testFromJson() throws IOException {
    // Setup mock data
    GeoMapCollection originalCollection = createMockGeoMapCollection();
    String json = geoMapAdapter.toJson(originalCollection);

    // Deserialize JSON back to GeoMapCollection
    GeoMapCollection deserializedCollection = geoMapAdapter.fromJson(json);

    // Assert that the deserialized collection has the same features as the original
    assertNotNull(deserializedCollection);
    assertEquals(originalCollection.features.size(), deserializedCollection.features.size());
    assertEquals("neighborhood1", deserializedCollection.features.get(0).properties.name);
    assertEquals(
        "keyword1",
        deserializedCollection.features.get(0).properties.area_description_data.get("desc"));
  }

  /**
   * Tests the deserialization of an empty JSON string into a GeoMapCollection.
   * <p>
   * This test verifies that an empty JSON string returns an empty GeoMapCollection with no features.
   *
   * @throws IOException if an I/O error occurs during JSON processing
   */
  @Test
  public void testFromJson_EmptyJson() throws IOException {
    // Test deserialization with empty JSON string
    String emptyJson = "{}";

    GeoMapCollection result = geoMapAdapter.fromJson(emptyJson);

    // Assert that the result is an empty GeoMapCollection
    assertNotNull(result);
    assertEquals(0, result.features.size());
  }

  /**
   * Creates a mock GeoMapCollection containing two features with different names and keywords.
   *
   * @return a mock GeoMapCollection
   */
  private GeoMapCollection createMockGeoMapCollection() {
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = new ArrayList<>();

    // Create mock GeoMaps with different properties
    collection.features.add(createMockGeoMap("neighborhood1", "keyword1"));
    collection.features.add(createMockGeoMap("neighborhood2", "keyword2"));

    return collection;
  }

  /**
   * Creates a mock GeoMap with the specified name and keyword.
   *
   * @param name    the name of the neighborhood
   * @param keyword the keyword associated with the area
   * @return a mock GeoMap
   */
  private GeoMap createMockGeoMap(String name, String keyword) {
    GeoMap geoMap = new GeoMap();
    geoMap.type = "Feature";

    Property property = new Property();
    property.name = name;
    property.area_description_data = new HashMap<>();
    property.area_description_data.put("desc", keyword);

    Geometry geometry = new Geometry();
    geometry.coordinates =
        List.of(
            List.of(
                List.of(
                    List.of(-70.5, 40.5),
                    List.of(-70.5, 41.0),
                    List.of(-70.0, 41.0),
                    List.of(-70.0, 40.5),
                    List.of(-70.5, 40.5))));

    geoMap.properties = property;
    geoMap.geometry = geometry;

    return geoMap;
  }
}

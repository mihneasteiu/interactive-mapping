package test.unit;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.utils.GeoFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestGeoFilter {

  /**
   * Test case for filtering by keyword where a valid match exists.
   * Verifies that the collection only contains the expected feature after filtering by keyword.
   */
  @Test
  public void testFilterByKeyword_ValidMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a valid keyword
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "keyword1");

    // Assert that the filtered collection has the expected features
    assertEquals(1, filteredCollection.features.size());
    assertEquals("neighborhood1", filteredCollection.features.get(0).properties.name);
  }

  /**
   * Test case for filtering by keyword where no match is found.
   * Verifies that no features are returned when the keyword does not match any feature.
   */
  @Test
  public void testFilterByKeyword_NoMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a keyword that does not match any feature
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "nonexistent");

    // Assert that no features match
    assertEquals(0, filteredCollection.features.size());
  }

  /**
   * Test case for filtering by keyword with an empty keyword.
   * Verifies that no features are returned when an empty string is used as a keyword.
   */
  @Test
  public void testFilterByKeyword_EmptyKeyword() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with an empty keyword
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "");

    // Assert that no features match since empty string shouldn't match any keyword
    assertEquals(0, filteredCollection.features.size());
  }

  /**
   * Test case for filtering by bounding box where the bounding box includes all features.
   * Verifies that all features are included when the bounding box encompasses all of them.
   */
  @Test
  public void testFilterByBoundingBox_ValidBoundingBox() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a bounding box that includes all features
    GeoMapCollection filteredCollection =
        GeoFilter.filterByBoundingBox(collection, -71.0, -70.0, 40.0, 41.0);

    // Assert that all features are included as they all fall within the bounding box
    assertEquals(2, filteredCollection.features.size());
  }

  /**
   * Test case for filtering by bounding box where only some features match.
   * Verifies that only features within the bounding box are included in the filtered collection.
   */
  @Test
  public void testFilterByBoundingBox_PartialMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a bounding box that partially includes features
    GeoMapCollection filteredCollection =
        GeoFilter.filterByBoundingBox(collection, -71.0, -70.0, 40.5, 41.0);

    // Assert that only one feature is included
    assertEquals(1, filteredCollection.features.size());
    assertEquals("neighborhood2", filteredCollection.features.get(0).properties.name);
  }

  /**
   * Test case for filtering by bounding box where no features match.
   * Verifies that no features are returned when the bounding box doesn't match any feature.
   */
  @Test
  public void testFilterByBoundingBox_NoMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a bounding box that doesn't include any features
    GeoMapCollection filteredCollection =
        GeoFilter.filterByBoundingBox(collection, -72.0, -71.5, 42.0, 43.0);

    // Assert that no features are included
    assertEquals(0, filteredCollection.features.size());
  }

  /**
   * Test case for filtering by bounding box on an empty collection.
   * Verifies that the filtered collection remains empty when the original collection is empty.
   */
  @Test
  public void testFilterByBoundingBox_EmptyCollection() {
    // Create an empty GeoMapCollection
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = new ArrayList<>();

    // Apply filter with a bounding box
    GeoMapCollection filteredCollection =
        GeoFilter.filterByBoundingBox(collection, -71.0, -70.0, 40.0, 41.0);

    // Assert that the filtered collection is still empty
    assertEquals(0, filteredCollection.features.size());
  }

  /**
   * Test case for filtering by both keyword and bounding box.
   * Verifies that only the features matching both the keyword and bounding box remain in the filtered collection.
   */
  @Test
  public void testFilterByKeywordAndBoundingBox() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a valid keyword and bounding box
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "keyword1");
    filteredCollection =
        GeoFilter.filterByBoundingBox(filteredCollection, -71.0, -70.0, 40.0, 41.0);

    // Assert that only the feature matching both the keyword and bounding box remains
    assertEquals(1, filteredCollection.features.size());
    assertEquals("neighborhood1", filteredCollection.features.get(0).properties.name);
  }

  /**
   * Creates a mock {@link GeoMapCollection} object with predefined features for testing.
   * This collection includes two features with different properties.
   *
   * @return A mock {@link GeoMapCollection} object.
   */
  private GeoMapCollection createMockGeoMapCollection() {
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = new ArrayList<>();

    // Create two mock GeoMaps with different properties
    collection.features.add(
        createMockGeoMap("neighborhood1", "keyword1", -70.5, 40.5, -70.0, 41.0));
    collection.features.add(
        createMockGeoMap("neighborhood2", "keyword2", -71.0, 40.0, -70.5, 41.0));

    return collection;
  }

  /**
   * Creates a mock {@link GeoMap} object with predefined properties and geometry.
   *
   * @param name The name of the neighborhood.
   * @param keyword The keyword associated with the neighborhood.
   * @param minLat The minimum latitude of the bounding box.
   * @param minLong The minimum longitude of the bounding box.
   * @param maxLat The maximum latitude of the bounding box.
   * @param maxLong The maximum longitude of the bounding box.
   * @return A mock {@link GeoMap} object.
   */
  private GeoMap createMockGeoMap(
      String name, String keyword, double minLat, double minLong, double maxLat, double maxLong) {
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
                    List.of(minLong, minLat),
                    List.of(minLong, maxLat),
                    List.of(maxLong, maxLat),
                    List.of(maxLong, minLat),
                    List.of(minLong, minLat))));

    geoMap.properties = property;
    geoMap.geometry = geometry;

    return geoMap;
  }
}

package test.unit;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.utils.GeoFilter;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TestGeoFilter {

  // Test for Keyword Filtering
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

  @Test
  public void testFilterByKeyword_NoMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a keyword that does not match any feature
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "nonexistent");

    // Assert that no features match
    assertEquals(0, filteredCollection.features.size());
  }

  @Test
  public void testFilterByKeyword_EmptyKeyword() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with an empty keyword
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "");

    // Assert that no features match since empty string shouldn't match any keyword
    assertEquals(0, filteredCollection.features.size());
  }

  // Test for Bounding Box Filtering
  @Test
  public void testFilterByBoundingBox_ValidBoundingBox() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a bounding box that includes all features
    GeoMapCollection filteredCollection = GeoFilter.filterByBoundingBox(collection, -71.0, -70.0, 40.0, 41.0);

    // Assert that all features are included as they all fall within the bounding box
    assertEquals(2, filteredCollection.features.size());
  }

  @Test
  public void testFilterByBoundingBox_PartialMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a bounding box that partially includes features
    GeoMapCollection filteredCollection = GeoFilter.filterByBoundingBox(collection, -71.0, -70.0, 40.5, 41.0);

    // Assert that only one feature is included
    assertEquals(1, filteredCollection.features.size());
    assertEquals("neighborhood2", filteredCollection.features.get(0).properties.name);
  }

  @Test
  public void testFilterByBoundingBox_NoMatch() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a bounding box that doesn't include any features
    GeoMapCollection filteredCollection = GeoFilter.filterByBoundingBox(collection, -72.0, -71.5, 42.0, 43.0);

    // Assert that no features are included
    assertEquals(0, filteredCollection.features.size());
  }

  @Test
  public void testFilterByBoundingBox_EmptyCollection() {
    // Create an empty GeoMapCollection
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = new ArrayList<>();

    // Apply filter with a bounding box
    GeoMapCollection filteredCollection = GeoFilter.filterByBoundingBox(collection, -71.0, -70.0, 40.0, 41.0);

    // Assert that the filtered collection is still empty
    assertEquals(0, filteredCollection.features.size());
  }

  // Combined Test: Both Keyword and Bounding Box Filtering
  @Test
  public void testFilterByKeywordAndBoundingBox() {
    // Setup mock data
    GeoMapCollection collection = createMockGeoMapCollection();

    // Apply filter with a valid keyword and bounding box
    GeoMapCollection filteredCollection = GeoFilter.filterByKeyword(collection, "keyword1");
    filteredCollection = GeoFilter.filterByBoundingBox(filteredCollection, -71.0, -70.0, 40.0, 41.0);

    // Assert that only the feature matching both the keyword and bounding box remains
    assertEquals(1, filteredCollection.features.size());
    assertEquals("neighborhood1", filteredCollection.features.get(0).properties.name);
  }

  private GeoMapCollection createMockGeoMapCollection() {
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = new ArrayList<>();

    // Create two mock GeoMaps with different properties
    collection.features.add(createMockGeoMap("neighborhood1", "keyword1", -70.5, 40.5, -70.0, 41.0));
    collection.features.add(createMockGeoMap("neighborhood2", "keyword2", -71.0, 40.0, -70.5, 41.0));

    return collection;
  }

  private GeoMap createMockGeoMap(String name, String keyword, double minLat, double minLong, double maxLat, double maxLong) {
    GeoMap geoMap = new GeoMap();
    geoMap.type = "Feature";

    Property property = new Property();
    property.name = name;
    property.area_description_data = new HashMap<>();
    property.area_description_data.put("desc", keyword);

    Geometry geometry = new Geometry();
    geometry.coordinates = List.of(List.of(List.of(
        List.of(minLong, minLat),
        List.of(minLong, maxLat),
        List.of(maxLong, maxLat),
        List.of(maxLong, minLat),
        List.of(minLong, minLat)
    )));

    geoMap.properties = property;
    geoMap.geometry = geometry;

    return geoMap;
  }
}

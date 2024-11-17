package test.unit;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.utils.JSONParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the JSONParser class, verifying the functionality of parsing JSON files into
 * GeoMapCollection objects.
 */
public class TestJSONParser {

  /**
   * Tests the JSONParser with a valid JSON file.
   * <p>
   * This test ensures that the JSONParser correctly parses a valid JSON file into a GeoMapCollection,
   * and verifies that the parsed data matches the expected values.
   *
   * @throws FileNotFoundException if the file path is invalid
   */
  @Test
  public void testParserWithValidFile() throws FileNotFoundException {
    // Path to the valid JSON file
    String filePath = "data/test_geomap.json";

    // Create a JSONParser instance
    JSONParser parser = new JSONParser(filePath);

    // Retrieve the data
    GeoMapCollection collection = parser.getData();

    // Verify that the data is correctly parsed
    assertNotNull(collection);
    assertEquals(2, collection.features.size());
    assertEquals("neighborhood1", collection.features.get(0).properties.name);
    assertEquals(
        "keyword1", collection.features.get(0).properties.area_description_data.get("desc"));
  }

  /**
   * Tests the JSONParser with an empty JSON file.
   * <p>
   * This test verifies that the JSONParser correctly handles an empty JSON file, returning an empty
   * GeoMapCollection with no features.
   *
   * @throws IOException if an error occurs during file reading or parsing
   */
  @Test
  public void testParserWithEmptyFile() throws IOException {
    // Path to an empty JSON file
    String filePath = "data/emptyjson.json";

    // Create the empty file for testing
    // In reality, you'd create an empty JSON file (e.g., "{}")

    // Create a JSONParser instance
    JSONParser parser = new JSONParser(filePath);

    // Retrieve the data
    GeoMapCollection collection = parser.getData();

    // Assert that the collection is empty
    assertNotNull(collection);
    assertEquals(0, collection.features.size());
  }
}
package test.unit;

import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.utils.JSONParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TestJSONParser {

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
    assertEquals("keyword1", collection.features.get(0).properties.area_description_data.get("desc"));
  }

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

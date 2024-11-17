package test.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.GetDataHandler;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This class contains integration tests for the GetDataHandler endpoint, which retrieves geographic
 * data based on the specified bounding box coordinates. The tests validate different scenarios such as
 * valid bounding box, missing parameters, invalid coordinates, and ensuring the response matches the
 * requested area.
 */
public class TestGetDataHandler {

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * Sets up the test environment before each test case. Initializes Moshi for JSON parsing, 
   * configures the GetDataHandler, and starts the Spark server.
   */
  @BeforeEach
  public void setup() {
    Spark.port(3232);  // Start Spark server before each test
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    GeoMapCollection collection = createMockGeoMapCollection(); // Create mock collection
    GetDataHandler handler = new GetDataHandler(collection);
    Spark.get("/getData", handler);
    Spark.awaitInitialization();
  }

  /**
   * Sends an HTTP request to the server for the given API endpoint.
   *
   * @param apiCall the API endpoint to be called
   * @return an HttpURLConnection object for the given API call
   * @throws IOException if there is an error in making the request
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Creates a mock GeoMapCollection with a single mock GeoMap that has a bounding box.
   *
   * @return a mock GeoMapCollection object
   */
  private GeoMapCollection createMockGeoMapCollection() {
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = List.of(createMockGeoMap("Area 1", -10.0, -10.0, 10.0, 10.0));
    return collection;
  }

  /**
   * Creates a mock GeoMap with the specified name and geographic coordinates.
   *
   * @param name the name of the GeoMap
   * @param minLat the minimum latitude of the bounding box
   * @param minLong the minimum longitude of the bounding box
   * @param maxLat the maximum latitude of the bounding box
   * @param maxLong the maximum longitude of the bounding box
   * @return a mock GeoMap object
   */
  private GeoMap createMockGeoMap(
      String name, double minLat, double minLong, double maxLat, double maxLong) {
    GeoMap geoMap = new GeoMap();
    geoMap.type = "Feature";
    Property property = new Property();
    property.name = name;
    property.area_description_data = Map.of("desc", "Some description");

    // Set geometry with mock coordinates
    Geometry geometry = new Geometry();
    geometry.coordinates =
        List.of(
            List.of(
                List.of(
                    List.of(minLong, minLat),
                    List.of(maxLong, minLat),
                    List.of(maxLong, maxLat),
                    List.of(minLong, maxLat),
                    List.of(minLong, minLat)))); // Bounding box coordinates
    geoMap.geometry = geometry;
    geoMap.properties = property;
    return geoMap;
  }

  /**
   * Tests the GetDataHandler endpoint with a valid bounding box. It ensures that the response
   * contains the correct data for the given coordinates.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetDataHandler_ValidBoundingBox() throws IOException {
    String url = "getData?minLat=-90&minLong=-180&maxLat=90&maxLong=180"; // Valid full bounding box
    HttpURLConnection connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Validate the response (check for key 'features' and the number of elements)
    assertTrue(responseBody.containsKey("features"));
    assertEquals(
        1,
        ((Map<String, Object>) responseBody.get("features"))
            .size()); // Should return the mock feature
  }

  /**
   * Tests the GetDataHandler endpoint with missing required parameters. It verifies that the server
   * returns an error when parameters are missing.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetDataHandler_MissingParameter() throws IOException {
    String url = "getData?minLat=-90&minLong=-180"; // Missing maxLat and maxLong
    HttpURLConnection connection = tryRequest(url);
    assertEquals(400, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertTrue(responseBody.containsKey("error"));
    assertEquals(
        "Missing required parameters. Please provide minLat, minLong, maxLat, and maxLong",
        responseBody.get("error"));
  }

  /**
   * Tests the GetDataHandler endpoint with an invalid coordinate (latitude). It ensures that the
   * server returns an error when the latitude is outside the valid range.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetDataHandler_InvalidCoordinate() throws IOException {
    String url = "getData?minLat=-100&minLong=-180&maxLat=90&maxLong=180"; // Invalid minLat
    HttpURLConnection connection = tryRequest(url);
    assertEquals(400, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertTrue(responseBody.containsKey("error"));
    assertEquals("Latitude values must be between -90 and 90 degrees", responseBody.get("error"));
  }

  /**
   * Tests the GetDataHandler endpoint with valid coordinates that should be inside the bounding box.
   * It ensures that the response contains the expected features within the specified bounding box.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetDataHandler_BoundingBox() throws IOException {
    // Test coordinates that should be within the bounding box
    String url = "getData?minLat=-10&minLong=-10&maxLat=10&maxLong=10";
    HttpURLConnection connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Check if response contains the features within the bounding box
    assertTrue(responseBody.containsKey("features"));
    assertEquals(1, ((Map<String, Object>) responseBody.get("features")).size());

    // Test coordinates that should also be within the bounding box
    url = "getData?minLat=-1&minLong=-1&maxLat=1&maxLong=1";
    connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());

    responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Check if response contains the features within the bounding box
    assertTrue(responseBody.containsKey("features"));
    assertEquals(1, ((Map<String, Object>) responseBody.get("features")).size());
  }
}

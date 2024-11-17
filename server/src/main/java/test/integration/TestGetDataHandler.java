package test.integration;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.GetDataHandler;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
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
import static org.junit.jupiter.api.Assertions.*;

public class TestGetDataHandler {
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    // Start Spark server before each test
    Spark.port(3232);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    GeoMapCollection collection = createMockGeoMapCollection(); // Create mock collection
    GetDataHandler handler = new GetDataHandler(collection);
    Spark.get("/getData", handler);
    Spark.awaitInitialization();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  private GeoMapCollection createMockGeoMapCollection() {
    // Create a mock GeoMapCollection with some mock GeoMaps
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features = List.of(createMockGeoMap("Area 1", -10.0, -10.0, 10.0, 10.0));
    return collection;
  }

  private GeoMap createMockGeoMap(String name, double minLat, double minLong, double maxLat, double maxLong) {
    // Create and return a mock GeoMap with the given coordinates
    GeoMap geoMap = new GeoMap();
    geoMap.type = "Feature";
    Property property = new Property();
    property.name = name;
    property.area_description_data = Map.of("desc", "Some description");

    // Set geometry with mock coordinates
    Geometry geometry = new Geometry();
    geometry.coordinates = List.of(List.of(List.of(
        List.of(minLong, minLat),
        List.of(maxLong, minLat),
        List.of(maxLong, maxLat),
        List.of(minLong, maxLat),
        List.of(minLong, minLat)
    )));
    geoMap.geometry = geometry;
    geoMap.properties = property;
    return geoMap;
  }

  @Test
  public void testGetDataHandler_ValidBoundingBox() throws IOException {
    // Valid request with a full bounding box
    String url = "getData?minLat=-90&minLong=-180&maxLat=90&maxLong=180";
    HttpURLConnection connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Validate the response (check for key 'features' and the number of elements)
    assertTrue(responseBody.containsKey("features"));
    assertEquals(1, ((Map<String, Object>) responseBody.get("features")).size()); // Should return the mock feature
  }

  @Test
  public void testGetDataHandler_MissingParameter() throws IOException {
    // Missing maxLat and maxLong
    String url = "getData?minLat=-90&minLong=-180";
    HttpURLConnection connection = tryRequest(url);
    assertEquals(400, connection.getResponseCode());

    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertTrue(responseBody.containsKey("error"));
    assertEquals("Missing required parameters. Please provide minLat, minLong, maxLat, and maxLong",
        responseBody.get("error"));
  }

  @Test
  public void testGetDataHandler_InvalidCoordinate() throws IOException {
    // Invalid coordinate for minLat
    String url = "getData?minLat=-100&minLong=-180&maxLat=90&maxLong=180"; // Invalid minLat
    HttpURLConnection connection = tryRequest(url);
    assertEquals(400, connection.getResponseCode());

    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertTrue(responseBody.containsKey("error"));
    assertEquals("Latitude values must be between -90 and 90 degrees", responseBody.get("error"));
  }

  @Test
  public void testGetDataHandler_BoundingBox() throws IOException {
    // Test coordinates that should be within the bounding box
    String url = "getData?minLat=-10&minLong=-10&maxLat=10&maxLong=10";
    HttpURLConnection connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Check if response contains the features within the bounding box
    assertTrue(responseBody.containsKey("features"));
    assertEquals(1, ((Map<String, Object>) responseBody.get("features")).size());

    // Test coordinates that should be within the bounding box
    url = "getData?minLat=-1&minLong=-1&maxLat=1&maxLong=1";
    connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());

    responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Check if response contains the features within the bounding box
    assertTrue(responseBody.containsKey("features"));
    assertEquals(1, ((Map<String, Object>) responseBody.get("features")).size());
  }
}

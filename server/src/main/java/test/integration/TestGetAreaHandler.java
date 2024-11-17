package test.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.GetAreaHandler;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Geometry;
import edu.brown.cs.student.main.server.mapCollection.GeoMap.fields.Property;
import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This class contains integration tests for the GetAreaHandler endpoint, which retrieves geographic
 * area data based on a keyword parameter. The tests validate different scenarios such as missing
 * parameters, successful retrieval of data, and the case where no areas match the given keyword.
 */
public class TestGetAreaHandler {
  
  private static final int PORT = 3232;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * Sets up the test environment before each test case. Initializes Moshi for JSON parsing,
   * configures the GetAreaHandler, and starts the Spark server.
   */
  @BeforeEach
  public void setup() {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    Spark.port(PORT);
    GeoMapCollection mockGeoMapCollection = createMockGeoMapCollection();
    Spark.get("/getArea", new GetAreaHandler(mockGeoMapCollection));
    Spark.awaitInitialization();
  }

  /**
   * Cleans up the test environment after each test case. Stops the Spark server.
   */
  @AfterEach
  public void tearDown() {
    Spark.stop();
    Spark.awaitStop();
  }

  /**
   * Creates a mock GeoMapCollection for testing. This collection contains mock geographic area data
   * with two cities: "city1" and "city2".
   *
   * @return a mock GeoMapCollection object
   */
  private GeoMapCollection createMockGeoMapCollection() {
    GeoMapCollection collection = new GeoMapCollection();
    collection.type = "FeatureCollection";
    collection.features =
        List.of(createMockGeoMap("city1", "big"), createMockGeoMap("city2", "small"));
    return collection;
  }

  /**
   * Creates a mock GeoMap object with the given name and keyword.
   *
   * @param name the name of the geographic area
   * @param keyword the keyword associated with the area
   * @return a mock GeoMap object
   */
  private GeoMap createMockGeoMap(String name, String keyword) {
    GeoMap geoMap = new GeoMap();
    geoMap.type = "Feature";

    Property property = new Property();
    property.name = name;
    property.area_description_data = new HashMap<>();
    property.area_description_data.put("desc", keyword);

    Geometry geometry = new Geometry();

    geoMap.geometry = geometry;
    geoMap.properties = property;

    return geoMap;
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
   * Tests a request to the /getArea endpoint with missing keyword parameter.
   * Verifies that the server responds with an error message indicating the missing parameter.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetAreaMissingKeyword() throws IOException {
    HttpURLConnection connection = tryRequest("getArea");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("error", responseBody.get("response_type"));
    assertEquals("Missing keyword parameters.", responseBody.get("error"));
  }

  /**
   * Tests a successful request to the /getArea endpoint with a valid keyword.
   * Verifies that the server returns the correct geographic area data that matches the keyword.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetAreaSuccess() throws IOException {
    HttpURLConnection connection = tryRequest("getArea?key=big");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("FeatureCollection", ((Map<String, Object>) responseBody.get("type")).get("type"));
    assertEquals(
        "city1",
        ((Map<String, Object>)
                ((Map<String, Object>) ((Map<String, Object>) responseBody.get("features")).get(0))
                    .get("properties"))
            .get("name"));
    assertEquals(1, ((Map<String, Object>) responseBody.get("features")).size());
  }

  /**
   * Tests a request to the /getArea endpoint with a keyword that does not match any area.
   * Verifies that the server responds with an empty list of features.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testGetAreaNoMatchingKeyword() throws IOException {
    HttpURLConnection connection = tryRequest("getArea?key=neighborhood_not_found");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals(0, ((Map<String, Object>) responseBody.get("features")).size());
  }
}
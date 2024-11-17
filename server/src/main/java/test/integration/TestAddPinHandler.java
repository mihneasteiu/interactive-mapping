package test.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeClass;
import spark.Spark;

/**
 * This class contains integration tests for the AddPinHandler endpoint,
 * which allows users to add pins to their map data.
 */
public class TestAddPinHandler {

  private static final int PORT = 3232;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeClass
  public static void setupOnce() {
    Spark.port(PORT);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    FirebaseUtilities firebaseUtils = new FirebaseUtilities();
    Spark.get("addPin", new AddPinHandler(firebaseUtils));
    Spark.awaitInitialization();
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("addPin");
    Spark.awaitStop();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  /** Tests a successful pin addition with valid parameters. */
  @Test
  public void testAddPinRequestSuccess() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=mishoo&ltd=23&lng=3");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("success", responseBody.get("response_type"));
    assertEquals("latitude: 23, longitude: 3", responseBody.get("pin"));

    connection.disconnect();
  }

  /** Tests adding a pin with missing parameters (latitude). */
  @Test
  public void testAddPinRequestFail_missingLatitude() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=mishoo&lng=-122.4194");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("Please enter all parameters", responseBody.get("error"));

    connection.disconnect();
  }

  /** Tests adding a pin with invalid latitude and longitude values. */
  @Test
  public void testAddPinRequestFail_invalidCoordinates() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=mishoo&ltd=abc&lng=xyz");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("Latitude and longitude must be valid numbers", responseBody.get("error"));

    connection.disconnect();
  }

  /** Tests adding a pin with missing user ID. */
  @Test
  public void testAddPinRequestFail_missingUserId() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?ltd=37.7749&lng=-122.4194");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("Please enter all parameters", responseBody.get("error"));

    connection.disconnect();
  }
}

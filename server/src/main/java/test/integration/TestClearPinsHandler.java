package test.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.ClearPinsHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
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
 * This class contains integration tests for the ClearPinsHandler endpoint, which allows users to
 * clear all pins associated with their user ID. The tests check for successful and failure cases,
 * such as missing or invalid parameters.
 */
public class TestClearPinsHandler {

  private static final int PORT = 3232;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  // Mock storage interface
  private StorageInterface firebaseUtils;

  /**
   * Initializes the Spark server and sets up logging for the tests. This setup runs only once
   * before all tests.
   */
  @BeforeClass
  public static void setupOnce() {
    Spark.port(PORT);
    Logger.getLogger("").setLevel(Level.WARNING); // Set logging level to show only warnings and errors
  }

  /**
   * Sets up the test environment before each test case. Initializes Moshi for JSON parsing,
   * configures the ClearPinsHandler, and starts the server.
   *
   * @throws IOException if there is an issue starting the server or initializing handlers
   */
  @BeforeEach
  public void setup() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);

    // Mock implementation of StorageInterface
    firebaseUtils = new FirebaseUtilities();
    Spark.get("clearPins", new ClearPinsHandler(firebaseUtils));
    Spark.awaitInitialization();
  }

  /**
   * Cleans up the test environment after each test case. Stops the server and unmaps the route.
   */
  @AfterEach
  public void tearDown() {
    Spark.unmap("clearPins");
    Spark.awaitStop();
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
   * Tests a successful request to clear all pins for a user.
   * Verifies that the server responds with a success message.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testClearPinsSuccess() throws IOException {
    HttpURLConnection connection = tryRequest("clearPins?uid=test_user");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("success", responseBody.get("response_type"));
    assertEquals("All pins cleared for user: test_user", responseBody.get("message"));

    connection.disconnect();
  }

  /**
   * Tests clearing pins without providing a user ID.
   * Verifies that the server responds with a failure message indicating the missing parameter.
   *
   * @throws IOException if there is an error in making the request
   */
  @Test
  public void testClearPinsFailure_missingUid() throws IOException {
    HttpURLConnection connection = tryRequest("clearPins");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("User ID is required", responseBody.get("error"));

    connection.disconnect();
  }
}

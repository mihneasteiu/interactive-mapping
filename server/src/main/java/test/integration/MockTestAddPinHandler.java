package test.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.storage.MockStorage;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * Integration tests for the AddPinHandler.
 * These tests verify that the AddPinHandler correctly processes requests to add a pin, 
 * handling success and various failure cases, such as missing parameters and invalid coordinates.
 */
public class MockTestAddPinHandler {
  
  private static final int PORT = 3232;

  private StorageInterface mockStorage;

  /**
   * Setup method that initializes the mock storage and starts the Spark server
   * before each test.
   */
  @BeforeEach
  public void setup() {
    mockStorage = new MockStorage();

    Spark.port(PORT);
    Spark.post("/addPin", new AddPinHandler(mockStorage));
    Spark.awaitInitialization();
  }

  /**
   * Teardown method that stops the Spark server after each test.
   */
  @AfterEach
  public void tearDown() {
    Spark.stop();
    Spark.awaitStop();
  }

  /**
   * Helper method to send an HTTP request to the server for a specific API call.
   * 
   * @param apiCall the API endpoint to request
   * @return an HttpURLConnection for the API call
   * @throws IOException if an error occurs while making the HTTP request
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
   * Helper method to parse the JSON response from an HTTP connection.
   * 
   * @param connection the HttpURLConnection to read the response from
   * @return a map representing the parsed JSON response
   * @throws IOException if an error occurs while reading the response
   */
  private Map<String, Object> parseResponse(HttpURLConnection connection) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }

      Moshi moshi = new Moshi.Builder().build();
      Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(type);
      return adapter.fromJson(response.toString());
    }
  }

  /**
   * Test case for successfully adding a pin.
   * It checks that the response contains the correct success message and pin details.
   * 
   * @throws IOException if an error occurs while making the HTTP request or reading the response
   */
  @Test
  public void testAddPinSuccess() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=test_user&ltd=45.0&lng=-93.0");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody = parseResponse(connection);
    assertEquals("success", responseBody.get("response_type"));
    assertEquals("latitude: 45.0, longitude: -93.0", responseBody.get("pin"));
    assertEquals("test_user", responseBody.get("userId"));
  }

  /**
   * Test case for attempting to add a pin with missing parameters.
   * It checks that the response contains the correct failure message for missing parameters.
   * 
   * @throws IOException if an error occurs while making the HTTP request or reading the response
   */
  @Test
  public void testAddPinFailure_missingParameters() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=test_user&ltd=45.0");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody = parseResponse(connection);
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("Please enter all parameters", responseBody.get("error"));
  }

  /**
   * Test case for attempting to add a pin with invalid coordinates (latitude out of range).
   * It checks that the response contains the correct failure message for invalid latitude.
   * 
   * @throws IOException if an error occurs while making the HTTP request or reading the response
   */
  @Test
  public void testAddPinFailure_invalidCoordinates() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=test_user&ltd=200.0&lng=-93.0");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody = parseResponse(connection);
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("Latitude must be between -90 and 90", responseBody.get("error"));
  }

  /**
   * Test case for attempting to add a pin with non-numeric coordinates.
   * It checks that the response contains the correct failure message for invalid number format.
   * 
   * @throws IOException if an error occurs while making the HTTP request or reading the response
   */
  @Test
  public void testAddPinFailure_nonNumericCoordinates() throws IOException {
    HttpURLConnection connection = tryRequest("addPin?uid=test_user&ltd=not_a_number&lng=-93.0");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody = parseResponse(connection);
    assertEquals("failure", responseBody.get("response_type"));
    assertEquals("Latitude and longitude must be valid numbers", responseBody.get("error"));
  }
}

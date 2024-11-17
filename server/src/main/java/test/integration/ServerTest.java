package test.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.*;
import edu.brown.cs.student.main.server.handlers.AddPinHandler;
import edu.brown.cs.student.main.server.handlers.ListPinsHandler;
import edu.brown.cs.student.main.server.handlers.ClearPinsHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.lang.reflect.Type;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ServerTest {

  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() throws IOException {
    // Start Spark server before each test
    Spark.port(3232);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);

    // Initialize the real handler with an actual implementation of StorageInterface
    StorageInterface storage = new FirebaseUtilities(); // A real implementation
    AddPinHandler addPinHandler = new AddPinHandler(storage);
    ListPinsHandler listPinsHandler = new ListPinsHandler(storage);
    ClearPinsHandler clearPinsHandler = new ClearPinsHandler(storage);

    // Map routes to handlers
    Spark.get("/addPin", addPinHandler);
    Spark.get("/listPins", listPinsHandler);
    Spark.get("/clearPins", clearPinsHandler);

    Spark.awaitInitialization();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Accept", "application/json");
    connection.connect();
    return connection;
  }

  @Test
  public void testAddPinAndListPins() throws IOException {
    // Step 1: Add a pin
    String addPinUrl = "addPin?uid=mishoo&ltd=23&lng=3";
    HttpURLConnection addPinConnection = tryRequest(addPinUrl);
    assertEquals(200, addPinConnection.getResponseCode());  // Ensure the response is 200 OK

    Map<String, Object> addPinResponseBody = adapter.fromJson(new Buffer().readFrom(addPinConnection.getInputStream()));
    assertEquals("success", addPinResponseBody.get("response_type"));
    assertEquals("latitude: 23, longitude: 3", addPinResponseBody.get("pin"));

    addPinConnection.disconnect();

    // Step 2: List pins to verify the pin has been added
    String listPinsUrl = "listPins";
    HttpURLConnection listPinsConnection = tryRequest(listPinsUrl);
    assertEquals(200, listPinsConnection.getResponseCode());  // Ensure the response is 200 OK

    Map<String, Object> listPinsResponseBody = adapter.fromJson(new Buffer().readFrom(listPinsConnection.getInputStream()));
    assertTrue(listPinsResponseBody.containsKey("pins"));
    List<Map<String, Object>> pins = (List<Map<String, Object>>) listPinsResponseBody.get("pins");
    assertEquals(1, pins.size()); // One pin has been added

    // Verify the added pin details
    assertEquals(23.0, pins.get(0).get("ltd"));
    assertEquals(3.0, pins.get(0).get("lng"));

    listPinsConnection.disconnect();

    // Step 3: Clear all pins
    String clearPinsUrl = "clearPins?uid=mishoo";
    HttpURLConnection clearPinsConnection = tryRequest(clearPinsUrl);
    assertEquals(200, clearPinsConnection.getResponseCode());  // Ensure the response is 200 OK

    Map<String, Object> clearPinsResponseBody = adapter.fromJson(new Buffer().readFrom(clearPinsConnection.getInputStream()));
    assertEquals("success", clearPinsResponseBody.get("response_type"));
    assertEquals("All pins cleared for user: mishoo", clearPinsResponseBody.get("message"));

    clearPinsConnection.disconnect();

    // Step 4: List pins again to verify all pins have been cleared
    HttpURLConnection listPinsAfterClearConnection = tryRequest(listPinsUrl);
    assertEquals(200, listPinsAfterClearConnection.getResponseCode());  // Ensure the response is 200 OK

    Map<String, Object> listPinsAfterClearResponseBody = adapter.fromJson(new Buffer().readFrom(listPinsAfterClearConnection.getInputStream()));
    assertTrue(listPinsAfterClearResponseBody.containsKey("pins"));
    List<Map<String, Object>> clearedPins = (List<Map<String, Object>>) listPinsAfterClearResponseBody.get("pins");
    assertEquals(0, clearedPins.size()); // No pins should remain after clearing

    listPinsAfterClearConnection.disconnect();
  }

  @Test
  public void testAddMultiplePinsThenClearPins() throws IOException {
    // Step 1: Add two pins
    String addPinUrl1 = "addPin?uid=mishoo&ltd=23&lng=3";
    HttpURLConnection addPinConnection1 = tryRequest(addPinUrl1);
    assertEquals(200, addPinConnection1.getResponseCode());
    addPinConnection1.disconnect();

    String addPinUrl2 = "addPin?uid=mishoo&ltd=24&lng=4";
    HttpURLConnection addPinConnection2 = tryRequest(addPinUrl2);
    assertEquals(200, addPinConnection2.getResponseCode());
    addPinConnection2.disconnect();

    // Step 2: List pins to verify both pins are present
    String listPinsUrl = "listPins";
    HttpURLConnection listPinsConnection = tryRequest(listPinsUrl);
    assertEquals(200, listPinsConnection.getResponseCode());

    Map<String, Object> listPinsResponseBody = adapter.fromJson(new Buffer().readFrom(listPinsConnection.getInputStream()));
    List<Map<String, Object>> pins = (List<Map<String, Object>>) listPinsResponseBody.get("pins");
    assertEquals(2, pins.size()); // Two pins have been added

    listPinsConnection.disconnect();

    // Step 3: Clear all pins
    String clearPinsUrl = "clearPins?uid=mishoo";
    HttpURLConnection clearPinsConnection = tryRequest(clearPinsUrl);
    assertEquals(200, clearPinsConnection.getResponseCode());

    Map<String, Object> clearPinsResponseBody = adapter.fromJson(new Buffer().readFrom(clearPinsConnection.getInputStream()));
    assertEquals("success", clearPinsResponseBody.get("response_type"));
    assertEquals("All pins cleared for user: mishoo", clearPinsResponseBody.get("message"));

    clearPinsConnection.disconnect();

    // Step 4: List pins again to verify all pins have been cleared
    HttpURLConnection listPinsAfterClearConnection = tryRequest(listPinsUrl);
    assertEquals(200, listPinsAfterClearConnection.getResponseCode());

    Map<String, Object> listPinsAfterClearResponseBody = adapter.fromJson(new Buffer().readFrom(listPinsAfterClearConnection.getInputStream()));
    List<Map<String, Object>> clearedPins = (List<Map<String, Object>>) listPinsAfterClearResponseBody.get("pins");
    assertEquals(0, clearedPins.size());

    listPinsAfterClearConnection.disconnect();
  }

  @Test
  public void testClearPinsThenListPins() throws IOException {
    // Step 1: Clear pins when no pins exist
    String clearPinsUrl = "clearPins?uid=mishoo";
    HttpURLConnection clearPinsConnection = tryRequest(clearPinsUrl);
    assertEquals(200, clearPinsConnection.getResponseCode());

    Map<String, Object> clearPinsResponseBody = adapter.fromJson(new Buffer().readFrom(clearPinsConnection.getInputStream()));
    assertEquals("success", clearPinsResponseBody.get("response_type"));
    assertEquals("No pins found for user: mishoo", clearPinsResponseBody.get("message"));

    clearPinsConnection.disconnect();

    // Step 2: List pins to verify the response is empty
    String listPinsUrl = "listPins";
    HttpURLConnection listPinsConnection = tryRequest(listPinsUrl);
    assertEquals(200, listPinsConnection.getResponseCode());

    Map<String, Object> listPinsResponseBody = adapter.fromJson(new Buffer().readFrom(listPinsConnection.getInputStream()));
    List<Map<String, Object>> pins = (List<Map<String, Object>>) listPinsResponseBody.get("pins");
    assertEquals(0, pins.size()); // No pins exist

    listPinsConnection.disconnect();
  }

  @Test
  public void testClearPinsForOneUserThenListPinsForAll() throws IOException {
    // Step 1: Add a pin for user mishoo
    String addPinUrl1 = "addPin?uid=mishoo&ltd=23&lng=3";
    HttpURLConnection addPinConnection1 = tryRequest(addPinUrl1);
    assertEquals(200, addPinConnection1.getResponseCode());
    addPinConnection1.disconnect();

    // Step 2: Add a pin for user george
    String addPinUrl2 = "addPin?uid=george&ltd=24&lng=4";
    HttpURLConnection addPinConnection2 = tryRequest(addPinUrl2);
    assertEquals(200, addPinConnection2.getResponseCode());
    addPinConnection2.disconnect();

    // Step 3: Clear pins for user mishoo
    String clearPinsUrl = "clearPins?uid=mishoo";
    HttpURLConnection clearPinsConnection = tryRequest(clearPinsUrl);
    assertEquals(200, clearPinsConnection.getResponseCode());

    Map<String, Object> clearPinsResponseBody = adapter.fromJson(new Buffer().readFrom(clearPinsConnection.getInputStream()));
    assertEquals("success", clearPinsResponseBody.get("response_type"));
    assertEquals("All pins cleared for user: mishoo", clearPinsResponseBody.get("message"));

    clearPinsConnection.disconnect();

    // Step 4: List pins for all users to verify george's pin remains
    String listPinsUrl = "listPins";
    HttpURLConnection listPinsConnection = tryRequest(listPinsUrl);
    assertEquals(200, listPinsConnection.getResponseCode());

    Map<String, Object> listPinsResponseBody = adapter.fromJson(new Buffer().readFrom(listPinsConnection.getInputStream()));
    List<Map<String, Object>> pins = (List<Map<String, Object>>) listPinsResponseBody.get("pins");

    // Verify that there is still one pin (george's pin) after clearing mishoo's pins
    assertEquals(1, pins.size());  // Only george's pin should be present
    assertEquals(24, pins.get(0).get("latitude"));  // Verify george's latitude

    listPinsConnection.disconnect();
  }

}

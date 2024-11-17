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

public class TestClearPinsHandler {
  private static final int PORT = 3232;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  // Mock storage interface
  private StorageInterface firebaseUtils;

  @BeforeClass
  public static void setupOnce() {
    Spark.port(PORT);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);

    // Mock implementation of StorageInterface
    firebaseUtils = new FirebaseUtilities();
    Spark.get("clearPins", new ClearPinsHandler(firebaseUtils));
    Spark.awaitInitialization();
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("clearPins");
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


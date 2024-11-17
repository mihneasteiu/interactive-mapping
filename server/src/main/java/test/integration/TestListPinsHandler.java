package test.integration;
import static org.junit.jupiter.api.Assertions.*;
import com.squareup.moshi.*;
import edu.brown.cs.student.main.server.handlers.ListPinsHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import io.grpc.Context.Storage;
import java.lang.reflect.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;
import okio.Buffer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class TestListPinsHandler {

  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() throws IOException{
    // Start Spark server before each test
    Spark.port(3232);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);

    // Initialize the real handler with an actual implementation of StorageInterface
    StorageInterface storage = new FirebaseUtilities(); // A real implementation that returns empty list
    ListPinsHandler handler = new ListPinsHandler(storage);
    Spark.get("/listPins", handler);
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

  @Test
  public void testListPinsHandler_EmptyDatabase() throws IOException {
    // Send a real HTTP request to the Spark server
    String url = "getPins";
    HttpURLConnection connection = tryRequest(url);
    assertEquals(200, connection.getResponseCode());  // Ensure the response is 200 OK

    // Read the response
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));

    // Validate the response (should be an empty "pins" list)
    assertTrue(responseBody.containsKey("pins"));
    List<String> pins = (List<String>) responseBody.get("pins");
    assertEquals(0, pins.size()); // The database is empty, so there should be no pins
  }
}


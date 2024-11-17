package edu.brown.cs.student.main.server.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Mock implementation of StorageInterface for testing purposes.
 * Uses in-memory storage instead of Firebase.
 */
public class MockStorage implements StorageInterface {
  // Structure: userId -> collection -> documentId -> data
  private final Map<String, Map<String, Map<String, Map<String, Object>>>> storage = new HashMap<>();

  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data) {
    storage.computeIfAbsent(uid, k -> new HashMap<>())
        .computeIfAbsent(collection_id, k -> new HashMap<>())
        .put(doc_id, data);
  }

  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException {
    return new ArrayList<>(
        storage.getOrDefault(uid, new HashMap<>())
            .getOrDefault(collection_id, new HashMap<>())
            .values()
    );
  }

  @Override
  public void clearUser(String uid) throws InterruptedException, ExecutionException {
    storage.remove(uid);
  }

  @Override
  public List<Map<String, Object>> getAllPins() throws InterruptedException, ExecutionException {
    List<Map<String, Object>> allPins = new ArrayList<>();

    // Iterate through all users and collect their pins
    for (Map.Entry<String, Map<String, Map<String, Map<String, Object>>>> userEntry : storage.entrySet()) {
      String userId = userEntry.getKey();
      Map<String, Map<String, Map<String, Object>>> collections = userEntry.getValue();

      // Get the pins collection for this user
      Map<String, Map<String, Object>> pinsCollection = collections.getOrDefault("pins", new HashMap<>());

      // Add all pins from this user to the result list
      allPins.addAll(pinsCollection.values());
    }

    return allPins;
  }
}

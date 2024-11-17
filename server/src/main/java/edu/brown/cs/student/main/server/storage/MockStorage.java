package edu.brown.cs.student.main.server.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Mock implementation of {@link StorageInterface} for testing purposes. 
 * This class simulates data storage using an in-memory {@link Map} instead of Firebase.
 */
public class MockStorage implements StorageInterface {
  // Structure: userId -> collection -> documentId -> data
  private final Map<String, Map<String, Map<String, Map<String, Object>>>> storage =
      new HashMap<>();

  /**
   * Adds a document to the mock storage for a given user, collection, and document ID.
   * 
   * @param uid The unique identifier of the user.
   * @param collection_id The ID of the collection to store the document in.
   * @param doc_id The ID of the document to add.
   * @param data The data to store in the document.
   */
  @Override
  public void addDocument(
      String uid, String collection_id, String doc_id, Map<String, Object> data) {
    storage
        .computeIfAbsent(uid, k -> new HashMap<>())
        .computeIfAbsent(collection_id, k -> new HashMap<>())
        .put(doc_id, data);
  }

  /**
   * Retrieves all documents from a specified collection for a given user.
   * 
   * @param uid The unique identifier of the user.
   * @param collection_id The ID of the collection to retrieve.
   * @return A list of maps, each representing a document in the collection.
   * @throws InterruptedException If the thread is interrupted during execution.
   * @throws ExecutionException If an error occurs during the retrieval of data.
   */
  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException {
    return new ArrayList<>(
        storage
            .getOrDefault(uid, new HashMap<>())
            .getOrDefault(collection_id, new HashMap<>())
            .values());
  }

  /**
   * Clears all data associated with a specific user.
   * 
   * @param uid The unique identifier of the user whose data should be cleared.
   * @throws InterruptedException If the thread is interrupted during execution.
   * @throws ExecutionException If an error occurs during the clearing process.
   */
  @Override
  public void clearUser(String uid) throws InterruptedException, ExecutionException {
    storage.remove(uid);
  }

  /**
   * Retrieves all pins from all users in the mock storage.
   * 
   * @return A list of maps representing all the pin data from every user.
   * @throws InterruptedException If the thread is interrupted during execution.
   * @throws ExecutionException If an error occurs during the retrieval of data.
   */
  @Override
  public List<Map<String, Object>> getAllPins() throws InterruptedException, ExecutionException {
    List<Map<String, Object>> allPins = new ArrayList<>();

    // Iterate through all users and collect their pins
    for (Map.Entry<String, Map<String, Map<String, Map<String, Object>>>> userEntry :
        storage.entrySet()) {
      String userId = userEntry.getKey();
      Map<String, Map<String, Map<String, Object>>> collections = userEntry.getValue();

      // Get the pins collection for this user
      Map<String, Map<String, Object>> pinsCollection =
          collections.getOrDefault("pins", new HashMap<>());

      // Add all pins from this user to the result list
      allPins.addAll(pinsCollection.values());
    }

    return allPins;
  }
}

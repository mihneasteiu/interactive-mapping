package edu.brown.cs.student.main.server.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {
  /**
   * Adds a document to a user's collection
   *
   * @param uid User ID
   * @param collection_id Collection name
   * @param doc_id Document ID
   * @param data Document data
   */
  void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data);

  /**
   * Gets all documents from a user's collection
   *
   * @param uid User ID
   * @param collection_id Collection name
   * @return List of documents as maps
   */
  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;

  /**
   * Clears all documents for a specific user
   *
   * @param uid User ID to clear
   */
  void clearUser(String uid) throws InterruptedException, ExecutionException;

  /**
   * Gets all pins across all users
   *
   * @return List of all pins with user attribution
   */
  List<Map<String, Object>> getAllPins() throws InterruptedException, ExecutionException;
}

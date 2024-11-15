package edu.brown.cs.student.main.server.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {
  void addDocument(String collectionId, String docId, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String collectionId)
      throws InterruptedException, ExecutionException;

  void clearUserCollection(String collectionId, String uid);
}

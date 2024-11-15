package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  private Firestore db;

  public FirebaseUtilities() {
    db = FirestoreClient.getFirestore();
  }

  @Override
  public void addDocument(String collectionId, String docId, Map<String, Object> data) {
    // Adds a document to Firestore in the specified collection
    db.collection(collectionId).document(docId).set(data);
  }

  @Override
  public List<Map<String, Object>> getCollection(String collectionId)
      throws ExecutionException, InterruptedException {
    // Retrieves all documents in the specified collection
    ApiFuture<QuerySnapshot> future = db.collection(collectionId).get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    List<Map<String, Object>> pins = new ArrayList<>();
    for (QueryDocumentSnapshot document : documents) {
      pins.add(document.getData());
    }
    return pins;
  }

  @Override
  public void clearUserCollection(String collectionId, String uid) {
    // Clears all documents for a specific user
    ApiFuture<QuerySnapshot> future = db.collection(collectionId).whereEqualTo("userId", uid).get();
    try {
      for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
        doc.getReference().delete();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

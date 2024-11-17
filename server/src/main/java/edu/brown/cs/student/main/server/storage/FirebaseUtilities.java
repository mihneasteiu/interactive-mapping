package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Provides utility methods for interacting with Firebase Firestore.
 * Implements the {@link StorageInterface} to perform CRUD operations on Firestore data.
 */
public class FirebaseUtilities implements StorageInterface {

  /**
   * Initializes the Firebase application using the provided service account credentials.
   * The Firebase config file is expected to be located in the "src/main/resources" directory.
   * 
   * @throws IOException If there is an issue reading the Firebase configuration file.
   */
  public FirebaseUtilities() throws IOException {
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");

    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    FirebaseApp.initializeApp(options);
  }

  /**
   * Retrieves all documents from a specified collection for a given user.
   * 
   * @param uid The unique identifier of the user.
   * @param collection_id The ID of the collection to retrieve.
   * @return A list of maps, each representing a document in the collection.
   * @throws InterruptedException If the thread is interrupted during execution.
   * @throws ExecutionException If an error occurs during the retrieval of data.
   * @throws IllegalArgumentException If the uid or collection_id is null.
   */
  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }

    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);
    QuerySnapshot dataQuery = dataRef.get().get();

    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }
    return data;
  }

  /**
   * Adds a new document to a specified collection for a given user.
   * 
   * @param uid The unique identifier of the user.
   * @param collection_id The ID of the collection to add the document to.
   * @param doc_id The ID of the document to add.
   * @param data The data to store in the new document.
   * @throws IllegalArgumentException If any of the input parameters are null.
   */
  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }

    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collectionRef =
        db.collection("users").document(uid).collection(collection_id);

    try {
      WriteResult result = collectionRef.document(doc_id).set(data).get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    System.out.println("Added document");
  }

  /**
   * Clears all data associated with a specific user by removing their document and collections.
   * 
   * @param uid The unique identifier of the user whose data should be cleared.
   * @throws IllegalArgumentException If the uid is null.
   */
  @Override
  public void clearUser(String uid) throws IllegalArgumentException {
    if (uid == null) {
      throw new IllegalArgumentException("removeUser: uid cannot be null");
    }
    try {
      Firestore db = FirestoreClient.getFirestore();
      DocumentReference userDoc = db.collection("users").document(uid);
      deleteDocument(userDoc);
    } catch (Exception e) {
      System.err.println("Error removing user : " + uid);
      System.err.println(e.getMessage());
    }
  }

  /**
   * Recursively deletes a document and all its subcollections.
   * 
   * @param doc The document reference to delete.
   */
  private void deleteDocument(DocumentReference doc) {
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    doc.delete();
  }

  /**
   * Recursively deletes all documents within a collection.
   * 
   * @param collection The collection reference to delete documents from.
   */
  private void deleteCollection(CollectionReference collection) {
    try {
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
      }
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }

  /**
   * Retrieves all pins across all users.
   * 
   * @return A list of maps representing all the pin data from every user.
   * @throws InterruptedException If the thread is interrupted during execution.
   * @throws ExecutionException If an error occurs during the retrieval of data.
   */
  @Override
  public List<Map<String, Object>> getAllPins() throws InterruptedException, ExecutionException {
    List<String> data = new ArrayList<>();
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dataRef = db.collection("users");
    dataRef
        .listDocuments()
        .forEach(
            doc -> {
              data.add(doc.getId());
            });
    System.out.println(data);
    List<Map<String, Object>> allPins = new ArrayList<>();
    for (String userId : data) {
      for (Map<String, Object> word : this.getCollection(userId, "pins")) {
        allPins.add(word);
      }
    }
    return allPins;
  }
}

package edu.brown.cs.student.main.server.utils;

import edu.brown.cs.student.main.server.mapCollection.GeoMapCollection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A utility class for parsing JSON files into GeoMapCollection objects.
 * The class reads a JSON file from the provided file path, parses it, and stores the resulting
 * GeoMapCollection data.
 */
public class JSONParser {
  private GeoMapCollection data;

  /**
   * Constructor that reads a JSON file from the specified file path and parses it into a GeoMapCollection.
   * 
   * @param filePath The path to the JSON file to be read and parsed.
   * @throws FileNotFoundException If the file specified by the filePath does not exist.
   */
  public JSONParser(String filePath) throws FileNotFoundException {
    try {
      // ***************** READING THE FILE *****************
      FileReader jsonReader = new FileReader(filePath);
      BufferedReader br = new BufferedReader(jsonReader);
      String fileString = "";
      String line = br.readLine();
      while (line != null) {
        fileString = fileString + line;
        line = br.readLine();
      }
      jsonReader.close();

      // ****************** CREATING THE ADAPTER ***********
      GeoMapAdapter myadapter = new GeoMapAdapter();
      this.data = myadapter.fromJson(fileString);

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Gets the parsed GeoMapCollection data.
   * 
   * @return The GeoMapCollection object that was parsed from the JSON file.
   */
  public GeoMapCollection getData() {
    return this.data;
  }
}

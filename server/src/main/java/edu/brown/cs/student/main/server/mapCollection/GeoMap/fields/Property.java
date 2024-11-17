package edu.brown.cs.student.main.server.mapCollection.GeoMap.fields;

import edu.brown.cs.student.main.server.mapCollection.GeoMap.GeoMap;
import java.util.Map;

/**
 * Represents the properties associated with a geographical map.
 * This class extends the {@link GeoMap} class and contains additional 
 * information specific to the geographical feature, such as location details and classification.
 */
public class Property extends GeoMap {

  /**
   * The name of the geographical feature, such as the name of the neighborhood or area.
   */
  public String name;

  /**
   * The state in which the geographical feature is located.
   */
  public String state;

  /**
   * The city in which the geographical feature is located.
   */
  public String city;

  /**
   * The HOLC (Home Owners' Loan Corporation) ID associated with the geographical feature.
   * This could be used for historical classification and mapping purposes.
   */
  public String holc_id;

  /**
   * The HOLC grade assigned to the geographical feature, indicating its historical grade.
   * This is used for classification purposes in historical mapping.
   */
  public String holc_grade;

  /**
   * The neighborhood ID associated with the geographical feature.
   * This could be used for grouping or identifying related features.
   */
  public Double neighborhood_id;

  /**
   * A map containing additional area description data.
   * The map's keys and values provide further context or categorization for the geographical feature.
   */
  public Map<String, String> area_description_data;
}

import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";

// Define the property name for filtering GeoJSON data
const propertyName = "holc_grade";

// Define the style for the GeoJSON fill layer
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04", // Green for grade A
      "B",
      "#04b8cc", // Blue for grade B
      "C",
      "#e9ed0e", // Yellow for grade C
      "D",
      "#d11d1d", // Red for grade D
      "#ccc", // Default grey for undefined grades
    ],
    "fill-opacity": 0.2, // Set fill opacity to 20%
  },
};

/**
 * Type guard to check if a given object is a FeatureCollection.
 * @param json - The object to check.
 * @returns true if the object is a FeatureCollection, false otherwise.
 */
function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

/**
 * Fetches the GeoJSON overlay data from the server.
 * @returns A Promise resolving to a GeoJSON FeatureCollection or undefined.
 */
export async function overlayData(): Promise<
  GeoJSON.FeatureCollection | undefined
> {
  const rl_data = await getData();
  return isFeatureCollection(rl_data) ? rl_data : undefined;
}

/**
 * Fetches the raw data from the server.
 * @returns A Promise resolving to the fetched data or throws an error if the request fails.
 */
async function getData(): Promise<any> {
  try {
    const loadResponse = await fetch(
      "http://localhost:3232/getData?minLat=-90&maxLong=180&minLong=-180&maxLat=90"
    );

    // Handle different response codes using switch
    switch (loadResponse.status) {
      case 200: // Success
        const loadJson = await loadResponse.json();
        return loadJson;

      case 404: // Not Found
        const notFoundData = await loadResponse.json();
        throw new Error(`Bad request: ${notFoundData.message}`);
    }
  } catch (error) {
    if (error instanceof Error) {
      throw error; // Re-throw the error with its original message
    }
    throw new Error("Error in fetch");
  }
}

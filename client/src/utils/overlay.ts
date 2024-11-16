import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";

const propertyName = "holc_grade";
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.2,
  },
};

// TODO: MAPS PART 4:
// - Download and import the geojson file
// - Implement the two functions below.

// Import the raw JSON file
// you may need to rename the downloaded .geojson to .json

function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

export async function overlayData(): Promise<GeoJSON.FeatureCollection | undefined> {
  const rl_data = await getData();
  return isFeatureCollection(rl_data) ? rl_data : undefined;
}

// First create mockedStars.json in your project:
// Make sure this file is in a location accessible to your code, like src/data/mockedStars.json

async function getData(): Promise<any> {

  try {
    const loadResponse = await fetch(
      "http://localhost:3232/getData?minLat=-90&maxLong=180&minLong=-180&maxLat=90"
    );

    // Handle different response codes using switch
    switch(loadResponse.status) {
      case 200: // Success
        const loadJson = await loadResponse.json();
        return loadJson;
        
      case 404: // Not Found
        const notFoundData = await loadResponse.json();
        throw new Error(`Bad request: ${notFoundData.message}`);
    }

  } catch (error) {
    if (error instanceof Error) {
      throw error;  // Re-throw the error with its original message
    }
    throw new Error("Error in fetch");
  }
}

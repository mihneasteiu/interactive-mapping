import "mapbox-gl/dist/mapbox-gl.css";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Source,
  ViewStateChangeEvent,
  Marker,
} from "react-map-gl";
import {
  Dispatch,
  SetStateAction,
  useState,
  KeyboardEvent,
  useRef,
  useEffect,
} from "react";
import { geoLayer, overlayData } from "../utils/overlay";

// Mapbox API key from environment variables
const MAPBOX_API_KEY = process.env.MAPBOX_TOKEN;
if (!MAPBOX_API_KEY) {
  console.error("Mapbox API key not found. Please add it to your .env file.");
}

// Interface for latitude and longitude values
export interface LatLong {
  lat: number;
  long: number;
}

// Interface for marker props passed to the Mapbox component
interface markerProps {
  markers: {
    lat: number;
    lng: number;
  }[]; // List of markers to display on the map
  setMarkers: Dispatch<
    SetStateAction<
      {
        lat: number;
        lng: number;
      }[]
    >
  >; // Function to update the markers state
  overlay: GeoJSON.FeatureCollection | undefined; // GeoJSON overlay data for map
  setErrorFetching: Dispatch<SetStateAction<string>>; // Function to set error messages
  user: string; // User ID to associate pins with
}

const initialZoom = 10; // Initial zoom level for the map

/**
 * Mapbox component responsible for rendering the map, pins, and overlay data.
 * Handles map interactions such as adding pins and fetching data.
 */
export default function Mapbox(props: markerProps) {
  /**
   * Handles click events on the map and adds a pin at the clicked location.
   * @param e - The mouse event triggered by clicking on the map
   */
  function onMapClick(e: MapLayerMouseEvent) {
    const lat = e.lngLat.lat;
    const lng = e.lngLat.lng;
    addPins(String(lat), String(lng)); // Add pin at clicked location
  }

  // State to manage map view (longitude, latitude, zoom level)
  const [viewState, setViewState] = useState({
    longitude: -71.4128,
    latitude: 41.824,
    zoom: initialZoom,
  });

  /**
   * Fetches the pin data from the server and updates the markers.
   */
  const fetchPins = async () => {
    try {
      const response = await fetch("http://localhost:3232/getPins");
      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }
      const pinsJson = await response.json();
      if (pinsJson.response_type == "error") {
        throw new Error(pinsJson.error);
      }
      const pinsStrings = pinsJson.pins;
      const pins = pinsStrings.map((pin: string[]) => ({
        lat: parseFloat(pin[0]),
        lng: parseFloat(pin[1]),
      }));
      props.setMarkers(pins); // Update the state with fetched markers
      props.setErrorFetching(""); // Clear any error messages
    } catch (error) {
      props.setErrorFetching("Error fetching pins data:" + error);
    }
  };

  /**
   * Adds a new pin to the server and reloads the pin data.
   * @param ltd - Latitude of the pin
   * @param lng - Longitude of the pin
   */
  const addPins = async (ltd: string, lng: string) => {
    try {
      const response = await fetch(
        `http://localhost:3232/addPin?uid=${props.user}&ltd=${ltd}&lng=${lng}`
      );
      if (!response.ok) {
        throw new Error("Failed to add pins");
      }
      const resp = await response.json();
      if (resp.response_type == "error") {
        throw new Error(resp.error);
      }
      fetchPins(); // Reload the pins after adding a new one
    } catch (error) {
      props.setErrorFetching("Error adding pins data:" + error);
    }
  };

  return (
    <div className="map">
      <Map
        mapboxAccessToken={MAPBOX_API_KEY} // Mapbox API token for authentication
        {...viewState} // Passes current view state to the map
        style={{ width: window.innerWidth, height: window.innerHeight }} // Full-screen map size
        mapStyle={"mapbox://styles/mapbox/streets-v12"} // Style for the map
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)} // Updates view state when map is moved
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)} // Handle map click to add pins
      >
        {/* Overlay layer rendered using geoJSON data */}
        <Source id="geo_data" type="geojson" data={props.overlay}>
          <Layer id={geoLayer.id} type={geoLayer.type} paint={geoLayer.paint} />
        </Source>

        {/* Map markers for each pin */}
        {props.markers.map((marker) => (
          <Marker longitude={marker.lng} latitude={marker.lat} anchor="bottom">
            <img
              src="src/pinimage.png"
              alt="pin"
              style={{
                width: "50px",
                height: "auto",
                objectFit: "contain",
                filter: "drop-shadow(0 0 2px rgba(0,0,0,0.3))", // Pin image style with shadow effect
              }}
            />
          </Marker>
        ))}
      </Map>
    </div>
  );
}

import "mapbox-gl/dist/mapbox-gl.css";
import { useEffect, useState } from "react";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Source,
  ViewStateChangeEvent,
  Marker,
} from "react-map-gl";
import { geoLayer, overlayData } from "../utils/overlay";

const MAPBOX_API_KEY = process.env.MAPBOX_TOKEN;
if (!MAPBOX_API_KEY) {
  console.error("Mapbox API key not found. Please add it to your .env file.");
}

export interface LatLong {
  lat: number;
  long: number;
}

// TODO: MAPS PART 1:
// - fill out starting map state and add to viewState
//
// const ProvidenceLatLong: LatLong = {
//   ...
// };
const initialZoom = 10;

export default function Mapbox() {
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );


  function onMapClick(e: MapLayerMouseEvent) {
    const newMarker = {
      lat: e.lngLat.lat,
      lng: e.lngLat.lng,
    };
    setMarkers((prevMarkers) => [...prevMarkers, newMarker]);
  }

  useEffect(() => {
    setOverlay(overlayData());
  }, []);

  const [viewState, setViewState] = useState({
    longitude: -71.4128,
    latitude: 41.824,
    zoom: initialZoom,
  });

  // TODO: MAPS PART 5:
  // - add the overlay useState
  // - implement the useEffect to fetch the overlay data

  return (
    <div className="map">
      <Map
        mapboxAccessToken={MAPBOX_API_KEY}
        {...viewState}
        // TODO: MAPS PART 2:
        // - add the primary props to the Map (style, mapStyle, onMove).

        style={{ width: window.innerWidth, height: window.innerHeight }}
        mapStyle={"mapbox://styles/mapbox/streets-v12"}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        // TODO: MAPS PART 3:
        // - add the onClick handler

        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
      >
        <Source id="geo_data" type="geojson" data={overlay}>
          <Layer id={geoLayer.id} type={geoLayer.type} paint={geoLayer.paint} />
        </Source>
        {markers.map((marker) => (
          <Marker longitude={marker.lng} latitude={marker.lat} anchor="bottom">
            <img
              src="src/pinimage.webp"
              alt="pin"
              style={{
                width: "50px", // Adjust size as needed
                height: "auto",
                objectFit: "contain",
                filter: "drop-shadow(0 0 2px rgba(0,0,0,0.3))", // Optional: adds a subtle shadow
              }}
            />
          </Marker>
        ))}
      </Map>
    </div>
  );
}

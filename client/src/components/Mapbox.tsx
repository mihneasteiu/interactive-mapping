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

const MAPBOX_API_KEY = process.env.MAPBOX_TOKEN;
if (!MAPBOX_API_KEY) {
  console.error("Mapbox API key not found. Please add it to your .env file.");
}

export interface LatLong {
  lat: number;
  long: number;
}

interface markerProps {
  markers: {
    lat: number;
    lng: number;
  }[];
  setMarkers: Dispatch<
    SetStateAction<
      {
        lat: number;
        lng: number;
      }[]
    >
  >;
  overlay: GeoJSON.FeatureCollection | undefined;
  setErrorFetching: Dispatch<
    SetStateAction<string>
  >;
  user: string
}

const initialZoom = 10;

export default function Mapbox(props: markerProps) {

  function onMapClick(e: MapLayerMouseEvent) {
    const lat = e.lngLat.lat;
    const lng = e.lngLat.lng;
    addPins(String(lat), String(lng));
  }

  const [viewState, setViewState] = useState({
    longitude: -71.4128,
    latitude: 41.824,
    zoom: initialZoom,
  });

  const fetchPins = async () => {
    try {
      const response = await fetch(
        "http://localhost:3232/getPins"
      );
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
      props.setMarkers(pins);
      props.setErrorFetching("");
    } catch (error) {
      props.setErrorFetching("Error fetching pins data:" + error);
    }
  };

  const addPins = async (ltd : string, lng : string) => {
    try {
      const response = await fetch(
        "http://localhost:3232/addPin?uid=" + props.user + "&ltd=" + ltd + "&lng=" + lng
      );
      if (!response.ok) {
        throw new Error("Failed to add pins");
      }
      const resp = await response.json();
      if (resp.response_type == "error") {
        throw new Error(resp.error);
      }
      fetchPins();
    } catch (error) {
      props.setErrorFetching("Error adding pins data:" + error);
    }
  };

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
        <Source id="geo_data" type="geojson" data={props.overlay}>
          <Layer id={geoLayer.id} type={geoLayer.type} paint={geoLayer.paint} />
        </Source>
        {props.markers.map((marker) => (
          <Marker longitude={marker.lng} latitude={marker.lat} anchor="bottom">
            <img
              src="src/pinimage.png"
              alt="pin"
              style={{
                width: "50px",
                height: "auto",
                objectFit: "contain",
                filter: "drop-shadow(0 0 2px rgba(0,0,0,0.3))",
              }}
            />
          </Marker>
        ))}
      </Map>
    </div>
  );
}

import { useState } from "react";
import FirestoreDemo from "./FirestoreDemo";
import Mapbox from "./Mapbox";

export default function MapsGearup() {
  return (
    <div>
      <h1 aria-label="Gearup Title">Map</h1>
      <Mapbox />
    </div>
  );
}

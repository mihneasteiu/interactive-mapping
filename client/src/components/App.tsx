import "../styles/App.css";
import MapsGearup from "./MapsGearup";
import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
} from "@clerk/clerk-react";
import { useEffect, useState } from "react";
import pinsJson from "../geodata/pinpoint.json";

// REMEMBER TO PUT YOUR API KEY IN A FOLDER THAT IS GITIGNORED!!
// (for instance, /src/private/api_key.tsx)
// import {API_KEY} from "./private/api_key"

function App() {
  const pinsStrings = pinsJson.pins;
  const pins = pinsStrings.map((pin) => {lat: parseFloat(pin[0]), lng: parseFloat(pin[1])})
  
  const [markers, setMarkers] = useState<{ lat: number; lng: number }[]>([]);

  const clearPins = () => {
    setMarkers([]);
  }

  return (
    <div className="App">
      <SignedOut>
        <SignInButton />
      </SignedOut>
      <SignedIn>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
          }}
        >
          <div
            style={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
              alignContent: "center",
              padding: "10px",
              gap: "10px",
            }}
          >
            <SignOutButton />
            <UserButton />
          </div>
          <button
          onClick={clearPins}>
            Clear pins
            </button>
          <MapsGearup />
        </div>
      </SignedIn>
    </div>
  );
}

export default App;

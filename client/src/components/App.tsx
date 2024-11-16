import "../styles/App.css";
import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
} from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import pinsJson from "../geodata/pinpoint.json";
import Mapbox from "./Mapbox";
import { overlayData } from "../utils/overlay";

// REMEMBER TO PUT YOUR API KEY IN A FOLDER THAT IS GITIGNORED!!
// (for instance, /src/private/api_key.tsx)
// import {API_KEY} from "./private/api_key"

function App() {
  const pinsStrings = pinsJson.pins;
  const pins = pinsStrings.map((pin) => ({
    lat: parseFloat(pin[0]),
    lng: parseFloat(pin[1]),
  }));
  const [keyword, setKeyword] = useState("");
  const [errorFetching, setErrorFetching] = useState("");
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );
  const [markers, setMarkers] = useState<{ lat: number; lng: number }[]>(pins);

  const fetchData = async () => {
    try {
      const fetchedData = await overlayData();
      setOverlay(fetchedData);
    } catch (error) {
      setErrorFetching("Error fetching overlay data:" + error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const fetchOverlay = async () => {
    if (keyword == "") {
      setErrorFetching("Please enter a keyword");
    } else {
      try {
        const response = await fetch(
          "http://localhost:3232/getArea?key=" + keyword
        );
        if (!response.ok) {
          throw new Error("Failed to fetch overlay data");
        }
        const newOverlay = await response.json();
        if (newOverlay.response_type == "error") {
          throw new Error(newOverlay.error);
        }
        setOverlay(newOverlay);
        setErrorFetching("");
      } catch (error) {
        setErrorFetching("Error fetching overlay data:" + error);
      }
    }
  };

  const clearPins = () => {
    setMarkers([]);
  };

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
          <div>
            <input
              type="text"
              value={keyword}
              placeholder="Enter a keyword"
              onChange={(e) => setKeyword(e.target.value)}
              style={{ padding: "5px", width: "200px" }}
            />
            <button
              onClick={() => fetchOverlay()}
              style={{ marginLeft: "10px" }}
            >
              Search
            </button>
          </div>
          {errorFetching && <div>{errorFetching}</div>}
          <div>
            <button onClick={() => fetchData()}>Restart redlining</button>
            <button onClick={clearPins}>Clear pins</button>
          </div>
          <Mapbox markers={markers} setMarkers={setMarkers} overlay={overlay} />
        </div>
      </SignedIn>
    </div>
  );
}

export default App;

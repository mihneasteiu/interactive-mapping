import "../styles/App.css";
import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
  useUser
} from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import Mapbox from "./Mapbox";
import { overlayData } from "../utils/overlay";

function App() {

  const [keyword, setKeyword] = useState("");
  const [errorFetching, setErrorFetching] = useState("");
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );
  const [markers, setMarkers] = useState<{ lat: number; lng: number }[]>([]);
  const [userId, setUserId] = useState<string>("");
  const { user } = useUser();

  const fetchData = async () => {
    try {
      const fetchedData = await overlayData();
      setOverlay(fetchedData);
      fetchPins();
    } catch (error) {
      setErrorFetching("Error fetching overlay data:" + error);
    }
  };

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
      setMarkers(pins);
      setErrorFetching("");
    } catch (error) {
      setErrorFetching("Error fetching pins data:" + error);
    }
  };

  useEffect(() => {
    fetchData();
    if (user) setUserId(user.id);
  }, [user]);

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

  const clearPins = async () => {
    try {
      const response = await fetch(
        "http://localhost:3232/clearPins?uid=" + userId
      );
      if (!response.ok) {
        throw new Error("Failed to clear pins");
      }
      const resp = await response.json();
      if (resp.response_type == "error") {
        throw new Error(resp.error);
      }
      fetchPins();
    } catch (error) {
      setErrorFetching("Error clearing pins data:" + error);
    }
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
            <button onClick={() => clearPins()}>Clear pins</button>
          </div>
          <Mapbox markers={markers} setMarkers={setMarkers} overlay={overlay} setErrorFetching={setErrorFetching} user={userId}/>
        </div>
      </SignedIn>
    </div>
  );
}

export default App;

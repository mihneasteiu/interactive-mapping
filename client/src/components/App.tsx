import "../styles/App.css";
import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
  useUser,
} from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import Mapbox from "./Mapbox";
import { overlayData } from "../utils/overlay";

/**
 * The main application component that handles user authentication,
 * overlay data, pin markers, and interactions with the map.
 */
function App() {
  // State variables
  const [keyword, setKeyword] = useState(""); // Keyword for searching overlay data
  const [errorFetching, setErrorFetching] = useState(""); // Error message for fetch requests
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  ); // GeoJSON overlay data
  const [markers, setMarkers] = useState<{ lat: number; lng: number }[]>([]); // Markers for the map
  const [userId, setUserId] = useState<string>(""); // Current user's ID
  const { user } = useUser(); // User data from Clerk

  /**
   * Fetches initial overlay data and pin markers from the server.
   */
  const fetchData = async () => {
    try {
      const fetchedData = await overlayData();
      setOverlay(fetchedData); // Set the fetched overlay data
      fetchPins(); // Fetch pins data
    } catch (error) {
      setErrorFetching("Error fetching overlay data:" + error);
    }
  };

  /**
   * Fetches pin data from the server and updates the markers on the map.
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
      setMarkers(pins); // Set the fetched pins as markers on the map
      setErrorFetching(""); // Clear any previous errors
    } catch (error) {
      setErrorFetching("Error fetching pins data:" + error);
    }
  };

  // Fetch data when the component mounts or when the user changes
  useEffect(() => {
    fetchData();
    if (user) setUserId(user.id); // Set the user ID if available
  }, [user]);

  /**
   * Fetches overlay data based on the provided keyword and updates the overlay state.
   */
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
        setOverlay(newOverlay); // Set the new overlay data
        setErrorFetching(""); // Clear any previous errors
      } catch (error) {
        setErrorFetching("Error fetching overlay data:" + error);
      }
    }
  };

  /**
   * Clears all user pins and reloads the pins data.
   */
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
      fetchPins(); // Reload the pins after clearing
    } catch (error) {
      setErrorFetching("Error clearing pins data:" + error);
    }
  };

  return (
    <div className="App">
      {/* Render sign-in button if user is not signed in */}
      <SignedOut>
        <SignInButton />
      </SignedOut>

      {/* Main content for signed-in users */}
      <SignedIn>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
          }}
        >
          {/* User controls (sign out, user info) */}
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

          {/* Keyword input and search button */}
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

          {/* Error message display */}
          {errorFetching && <div>{errorFetching}</div>}

          {/* Control buttons for restarting and clearing pins */}
          <div>
            <button onClick={() => fetchData()}>Restart redlining</button>
            <button onClick={() => clearPins()}>Clear pins</button>
          </div>

          {/* Mapbox component rendering the map with markers and overlay */}
          <Mapbox
            markers={markers}
            setMarkers={setMarkers}
            overlay={overlay}
            setErrorFetching={setErrorFetching}
            user={userId}
          />
        </div>
      </SignedIn>
    </div>
  );
}

export default App;

# Project Details
This is a full-stack web application developed as part of Brown University's CSCI 0320 "Introduction to Software Engineering" class. The application enables users to explore historical redlining data and contribute reports on landlord-tenant experiences via an interactive map interface. Users can pan and zoom across U.S. cities, overlay redlining data from the 1930s, and drop pins to mark negative experiences with landlords. These pins are visible to all authenticated users and are persistently stored in a cloud database to ensure that they remain across sessions. Users can also clear the pins they’ve dropped. 

On the backend, the application serves historical data filtered by geographic bounding boxes and handles persistent pin storage. It also supports keyword-based search functionality, allowing users to highlight regions on the map based on terms found in historical area descriptions.

# Technologies Used
The frontend is built using React and TypeScript, with interactive geographic rendering handled by Mapbox through the `react-map-gl` framework. Clerk is used to provide secure authentication. The backend is implemented in Java and interacts with Firebase Firestore to store and retrieve pin data. API endpoints on the backend also support filtered queries and search functionality. End-to-end testing is implemented using Playwright, with mock data infrastructure for frontend testing. Caching is used on the backend to optimize repeated queries for redlining data.

# Usage Instructions
To run the application locally, the frontend and backend must be started in separate terminals. In the client folder, run "npm install" followed by "npm start" to start the frontend. In the server folder, run "mvn package" and then execute the server with "./run". Before running, ensure Firebase and Clerk credentials are properly configured in both environments.

> **GETTING STARTED:** The Maps gearup code is a great starting point for both the backend and frontend. You might also want to grab code snippets from your final REPL project.

# Project Details

Github link: https://github.com/cs0320-f24/maps-fbaylav-msteiu.git

This Sprint 5.2 consists of an interactive map where a user logs in and places pins to the map. The user logs in via Clerk and is assigned a unique user ID. The user could click on the map for adding pins. Via Firevase, we made it so that the other users could see the pins anonimously. There are other features such as restarting redlining and locating an are by a keyword. When the pins are cleared on the map, other users could see that the pins have been cleared.

# Design Choices

We included handlers in our backend to do the logical adding, sharing, and Firebase usage of the pins. The handlers do the tasks for adding, listing, clearing the pins on the Firebase. Other handlers consist of logical implementation of the redlining and boundary box calculations. Then we linked our backend to the frontend to get the functionality.

# Errors/Bugs

We did not encounter any major errors or bugs. However, on App.spec.ts, the "process" keyword is throwing errors that we couldn't solve. Even though there are errors regarding this keyword, it is not prohibiting us from the testing suite.

# Tests

General Tests
Basic Labels Verification
Ensures key UI labels like "Restart redlining," "Sign out," "Clear pins," and "Restart" are visible upon login.

No Pins on Initial Load
Confirms that no pins are displayed when the application loads for the first time.

Mocked Pins Display
Verifies that pins mocked via an API response are displayed correctly on the map.

Pin Management Tests
Add and Clear Pins
Tests the functionality of adding a new pin to the map and clearing all pins using the "Clear pins" button.

Pin Persistence After Restart
Confirms that pins added to the map remain visible even after restarting the redlining process.

Error Handling Tests
Search Error for Empty Input
Validates that an appropriate error message appears when attempting to search without entering a keyword.

Restart Failure Handling
Mocks a failed API response during restart and ensures an error message is displayed.

Multi-User Functionality Tests
Multi-User Pin Visibility
Ensures that pins added by one user are visible to another user after a page refresh and verifies pin clearing syncs across sessions.
Authentication Tests

Invalid Login Credentials
Simulates incorrect username or password input and verifies that an error message appears.

# Collaboration

We have used LLMs for guidance and conceptual questions about how to add pins on click and adding a pre-determined set of pins.

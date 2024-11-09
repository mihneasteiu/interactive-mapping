> **GETTING STARTED:** The Maps gearup code is a great starting point for both the backend and frontend. You might also want to grab code snippets from your final REPL project.

# Project Details

Github link: https://github.com/cs0320-f24/maps-fbaylav-msteiu.git

This Sprint 5.1 consists of an interactive map where a user logs in and places pins to the map. The map is specifically the map of the US, and it is centered in Providence. There are pre-determined pins on major US cities. The user could add pins by clicking on the map and clear them all when desired.

# Design Choices

We included 15 pre-determined pins. We chose the pin image to be a .png file with its background removed. The pin could change size when zoomed in or out interactively with the map. The buttons are simple and convenient to use. The user could simply press the "Clear pins" button to remove all pins on the map and click on anywhere on the map for adding a pin.

# Errors/Bugs

We did not encounter any major errors or bugs. However, on App.spec.ts, the "process" keyword is throwing errors that we couldn't solve. Even though there are errors regarding this keyword, it is not prohibiting us from the testing suite.

# Tests

Basic UI Labels Check:
Verifies that essential labels like "Sprint 5.1," "Sign out," and "Clear pins" are visible upon loading the application.

Pins Visibility on Load:
Confirms that 15 map pins are displayed upon loading and that each pin is visible, verifying the initial state of the map.

Clear Pins Functionality:
Checks that the "Clear pins" button removes all pins from the map, ensuring the clearing functionality works correctly.

Adding a New Pin:
Simulates a random map click to add a new pin, then verifies that the pin count increases by one.

Add and Clear Pins:
Adds a new pin by clicking on a random spot on the map and subsequently clears all pins by clicking the "Clear pins" button, confirming both add and clear actions work in sequence.

# Collaboration

We have used LLMs for guidance and conceptual questions about how to add pins on click and adding a pre-determined set of pins.

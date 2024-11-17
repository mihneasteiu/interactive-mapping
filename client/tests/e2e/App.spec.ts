import { expect, test } from "@playwright/test";
import {
  clerk,
  clerkSetup,
  setupClerkTestingToken,
} from "@clerk/testing/playwright";

/**
 * Test: Can't connect with wrong credentials
 * Description: This test ensures that the application handles incorrect sign-in attempts
 * by showing the appropriate error message related to wrong credentials.
 */
test("Can't connect with wrong credentials", async ({ page }) => {
  try {
    // Attempt to sign in with incorrect credentials
    await signInUser(
      page,
      process.env.E2E_CLERK_USER_USERNAME1,
      process.env.E2E_CLERK_USER_PASSWORD2
    );
  } catch (error) {
    // Assert that the error is the expected Clerk error message
    expect(error.message).toContain(
      "Clerk: Failed to sign in: Password is incorrect"
    );
  }
});

/**
 * Test: I see the basic labels when I open the project
 * Description: This test verifies that the essential interface elements are visible after signing in,
 * ensuring that the user can interact with them.
 */
test("I see the basic labels when I open the project", async ({ page }) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1,
    process.env.E2E_CLERK_USER_PASSWORD1
  );
  await expect(
    page.getByRole("button", { name: "Sign out" })
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Clear pins" })
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Restart redlining" })
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Search" })
  ).toBeVisible();
});

/**
 * Test: I see no pins on load
 * Description: This test checks that there are no pins initially visible on the map when the page loads.
 */
test("I see no pins on load", async ({ page }) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  await page.waitForLoadState("networkidle");
  const pins = await page.locator("img[alt='pin']");
  await expect(pins).toHaveCount(0);
});

/**
 * Test: I see the pins on load, mocked
 * Description: This test ensures that pins are visible on the map when mock data is provided via routing.
 */
test("I see the pins on load, mocked", async ({ page }) => {
  await page.route("**/getPins", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        pins: [
          ["23", "23"],
          ["24", "24"],
        ],
      }),
    });
  });
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  await page.waitForLoadState("networkidle");
  const pins = await page.locator("img[alt='pin']");
  await expect(pins).toHaveCount(2);
  for (let i = 0; i < 2; i++) {
    await expect(pins.nth(i)).toBeVisible();
  }
});

/**
 * Test: Pins are shown and cleared after clicking 'Clear Pins' button
 * Description: This test ensures that pins can be added to the map and then cleared when the 'Clear Pins' button is clicked.
 */
test("Pins are shown and cleared after clicking 'Clear Pins' button", async ({
  page,
}) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  const pins = await page.locator("img[alt='pin']");
  const initialPinCount = await pins.count();
  const map = await page.locator(".map");
  await map.click({ position: { x: 300, y: 300 } });
  await expect(pins).toHaveCount(initialPinCount + 1);
  const clearPinsButton = page.getByRole("button", { name: "Clear pins" });
  await clearPinsButton.click();
  await expect(pins).toHaveCount(initialPinCount);
});

/**
 * Test: Pins persist after redlining restart
 * Description: This test checks that added pins persist even after restarting the redlining process.
 */
test("Pins persist after redlining restart", async ({ page }) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  await page.waitForLoadState("networkidle");
  // Get initial pin count
  const pins = await page.locator("img[alt='pin']");
  const initialPinCount = await pins.count();

  // Add a new pin
  const map = await page.locator(".map");
  await map.click({ position: { x: 200, y: 200 } });

  // Verify pin was added
  await expect(pins).toHaveCount(initialPinCount + 1);

  // Click restart button
  const restartButton = page.getByRole("button", { name: "Restart redlining" });
  await restartButton.click();

  // Wait for network requests to complete
  await page.waitForLoadState("networkidle");

  // Verify pins are still present after restart
  await expect(pins).toHaveCount(initialPinCount + 1);
});

/**
 * Test: Error shows up when searching for empty keyword
 * Description: This test ensures that an appropriate error message is displayed when an empty search keyword is submitted.
 */
test("Error shows up when searching for empty keyword", async ({ page }) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  const searchButton = page.getByRole("button", { name: "Search" });
  await searchButton.click();

  // Wait for error message
  await expect(page.getByText("Please")).toBeVisible();
});

/**
 * Test: Error message appears when restart fails
 * Description: This test ensures that the user sees an error message if the restart action fails due to network or server issues.
 */
test("Error message appears when restart fails", async ({ page }) => {
  await page.route("**/getData*", async (route) => {
    await route.fulfill({
      status: 404,
      body: JSON.stringify({
        response_type: "error",
        error: "Failed to fetch overlay data",
      }),
    });
  });
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  // Click restart button
  const restartButton = page.getByRole("button", { name: "Restart redlining" });
  await restartButton.click();

  // Wait for error message
  await expect(page.getByText(/Error fetching overlay data/)).toBeVisible();
});

/**
 * Test Suite: Multi-user Pin Visibility
 * Description: This suite tests that pins are visible across different user sessions.
 */
test.describe("Multi-user Pin Visibility", () => {
  test("pins should be visible across different user sessions", async ({
    browser,
  }) => {
    // Create two browser contexts for two different users
    const userContext1 = await browser.newContext();
    const userContext2 = await browser.newContext();

    const page1 = await userContext1.newPage();
    const page2 = await userContext2.newPage();

    await signInUser(
      page1,
      process.env.E2E_CLERK_USER_USERNAME1,
      process.env.E2E_CLERK_USER_PASSWORD1
    );

    // Sign in second user (Furkan)
    await signInUser(
      page2,
      process.env.E2E_CLERK_USER_USERNAME2,
      process.env.E2E_CLERK_USER_PASSWORD2
    );

    // Get initial pin count for both users
    const initialPins1 = await page1.locator("img[alt='pin']").count();
    const initialPins2 = await page2.locator("img[alt='pin']").count();

    // Verify both users see the same initial pins
    expect(initialPins1).toBe(initialPins2);

    // User 1 adds a pin
    const map1 = await page1.locator(".map");
    await map1.click({ position: { x: 200, y: 200 } });

    // Wait for network requests to complete
    await page1.waitForLoadState("networkidle");
    await page2.waitForLoadState("networkidle");

    // Verify pin count increased for first user
    await expect(page1.locator("img[alt='pin']")).toHaveCount(initialPins1 + 1);

    // Refresh second user's page to see updates
    await page2.reload();
    await page2.waitForLoadState("networkidle");

    // Verify second user sees the new pin
    await expect(page2.locator("img[alt='pin']")).toHaveCount(initialPins2 + 1);

    // Test clearing pins
    const clearPinsButton = page1.getByRole("button", { name: "Clear pins" });
    await clearPinsButton.click();

    // Wait for network requests to complete
    await page1.waitForLoadState("networkidle");
    await page2.waitForLoadState("networkidle");

    // Verify pins are cleared for first user
    await expect(page1.locator("img[alt='pin']")).toHaveCount(0);

    // Refresh second user's page
    await page2.reload();
    await page2.waitForLoadState("networkidle");

    // Verify pins are cleared for second user as well
    await expect(page2.locator("img[alt='pin']")).toHaveCount(0);

    // Clean up
    await userContext1.close();
    await userContext2.close();
  });
});

/**
 * This function sends a sign-in request to Clerk using the provided username and password.
 * It sets up the necessary Clerk testing token, navigates to the local app, and performs the sign-in action.
 * 
 * @param {Page} page - The Playwright page object that represents the browser tab in which the sign-in process will occur.
 * @param {string} username - The username (or email) of the user attempting to sign in.
 * @param {string} password - The password associated with the username.
 * 
 * @returns {Promise<void>} - A promise that resolves when the sign-in process is completed.
 */
async function signInUser(page, username, password) {
  // Setup Clerk testing token for the page
  await clerkSetup;
  await setupClerkTestingToken({
    page,
  });

  // Navigate to the local app
  await page.goto("http://localhost:8000/");

  // Ensure Clerk has loaded on the page
  await clerk.loaded({ page });

  // Perform the sign-in action with the provided credentials
  await clerk.signIn({
    page,
    signInParams: {
      strategy: "password", // Use password strategy for authentication
      password: password,   // Password to sign in
      identifier: username, // Username (email or username) to sign in
    },
  });
}

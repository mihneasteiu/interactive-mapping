import { expect, test } from "@playwright/test";
import {
  clerk,
  clerkSetup,
  setupClerkTestingToken,
} from "@clerk/testing/playwright";


/*test.beforeEach(async ({ page }) => {
  await clerkSetup;
  await setupClerkTestingToken({
    page,
  });
  await page.goto("http://localhost:8000/");
  await clerk.loaded({ page });
  await clerk.signIn({
    page,
    signInParams: {
      strategy: "password",
      password: process.env.E2E_CLERK_USER_PASSWORD1!,
      identifier: process.env.E2E_CLERK_USER_USERNAME1!,
    },
  });
});*/


test("I see the basic labels when I open the project", async ({ page }) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1,
    process.env.E2E_CLERK_USER_PASSWORD1
  );
  await expect(page.getByLabel("Restart redlining")).toBeVisible;
  await expect(page.getByLabel("Sign out")).toBeVisible;
  await expect(page.getByLabel("Clear pins")).toBeVisible;
  await expect(page.getByLabel("Restart")).toBeVisible;
});

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

test("I see the pins on load, mocked", async ({ page }) => {
  await page.route("**/getPins", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        pins: [
          ["23","23"],
          ["24","24"]
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

test("Error shows up when searching for empty keyword", async ({page}) => {
  await signInUser(
    page,
    process.env.E2E_CLERK_USER_USERNAME1!,
    process.env.E2E_CLERK_USER_PASSWORD1!
  );
  const searchButton = page.getByRole("button", { name: "Search" });
  await searchButton.click();

  // Wait for error message
  await expect(page.getByText("Please")).toBeVisible();
})

test("Error message appears when restart fails", async ({ page }) => {
  await page.route('**/getData*', async route => {
    await route.fulfill({
      status: 404,
      body: JSON.stringify({ response_type: 'error', error: 'Failed to fetch overlay data' })
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

test.describe('Multi-user Pin Visibility', () => {
  test('pins should be visible across different user sessions', async ({ browser }) => {
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
})

async function signInUser(page, username, password) {
  await clerkSetup;
  await setupClerkTestingToken({
    page,
  });
  await page.goto("http://localhost:8000/");
  await clerk.loaded({ page });
  await clerk.signIn({
    page,
    signInParams: {
      strategy: "password",
      password: password,
      identifier: username,
    },
  });
}
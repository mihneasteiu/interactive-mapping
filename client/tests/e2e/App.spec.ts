import { expect, test } from "@playwright/test";
import {
  clerk,
  clerkSetup,
  setupClerkTestingToken,
} from "@clerk/testing/playwright";

test.beforeEach(async ({ page }) => {
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
      password: process.env.E2E_CLERK_USER_PASSWORD!,
      identifier: process.env.E2E_CLERK_USER_USERNAME!,
    },
  });
});

test("I see the basic labels when I open the project", async ({ page }) => {
  await expect(page.getByLabel("Sprint 5.1")).toBeVisible;
  await expect(page.getByLabel("Sign out")).toBeVisible;
  await expect(page.getByLabel("Clear pins")).toBeVisible;
});

test("I see the pins on load", async ({ page }) => {
  await page.waitForLoadState("networkidle");
  const pins = await page.locator("img[alt='pin']");
  await expect(pins).toHaveCount(15);
  for (let i = 0; i < 15; i++) {
    await expect(pins.nth(i)).toBeVisible();
  }
});

test("Pins are cleared after clicking 'Clear Pins' button", async ({
  page,
}) => {
  const pins = page.locator("img[alt='pin']");
  await expect(pins).toHaveCount(15);
  const clearPinsButton = page.getByRole("button", { name: "Clear pins" });
  await clearPinsButton.click();
  await expect(pins).toHaveCount(0);
});

test("A new pin is added on random map click", async ({ page }) => {
  const pins = page.locator("img[alt='pin']");
  const initialPinCount = await pins.count();
  const map = page.locator(".map");
  await map.click({ position: { x: 300, y: 300 } });
  await expect(pins).toHaveCount(initialPinCount + 1);
});

test("Add a pin on map click and clear all pins", async ({ page }) => {
  const pins = page.locator("img[alt='pin']");
  const initialPinCount = await pins.count();
  const map = page.locator(".map");
  await map.click({ position: { x: 300, y: 300 } });
  await expect(pins).toHaveCount(initialPinCount + 1);
  const clearPinsButton = page.getByRole("button", { name: "Clear pins" });
  await clearPinsButton.click();
  await expect(pins).toHaveCount(0);
});

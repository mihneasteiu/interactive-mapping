import { expect, test } from "@playwright/test";
import {
  clerk,
  clerkSetup,
  setupClerkTestingToken,
} from "@clerk/testing/playwright";

test.beforeEach(async ({ page }) => {
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

test.afterEach(async ({ page }) => {
  await clerk.signOut({ page });
});

test("I see the basic labels when I open the project", async ({ page }) => {
  await expect(page.getByLabel("Sprint 5.1")).toBeVisible;
  await expect(page.getByLabel("Sign out")).toBeVisible;
  await expect(page.getByLabel("Clear pins")).toBeVisible;
});

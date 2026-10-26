import { expect } from '@playwright/test';

export const adminEmail = process.env.E2E_ADMIN_EMAIL || 'admin@gestor.chef';
export const adminPassword = process.env.E2E_ADMIN_PASSWORD || 'Admin123!';

export async function login(page, email = adminEmail, password = adminPassword) {
  await page.goto('/login');
  await page.getByLabel('Email').fill(email);
  await page.getByLabel('Contraseña').fill(password);
  await page.getByRole('button', { name: 'Ingresar' }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(page.getByRole('heading', { name: 'Resumen general' })).toBeVisible();
}

export async function go(page, path, heading) {
  await page.goto(path);
  await expect(page.getByRole('heading', { name: heading, exact: true })).toBeVisible();
  await expect(page.locator('main .loading-state')).toHaveCount(0);
  await expect(page.locator('main .alert--error')).toHaveCount(0);
}

export function row(page, text) {
  return page.locator('tbody tr').filter({ hasText: text });
}

export function unique(prefix) {
  return `${prefix} E2E ${Date.now()} ${Math.floor(Math.random() * 10000)}`;
}

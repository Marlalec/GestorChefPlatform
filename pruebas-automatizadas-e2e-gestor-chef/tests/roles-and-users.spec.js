import { test, expect } from '@playwright/test';
import { go, login, row, unique } from './helpers.js';

test('cocina solo accede a sus módulos', async ({ page }) => {
  await login(page, process.env.E2E_COCINA_EMAIL || 'cocina@gestor.chef', process.env.E2E_COCINA_PASSWORD || 'Cocina123!');
  for (const [path, heading] of [['/inventory', 'Inventario'], ['/products', 'Productos e insumos'], ['/recipes', 'Recetas'], ['/orders', 'Pedidos Mesa'], ['/wastes', 'Desperdicios'], ['/alerts', 'Alertas']]) {
    await go(page, path, heading);
  }
  await page.goto('/users');
  await expect(page).toHaveURL(/\/dashboard$/);
  await page.goto('/reports');
  await expect(page).toHaveURL(/\/dashboard$/);
});

test('contabilidad solo accede a sus módulos', async ({ page }) => {
  await login(page, process.env.E2E_CONTABLE_EMAIL || 'contable@gestor.chef', process.env.E2E_CONTABLE_PASSWORD || 'Contable123!');
  for (const [path, heading] of [['/suppliers', 'Proveedores'], ['/reports', 'Reportes'], ['/wastes', 'Desperdicios'], ['/alerts', 'Alertas'], ['/roles', 'Roles']]) {
    await go(page, path, heading);
  }
  await page.goto('/products');
  await expect(page).toHaveURL(/\/dashboard$/);
  await page.goto('/users');
  await expect(page).toHaveURL(/\/dashboard$/);
});

test('administración edita y cambia el estado de un usuario aislado', async ({ page, request }) => {
  const name = unique('Usuario');
  const email = `e2e-${Date.now()}-${Math.floor(Math.random() * 10000)}@example.com`;
  const api = process.env.E2E_API_URL || 'http://localhost:8080/api';
  const created = await request.post(`${api}/auth/register`, {
    data: { name, email, password: 'E2ePassword123!', rol: 'COCINA', phone: '3000000000' }
  });
  expect(created.ok(), await created.text()).toBeTruthy();
  await login(page);
  await go(page, '/users', 'Usuarios');
  await page.getByPlaceholder('Buscar usuario').fill(email);
  await expect(row(page, email)).toBeVisible();
  await row(page, email).getByRole('button', { name: 'Editar' }).click();
  const dialog = page.getByRole('dialog');
  await dialog.getByLabel('Nombre').fill(`${name} actualizado`);
  await dialog.getByRole('button', { name: 'Guardar usuario' }).click();
  await expect(row(page, email)).toContainText('actualizado');
  await row(page, email).getByRole('button', { name: 'Inactivar' }).click();
  await expect(row(page, email)).toContainText('Inactivo');
  await row(page, email).getByRole('button', { name: 'Activar' }).click();
  await expect(row(page, email)).toContainText('Activo');
  page.once('dialog', (confirm) => confirm.accept());
  await row(page, email).getByRole('button', { name: 'Eliminar' }).click();
  await expect(page.getByText('Usuario eliminado correctamente')).toBeVisible();
});

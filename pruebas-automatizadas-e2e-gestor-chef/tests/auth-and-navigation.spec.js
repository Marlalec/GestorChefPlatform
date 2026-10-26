import { test, expect } from '@playwright/test';
import { adminEmail, adminPassword, go, login } from './helpers.js';

test('inicio público, redirección protegida, sesión y cierre', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByText('GESTOR CHEF').first()).toBeVisible();
  await page.goto('/dashboard');
  await expect(page).toHaveURL(/\/login$/);
  await login(page);
  await page.reload();
  await expect(page.getByRole('heading', { name: 'Resumen general' })).toBeVisible();
  await page.getByRole('button', { name: 'Cerrar sesión' }).click();
  await expect(page).toHaveURL(/\/login$/);
  await page.goto('/products');
  await expect(page).toHaveURL(/\/login$/);
});

test('credenciales inválidas muestran un error', async ({ page }) => {
  await page.goto('/login');
  await page.getByLabel('Email').fill(adminEmail);
  await page.getByLabel('Contraseña').fill(`${adminPassword}incorrecta`);
  await page.getByRole('button', { name: 'Ingresar' }).click();
  await expect(page).toHaveURL(/\/login$/);
  await expect(page.locator('.alert').filter({ hasText: /.+/ })).toBeVisible();
});

test('todos los módulos del administrador cargan', async ({ page }) => {
  await login(page);
  const modules = [
    ['/dashboard', 'Resumen general'], ['/inventory', 'Inventario'],
    ['/products', 'Productos e insumos'], ['/categories', 'Categorías'],
    ['/suppliers', 'Proveedores'], ['/recipes', 'Recetas'],
    ['/orders', 'Pedidos Mesa'], ['/wastes', 'Desperdicios'],
    ['/alerts', 'Alertas'], ['/reports', 'Reportes'],
    ['/users', 'Usuarios'], ['/roles', 'Roles']
  ];
  for (const [path, heading] of modules) await go(page, path, heading);
});

test('las rutas desconocidas regresan al inicio público', async ({ page }) => {
  await page.goto('/ruta-inexistente');
  await expect(page).toHaveURL(/\/$/);
});

import { test, expect } from '@playwright/test';
import { go, login, row, unique } from './helpers.js';

test('proveedor, producto, inventario, receta, pedido y desperdicio', async ({ page }) => {
  const supplier = unique('Proveedor');
  const product = unique('Producto');
  const recipe = unique('Receta');
  const table = unique('Mesa');
  await login(page);

  await go(page, '/suppliers', 'Proveedores');
  await page.getByRole('button', { name: 'Nuevo proveedor' }).click();
  let dialog = page.getByRole('dialog');
  await dialog.getByLabel('Nombre').fill(supplier);
  await dialog.getByLabel('Contacto').fill('Prueba E2E');
  await dialog.getByLabel('Email').fill('proveedor-e2e@example.com');
  await dialog.getByRole('button', { name: 'Guardar proveedor' }).click();
  await expect(row(page, supplier)).toBeVisible();
  await row(page, supplier).getByRole('button', { name: 'Editar' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Contacto').fill('Contacto actualizado E2E');
  await dialog.getByRole('button', { name: 'Guardar proveedor' }).click();
  await expect(row(page, supplier)).toContainText('Contacto actualizado E2E');

  await go(page, '/products', 'Productos e insumos');
  await page.getByRole('button', { name: 'Nuevo producto' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Nombre').fill(product);
  await dialog.getByLabel('Proveedor').selectOption({ label: supplier });
  await dialog.getByLabel('Cantidad', { exact: true }).fill('25');
  await dialog.getByLabel('Cantidad mínima').fill('5');
  await dialog.getByLabel('Precio').fill('1000');
  await dialog.getByRole('button', { name: 'Guardar producto' }).click();
  await expect(row(page, product)).toContainText(supplier);
  await page.getByPlaceholder('Buscar por nombre, categoría o proveedor').fill(product);
  await expect(page.locator('tbody tr')).toHaveCount(1);
  await row(page, product).getByRole('button', { name: 'Editar' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Precio').fill('1200');
  await dialog.getByRole('button', { name: 'Guardar producto' }).click();
  await expect(page.getByText('Producto actualizado correctamente')).toBeVisible();

  await go(page, '/categories', 'Categorías');
  await expect(page.getByRole('heading', { name: 'Resumen por categoría' })).toBeVisible();

  await go(page, '/inventory', 'Inventario');
  await page.getByRole('button', { name: 'Sincronizar' }).click();
  await expect(page.getByText('Inventario sincronizado correctamente')).toBeVisible();
  await page.getByRole('button', { name: 'Registrar movimiento' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Producto').selectOption({ label: product });
  await dialog.getByLabel(/^Cantidad/).fill('2');
  await dialog.getByRole('button', { name: 'Registrar movimiento' }).click();
  await expect(page.getByText('Movimiento registrado correctamente')).toBeVisible();
  await expect(page.locator('tbody tr').filter({ hasText: product }).first()).toBeVisible();

  await go(page, '/recipes', 'Recetas');
  await page.getByRole('button', { name: 'Nueva receta' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Plato').fill(recipe);
  await dialog.getByLabel('Precio venta').fill('12000');
  await dialog.getByLabel('Tiempo preparación').fill('15');
  await dialog.locator('.ingredient-row select').selectOption({ label: product });
  await dialog.getByPlaceholder('Cantidad').fill('1');
  await dialog.getByRole('button', { name: 'Guardar receta' }).click();
  await expect(row(page, recipe)).toBeVisible();
  page.once('dialog', async (alert) => { expect(alert.message()).toContain('Costo calculado'); await alert.accept(); });
  await row(page, recipe).getByRole('button', { name: 'Costo' }).click();

  await go(page, '/orders', 'Pedidos Mesa');
  await page.getByRole('button', { name: 'Nueva orden' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Mesa').fill(table);
  await dialog.getByLabel('Plato').selectOption({ label: recipe });
  await dialog.getByRole('button', { name: 'Crear orden' }).click();
  await expect(row(page, table)).toBeVisible();
  await row(page, table).getByRole('button', { name: 'Procesar' }).click();
  await expect(row(page, table)).toContainText('En proceso');
  await row(page, table).getByRole('button', { name: 'Completar' }).click();
  await expect(row(page, table)).toContainText('Completado');

  await go(page, '/wastes', 'Desperdicios');
  await page.getByRole('button', { name: 'Registrar desperdicio' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Producto').selectOption({ label: product });
  await dialog.getByLabel(/^Cantidad desperdiciada/).fill('1');
  await dialog.getByRole('button', { name: 'Registrar desperdicio' }).click();
  await expect(row(page, product)).toBeVisible();

  await go(page, '/dashboard', 'Resumen general');
  await expect(page.getByRole('heading', { name: 'Inventario con riesgo' })).toBeVisible();

  await go(page, '/orders', 'Pedidos Mesa');
  const cancelledTable = unique('Mesa cancelada');
  await page.getByRole('button', { name: 'Nueva orden' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByLabel('Mesa').fill(cancelledTable);
  await dialog.getByLabel('Plato').selectOption({ label: recipe });
  await dialog.getByRole('button', { name: 'Crear orden' }).click();
  await row(page, cancelledTable).getByRole('button', { name: 'Cancelar' }).click();
  await expect(row(page, cancelledTable)).toContainText('Cancelado');

  await go(page, '/recipes', 'Recetas');
  page.once('dialog', (confirm) => confirm.accept());
  await row(page, recipe).getByRole('button', { name: 'Eliminar' }).click();
  await expect(row(page, recipe)).toContainText('Inactivo');

  await go(page, '/products', 'Productos e insumos');
  page.once('dialog', (confirm) => confirm.accept());
  await row(page, product).getByRole('button', { name: 'Eliminar' }).click();
  await expect(row(page, product)).toContainText('Inactivo');

  await go(page, '/suppliers', 'Proveedores');
  await row(page, supplier).getByRole('button', { name: 'Inactivar' }).click();
  await expect(row(page, supplier)).toContainText('Inactivo');
  await row(page, supplier).getByRole('button', { name: 'Activar' }).click();
  await expect(row(page, supplier)).toContainText('Activo');
  page.once('dialog', (confirm) => confirm.accept());
  await row(page, supplier).getByRole('button', { name: 'Eliminar' }).click();
  await expect(row(page, supplier)).toContainText('Inactivo');
});

test('formularios rechazan datos inválidos antes de enviarlos', async ({ page }) => {
  await login(page);
  await go(page, '/products', 'Productos e insumos');
  await page.getByRole('button', { name: 'Nuevo producto' }).click();
  let dialog = page.getByRole('dialog');
  await dialog.getByLabel('Nombre').fill('Producto inválido E2E');
  await dialog.getByLabel('Precio').fill('0');
  await dialog.getByRole('button', { name: 'Guardar producto' }).click();
  await expect(dialog.getByText('El precio debe ser mayor a cero')).toBeVisible();
  await dialog.getByRole('button', { name: 'Cancelar' }).click();

  await go(page, '/orders', 'Pedidos Mesa');
  await page.getByRole('button', { name: 'Nueva orden' }).click();
  dialog = page.getByRole('dialog');
  await dialog.getByRole('button', { name: 'Crear orden' }).click();
  await expect(dialog.getByText('Campo obligatorio').first()).toBeVisible();
  await dialog.getByRole('button', { name: 'Cancelar' }).click();
});

test('alertas: generación, filtros y lectura', async ({ page }) => {
  await login(page);
  await go(page, '/alerts', 'Alertas');
  await page.getByRole('button', { name: 'Generar stock' }).click();
  await expect(page.getByText('Alertas generadas correctamente')).toBeVisible();
  await page.getByRole('button', { name: 'Generar vencimiento' }).click();
  await expect(page.getByText('Alertas generadas correctamente')).toBeVisible();
  await page.locator('.search-input').selectOption('UNREAD');
  await page.getByRole('button', { name: 'Marcar todas' }).click();
  await expect(page.getByText('Alertas marcadas como leídas')).toBeVisible();
  await page.locator('.search-input').selectOption('READ');
});

test('reportes: generar, descargar y eliminar un reporte', async ({ page }) => {
  await login(page);
  await go(page, '/reports', 'Reportes');
  const today = new Date().toISOString().slice(0, 10);
  const before = await page.locator('tbody tr').count();
  await page.getByLabel('Tipo').selectOption('inventory');
  await page.getByLabel('Desde').fill(today);
  await page.getByLabel('Hasta').fill(today);
  const [generated] = await Promise.all([
    page.waitForResponse((response) => response.url().includes('/reports/generate/inventory') && response.request().method() === 'POST'),
    page.getByRole('button', { name: 'Generar', exact: true }).click()
  ]);
  expect(generated.ok(), await generated.text()).toBeTruthy();
  const reportTitle = (await generated.json()).data.title;
  await expect(page.getByText('Reporte generado correctamente')).toBeVisible();
  await expect(page.locator('tbody tr')).toHaveCount(before + 1);
  const report = row(page, reportTitle).first();
  const [pdf] = await Promise.all([page.waitForEvent('download'), report.getByRole('button', { name: 'PDF' }).click()]);
  expect(pdf.suggestedFilename()).toMatch(/\.pdf$/);
  const [excel] = await Promise.all([page.waitForEvent('download'), report.getByRole('button', { name: 'Excel' }).click()]);
  expect(excel.suggestedFilename()).toMatch(/\.xlsx$/);
  page.once('dialog', (dialog) => dialog.accept());
  await report.getByRole('button', { name: 'Eliminar' }).click();
  await expect(page.locator('tbody tr')).toHaveCount(before);
});

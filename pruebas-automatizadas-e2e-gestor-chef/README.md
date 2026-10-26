# Pruebas E2E de Gestor Chef

Suite Playwright para los flujos públicos y los 12 módulos protegidos del frontend. Usa Chromium y un solo trabajador para evitar conflictos entre operaciones sobre la misma base de datos.

## Preparación

1. En una instalación de Docker dedicada a pruebas, define `MONGODB_DATABASE=gestor_chef_e2e_db` y levanta la aplicación desde la raíz de GestorChefPlatform con `docker compose up --build -d`. En PowerShell: `$env:MONGODB_DATABASE='gestor_chef_e2e_db'; docker compose up --build -d`. El frontend queda en `http://localhost:3000` y el backend en `http://localhost:8080/api`.
2. En esta carpeta ejecuta `npm ci` y `npx playwright install chromium`.
3. Ejecuta `npm test`. Para ver el navegador, usa `npm run test:headed`.

Variables opcionales: `E2E_BASE_URL`, `E2E_API_URL`, `E2E_ADMIN_EMAIL`, `E2E_ADMIN_PASSWORD`, `E2E_COCINA_EMAIL`, `E2E_COCINA_PASSWORD`, `E2E_CONTABLE_EMAIL`, `E2E_CONTABLE_PASSWORD`.

La suite espera los tres usuarios semilla definidos en `DataSeeder.java`. Crea proveedores, productos, recetas, pedidos, movimientos, mermas, alertas y un usuario de prueba; las operaciones de borrado del sistema son lógicas en varios módulos. Por eso debe correr sobre una base aislada que pueda reiniciarse. El reporte creado se elimina al final de su prueba.

## Cobertura

- Inicio público, acceso protegido, login correcto e incorrecto, persistencia y cierre de sesión.
- Carga de todos los módulos del administrador y restricciones de cocina y contabilidad.
- Alta, edición, cambio de estado y baja lógica de proveedores y productos; búsqueda, categorías y validación de formularios.
- Sincronización y movimiento de inventario; alta y cálculo de costo de receta.
- Pedido de mesa, procesamiento, finalización y cancelación; registro de desperdicio; dashboard.
- Generación y lectura de alertas.
- Generación, descarga PDF/Excel y eliminación de reporte.
- Edición, activación, inactivación y eliminación lógica de un usuario creado para la prueba.

Los resultados HTML quedan en `playwright-report/`. Las trazas, capturas y videos de fallos quedan en `test-results/`.

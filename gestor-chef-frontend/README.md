# Gestor Chef Frontend

Frontend en React para el sistema Gestor Chef. La solución consume el backend Spring Boot bajo el contexto `/api`, mantiene la estética visual de las plantillas originales y organiza el código por responsabilidades.

## Stack

- React
- Vite
- React Router
- Axios
- CSS global modularizado por clases reutilizables
- Hooks de React para estado local, consumo de recursos y acciones asíncronas

## Requisitos

- Node.js 18 o superior
- Backend Gestor Chef ejecutándose en `http://localhost:8080/api`

## Instalación

```bash
npm install
```

## Configuración

Copia el archivo de ejemplo:

```bash
cp .env.example .env
```

Configura la URL del backend:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Ejecución

```bash
npm run dev
```

La aplicación quedará disponible normalmente en:

```text
http://localhost:5173
```

## Usuario demo esperado

El backend incluye usuarios semilla. Para validar el flujo completo se puede usar:

```text
Email: admin@gestor.chef
Contraseña: Admin123!
```

## Arquitectura aplicada

La solución separa responsabilidades de la siguiente manera:

```text
src/
 ├── assets/
 ├── components/
 │   ├── common/
 │   ├── forms/
 │   ├── layout/
 │   └── tables/
 ├── context/
 ├── hooks/
 ├── pages/
 ├── routes/
 ├── services/
 ├── styles/
 └── utils/
```

### Capas principales

- `services`: centraliza el consumo de APIs REST con Axios.
- `context`: administra sesión, usuario autenticado y cierre de sesión.
- `routes`: define navegación protegida y reglas por rol.
- `pages`: contiene vistas funcionales por módulo.
- `components`: contiene piezas reutilizables como tablas, modales, formularios, KPIs y mensajes.
- `utils`: contiene constantes, formateadores, validaciones y reglas de autorización frontend.

## Módulos implementados

- Inicio público
- Inicio de sesión
- Dashboard
- Inventario
- Movimientos de inventario
- Productos e insumos
- Categorías de producto
- Recetas
- Pedidos de mesa
- Desperdicios
- Alertas
- Proveedores
- Reportes
- Usuarios
- Roles informativos

## Roles soportados

El backend actual maneja estos roles:

- `ADMIN`
- `COCINA`
- `CONTABLE`

La vista de roles es informativa porque el backend tiene reglas de seguridad para `/roles/**`, pero no expone un controlador funcional de roles en el código revisado.

## Endpoints principales consumidos

- `/auth/login`
- `/dashboard`
- `/inventory`
- `/inventory/kpis`
- `/inventory/sync`
- `/inventory-movements`
- `/products`
- `/products/low-stock`
- `/suppliers`
- `/users`
- `/alerts`
- `/recipes`
- `/orders`
- `/reports`
- `/wastes`

## Criterios técnicos aplicados

- Configuración centralizada de API mediante `VITE_API_BASE_URL`.
- Interceptor HTTP para enviar token JWT automáticamente.
- Manejo centralizado de errores del backend.
- Rutas protegidas por autenticación y rol.
- Formularios con validaciones básicas.
- Tablas reutilizables con estados de carga y vacío.
- Modales reutilizables para creación y edición.
- Componentes de KPI reutilizables para tableros.
- Diseño responsive básico.

## Archivos principales creados

- `src/services/api.js`
- `src/context/AuthContext.jsx`
- `src/routes/AppRoutes.jsx`
- `src/routes/ProtectedRoute.jsx`
- `src/components/layout/MainLayout.jsx`
- `src/components/common/*`
- `src/components/forms/*`
- `src/components/tables/DataTable.jsx`
- `src/pages/*`
- `src/utils/*`
- `src/styles/global.css`

## Recomendaciones futuras

- Agregar pruebas unitarias de componentes críticos.
- Agregar integración con WebSocket para alertas en tiempo real.
- Crear formulario de creación administrativa de usuarios si el backend expone un endpoint específico.
- Implementar paginación visual si el volumen de registros crece.
- Agregar confirmaciones con un componente visual en lugar de `window.confirm`.
- Agregar manejo de refresh token si el backend amplía el contrato de autenticación.

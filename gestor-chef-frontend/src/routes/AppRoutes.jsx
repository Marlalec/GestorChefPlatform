import { Navigate, Route, Routes } from 'react-router-dom';
import { MainLayout } from '../components/layout/MainLayout';
import { AlertsPage } from '../pages/alerts/AlertsPage';
import { LoginPage } from '../pages/auth/LoginPage';
import { CategoriesPage } from '../pages/categories/CategoriesPage';
import { DashboardPage } from '../pages/dashboard/DashboardPage';
import { InventoryPage } from '../pages/inventory/InventoryPage';
import { OrdersPage } from '../pages/orders/OrdersPage';
import { ProductsPage } from '../pages/products/ProductsPage';
import { LandingPage } from '../pages/public/LandingPage';
import { RecipesPage } from '../pages/recipes/RecipesPage';
import { ReportsPage } from '../pages/reports/ReportsPage';
import { RolesPage } from '../pages/roles/RolesPage';
import { SuppliersPage } from '../pages/suppliers/SuppliersPage';
import { UsersPage } from '../pages/users/UsersPage';
import { WastesPage } from '../pages/wastes/WastesPage';
import { ProtectedRoute } from './ProtectedRoute';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/inventory" element={<InventoryPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/categories" element={<CategoriesPage />} />
          <Route path="/recipes" element={<RecipesPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/wastes" element={<WastesPage />} />
          <Route path="/alerts" element={<AlertsPage />} />
          <Route path="/suppliers" element={<SuppliersPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/users" element={<UsersPage />} />
          <Route path="/roles" element={<RolesPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

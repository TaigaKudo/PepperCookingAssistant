import {Routes, Route } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'
import StockCreatePage from './pages/StockCreatePage'
import StockEditPage from './pages/StockEditPage'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import SettingPage from './pages/SettingPage'

function App() {
  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginPage />}
      />
      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route
          path="/"
          element={
            <HomePage />
          }
        />
        <Route
          path="/settings"
          element={
            <SettingPage />
          }
        />
        <Route
          path="/stocks"
          element={
            <StockListPage />
          }
        />
        <Route
          path="/stocks/new"
          element={
            <StockCreatePage />
          }
        />
        <Route
          path="/stocks/:id/edit"
          element={
            <StockEditPage />
          }
        />
      </Route>
    </Routes>
  )
}

export default App
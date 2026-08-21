import {Routes, Route } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'
import StockCreatePage from './pages/StockCreatePage'
import StockEditPage from './pages/StockEditPage'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import RegisterPage from './pages/RegisterPage'

function App() {
  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginPage />}
      />
      <Route
        path="/register"
        element={<RegisterPage />}
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
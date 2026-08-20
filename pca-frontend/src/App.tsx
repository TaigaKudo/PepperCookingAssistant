import {Routes, Route } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'
import StockCreatePage from './pages/StockCreatePage'
import StockEditPage from './pages/StockEditPage'
import Layout from './components/Layout'

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
          path="/stocks"
          element={
            <ProtectedRoute>
              <StockListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/stocks/new"
          element={
            <ProtectedRoute>
              <StockCreatePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/stocks/:id/edit"
          element={
            <ProtectedRoute>
              <StockEditPage />
            </ProtectedRoute>
          }
        />
      </Route>
    </Routes>
  )
}

export default App
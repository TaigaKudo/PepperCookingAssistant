import {Routes, Route } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'
import StockCreatePage from './pages/StockCreatePage'
import StockEditPage from './pages/StockEditPage'

function App() {
  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginPage />}
      />
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
    </Routes>
  )
}

export default App
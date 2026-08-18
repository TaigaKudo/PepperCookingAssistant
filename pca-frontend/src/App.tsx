import {Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'
import StockCreatePage from './pages/StockCreatePage'
import ProtectedRoute from './components/ProtectedRoute'

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
    </Routes>
  )
}

export default App
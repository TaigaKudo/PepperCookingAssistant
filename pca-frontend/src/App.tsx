import {Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'
import StockCreatePage from './pages/StockCreatePage'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/stocks" element={<StockListPage />} />
      <Route path="/stocks/new" element={<StockCreatePage />} />
    </Routes>
  )
}

export default App
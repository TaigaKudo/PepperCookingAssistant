import {Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import StockListPage from './pages/StockListPage'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/stocks" element={<StockListPage />} />
    </Routes>
  )
}

export default App
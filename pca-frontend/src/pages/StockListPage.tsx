import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { getStocks } from '../api/stockApi'
import type { Stock } from '../types/stock'
import { Link } from 'react-router-dom'

function StockListPage() {
    const { accessToken } = useAuth()
    const [stocks, setStocks] = useState<Stock[]>([])

    useEffect(() => {
        if (!accessToken) {
            return
        }

        const fetchStocks = async () => {
            const fetchedStocks = await getStocks(accessToken)
            setStocks(fetchedStocks)
        }

        fetchStocks()
    }, [accessToken])

    return (
        <main>
            <h1>在庫一覧</h1>

            {stocks.length === 0 ? (
                <p>在庫がありません</p>
            ) : (
                <ul>
                    {stocks.map((stock) => (
                        <li key={stock.stockId}>
                            {stock.ingredientId}
                            {' '}
                            {stock.quantity}
                            {stock.defaultUnit}
                            {' '}
                            期限：{stock.expirationDate}
                        </li>
                    ))}
                </ul>
            )}

            <Link to="/stocks/new">在庫を登録する</Link>
        </main>
    )
}

export default StockListPage
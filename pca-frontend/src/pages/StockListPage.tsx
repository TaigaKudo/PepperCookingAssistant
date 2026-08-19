import { useEffect, useState } from 'react'
import { useAuth} from '../context/AuthContext'
import { getStocks, deleteStock } from '../api/stockApi'
import type { Stock } from '../types/stock'
import { Link, useNavigate } from 'react-router-dom'
import { logout } from '../api/authApi'

function StockListPage() {
    const { accessToken, setAccessToken, isLoading } = useAuth()
    const [stocks, setStocks] = useState<Stock[]>([])
    const navigate = useNavigate()

    const handleDelete = async (stockId: number) => {
        if(!accessToken){
            return
        }

        await deleteStock(accessToken, stockId)

        setStocks((currentStocks) =>
            currentStocks.filter((stock) => stock.stockId !== stockId)
        )
    }

    const handleLogout = async () => {
        await logout()

        setAccessToken(null)

        navigate('/login')
    }

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

    if(isLoading){
        return <p>読み込み中...</p>
    }

    return (
        <main>
            <button onClick={handleLogout}>
                ログアウト
            </button>
            <h1>在庫一覧</h1>

            {stocks.length === 0 ? (
                <p>在庫がありません</p>
            ) : (
                <ul>
                    {stocks.map((stock) => (
                        <li key={stock.stockId}>
                            {stock.ingredientName}
                            {' '}
                            {stock.quantity}
                            {stock.defaultUnit}
                            {' '}
                            期限：{stock.expirationDate}

                            {' '}

                            <Link to={`/stocks/${stock.stockId}/edit`}>
                                編集
                            </Link>
                            <button onClick={() => handleDelete(stock.stockId)}>
                                削除
                            </button>
                        </li>
                    ))}
                </ul>
            )}

            <Link to="/stocks/new">在庫を登録する</Link>
        </main>
    )
}

export default StockListPage
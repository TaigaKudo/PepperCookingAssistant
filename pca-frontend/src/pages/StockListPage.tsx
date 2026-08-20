import { useEffect, useState } from 'react'
import { useAuth} from '../context/AuthContext'
import { getStocks, deleteStock } from '../api/stockApi'
import type { Stock } from '../types/stock'
import { Link, useNavigate } from 'react-router-dom'

function StockListPage() {
    const {
        accessToken,
        isLoading,
        authFetch
    } = useAuth()
    const [stocks, setStocks] = useState<Stock[]>([])
    const [isFetching, setIsFetching] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const handleDelete = async (stockId: number) => {
        if(!accessToken){
            return
        }

        await deleteStock(authFetch, stockId)

        setStocks((currentStocks) =>
            currentStocks.filter((stock) => stock.stockId !== stockId)
        )
    }

    useEffect(() => {
        if (!accessToken) {
            return
        }

        const fetchStocks = async () => {
            try{
                setIsFetching(true)
                setError(null)

                const fetchedStocks = await getStocks(authFetch)
                setStocks(fetchedStocks)
            } catch {
                setError('在庫一覧の取得に失敗しました')
            } finally {
                setIsFetching(false)
            }
        }

        fetchStocks()
    }, [accessToken, authFetch])

    if(isLoading){
        return <p>認証情報を確認中...</p>
    }

    if(isFetching){
        return <p>在庫一覧を読み込み中...</p>
    }

    if(error){
        return <p>{error}</p>
    }

    return (
        <main>
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
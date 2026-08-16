import { useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { getStocks } from '../api/stockApi'

function StockListPage() {
    const { accessToken } = useAuth()

    useEffect(() => {
        if (!accessToken) {
            return
        }

        const fetchStocks = async () => {
            const stocks = await getStocks(accessToken)
            console.log(stocks)
        }

        fetchStocks()
    }, [accessToken])

    return (
        <main>
            <h1>在庫一覧</h1>
        </main>
    )
}

export default StockListPage
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getStock, updateStock } from '../api/stockApi'
import type { Stock } from '../types/stock'
import type { Ingredient } from '../types/ingredient'
import { getIngredients } from '../api/ingredientApi'

function StockEditPage() {
    const { id } = useParams()
    const { accessToken, authFetch } = useAuth()
    const navigate = useNavigate()

    const [stock, setStock] = useState<Stock | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [notFound, setNotFound] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [ingredients, setIngredients] = useState<Ingredient[]>([])

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault()

        if(!accessToken
            || !id
            || !stock
        ){
            return
        }

        try{
            setError(null)
            
            await updateStock(
                authFetch,
                stock.stockId,
                {
                    ingredientId: stock.ingredientId,
                    quantity: stock.quantity,
                    expirationDate: stock.expirationDate
                }
            )

            navigate('/stocks')
        } catch {
            setError('在庫更新に失敗しました')
        }

    }

    useEffect(() => {
        if(!accessToken || !id){
            return 
        }
        const fetchStock = async () => {
            try{
                setIsLoading(true)
                setNotFound(false)
                setError(null)

                const fetchedStock = await getStock(
                authFetch,
                    Number(id)
                )

                if(!fetchedStock){
                    setNotFound(true)
                        return
                }

                setStock(fetchedStock)
            } catch {
                setError('在庫情報の取得に失敗しました')
            } finally {
                setIsLoading(false)
            }
        }

        const fetchIngredients = async () => {
            const fetchedIngredients = await getIngredients(
                authFetch
            )

            setIngredients(fetchedIngredients)
        }

        fetchStock()
        fetchIngredients()
    }, [accessToken, id])

    if(isLoading){
        return <p>読み込み中...</p>
    }

    if(notFound){
        return <p>在庫が見つかりません</p>
    }

    if(error){
        return <p>{error}</p>
    }

    if(!stock){
        return <p>在庫情報を表示できません</p>
    }
    
    return (
        <main>
            <h1>在庫編集</h1>

            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="ingredientName">食材</label>
                    <select
                        id="ingredientName"
                        value={stock.ingredientId}
                        onChange={(e) => setStock({
                            ...stock,
                            ingredientId: Number(e.target.value)
                        })}
                    >
                        {ingredients.map((ingredient) => (
                            <option
                                key={ingredient.id}
                                value={ingredient.id}>
                                {ingredient.name}
                            </option>
                        ))}
                    </select>
                </div>
                <div>
                    <label htmlFor="ingredientQuantity">数量</label>
                    <input
                        id="ingredientQuantity"
                        type="number"
                        value={stock.quantity}
                        onChange={(e) => setStock({
                            ...stock,
                            quantity: Number(e.target.value)
                        })}
                    />
                </div>
                <div>
                    <label htmlFor="expirationDate">期限</label>
                    <input
                        type="date"
                        id="expirationDate"
                        value={stock.expirationDate}
                        onChange={(e) => setStock({
                            ...stock,
                            expirationDate: e.target.value
                        })}
                    />
                </div>
                <button type="submit">
                    更新
                </button>
            </form>
        </main>
    )
}

export default StockEditPage
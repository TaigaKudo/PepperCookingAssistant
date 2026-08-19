import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { createStock } from '../api/stockApi'
import { useAuth } from '../context/AuthContext'
import { getIngredients } from '../api/ingredientApi'
import type { Ingredient } from '../types/ingredient'

function StockCreatePage(){
    const [ingredientId, setIngredientId] = useState('')
    const [quantity, setQuantity] = useState('')
    const [expirationDate, setExpirationDate] = useState('')
    const { accessToken } = useAuth()
    const [ingredients, setIngredients] = useState<Ingredient[]>([])
    const navigate = useNavigate()

    const handleSubmit = async (
        e: React.SubmitEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        if(!accessToken){
            return 
        }

        await createStock(
            accessToken,
            Number(ingredientId),
            Number(quantity),
            expirationDate
        )

        navigate('/stocks')
    }

    useEffect(() => {
        if(!accessToken){
            return
        }

        const fetchIngredients = async () => {
            const fetchedIngredients = await getIngredients(accessToken)
            console.log(fetchedIngredients)
            setIngredients(fetchedIngredients)
        }

        fetchIngredients()
    }, [accessToken])

    return (
        <main>
            <h1>在庫登録</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="ingredientId">食材</label>
                    <select
                        id="ingredientId"
                        value={ingredientId}
                        onChange={(e) => setIngredientId(e.target.value)}
                        >

                        <option value="">食材を選択してください</option>

                        {ingredients.map((ingredient) => (
                            <option
                                key={ingredient.id}
                                value={ingredient.id}
                                >
                                    {ingredient.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label htmlFor="quantity">数量</label>
                    <input
                        id="quantity"
                        type="number"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="expirationDate">期限</label>
                    <input
                        id="expirationDate"
                        type="date"
                        value={expirationDate}
                        onChange={(e) => setExpirationDate(e.target.value)}
                        />
                </div>

                <button type="submit">
                    登録
                </button>
            </form>
        </main>
    )
}

export default StockCreatePage
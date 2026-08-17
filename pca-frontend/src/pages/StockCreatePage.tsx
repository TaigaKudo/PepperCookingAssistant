import { useState } from 'react'

function StockCreatePage(){
    const [ingredientId, setIngredientId] = useState('')
    const [quantity, setQuantity] = useState('')
    const [expirationDate, setExpirationDate] = useState('')

    const handleSubmit = async (
        e: React.SubmitEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        console.log({
            ingredientId,
            quantity,
            expirationDate
        })
    }

    return (
        <main>
            <h1>在庫登録</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="ingredientId">食材ID</label>
                    <input
                        id="ingredientId"
                        type="number"
                        value={ingredientId}
                        onChange={(e) => setIngredientId(e.target.value)}
                    />
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
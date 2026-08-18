import type { Stock } from '../types/stock'
import { getCsrfToken } from './csrf'

export async function getStocks(
    accessToken: string
):Promise<Stock[]> {
    const response = await fetch('http://localhost:8080/stocks', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
        },
    })

    if(!response.ok){
        throw new Error('在庫一覧の取得に失敗しました')
    }

    return await response.json()
}

export async function createStock(
    accessToken: string,
    ingredientId: number,
    quantity: number,
    expirationDate: string
){
    const csrfToken = await getCsrfToken()

    const response = await fetch('http://localhost:8080/stocks', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': `application/json`,
            'X-XSRF-TOKEN': csrfToken,
        },
        body: JSON.stringify({
            ingredientId,
            quantity,
            expirationDate,
        }),
    })
    if(!response.ok){
        throw new Error('在庫登録に失敗しました')
    }

    return await response.json()
}
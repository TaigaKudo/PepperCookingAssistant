import type { Stock } from '../types/stock'
import { getCsrfToken } from './csrf'
import type { StockUpdateRequest } from '../types/stockUpdateRequest'
import StockEditPage from '../pages/StockEditPage'

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

export async function getStock(
    accessToken: string,
    stockId: number
): Promise<Stock> {
    const response = await fetch(
        `http://localhost:8080/stocks/${stockId}`,
        {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
            },
        }
    )

    if(!response.ok){
        throw new Error('在庫情報の取得に失敗しました')
    }

    return await response.json()
}

export async function updateStock(
    accessToken: string,
    stockId: number,
    request: StockUpdateRequest
){
    const csrfToken = await getCsrfToken()

    const response = await fetch(
        `http://localhost:8080/stocks/${stockId}`,
        {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': csrfToken,
            },
            body: JSON.stringify(request),
        }
    )

    if(!response.ok){
        throw new Error('在庫更新に失敗しました')
    }

    return await response.json()
}
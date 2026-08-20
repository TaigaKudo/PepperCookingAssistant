import type { Stock } from '../types/stock'
import { getCsrfToken } from './csrf'
import type { StockUpdateRequest } from '../types/stockUpdateRequest'
import type { AuthFetch } from '../types/auth'

export async function getStocks(
    authFetch: AuthFetch
):Promise<Stock[]> {
    const response = await authFetch('http://localhost:8080/stocks')

    if(!response.ok){
        throw new Error('在庫一覧の取得に失敗しました')
    }

    return await response.json()
}

export async function createStock(
    authFetch: AuthFetch,
    ingredientId: number,
    quantity: number,
    expirationDate: string
): Promise<void>{
    const csrfToken = await getCsrfToken()

    const response = await authFetch('http://localhost:8080/stocks', {
        method: 'POST',
        credentials: 'include',
        headers: {
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
}

export async function getStock(
    authFetch: AuthFetch,
    stockId: number
): Promise<Stock | null> {
    const response = await authFetch(
        `http://localhost:8080/stocks/${stockId}`,
        {
            method: 'GET',
        }
    )

    if(response.status === 404){
        return null
    }
    
    if(!response.ok){
        throw new Error('在庫情報の取得に失敗しました')
    }

    return await response.json()
}

export async function updateStock(
    authFetch: AuthFetch,
    stockId: number,
    request: StockUpdateRequest
){
    const csrfToken = await getCsrfToken()

    const response = await authFetch(
        `http://localhost:8080/stocks/${stockId}`,
        {
            method: 'PUT',
            credentials: 'include',
            headers: {
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

export async function deleteStock(
    authFetch: AuthFetch,
    stockId: number
){
    const csrfToken = await getCsrfToken()

    const response = await authFetch(
        `http://localhost:8080/stocks/${stockId}`,
        {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': csrfToken,
            },
        }
    )
    if(!response.ok){
        throw new Error('在庫削除に失敗しました')
    }
}
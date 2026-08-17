import type { Stock } from '../types/stock'

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
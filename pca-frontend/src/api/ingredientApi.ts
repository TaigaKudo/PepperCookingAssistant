import type { Ingredient } from '../types/ingredient'

export async function getIngredients(
    accessToken: string
): Promise<Ingredient[]> {
    const response = await fetch('http://localhost:8080/ingredient', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
        },
    })

    if (!response.ok) {
        throw new Error('食材一覧の取得に失敗しました')
    }

    return await response.json()
}
import type { Ingredient } from '../types/ingredient'
import type { AuthFetch } from '../types/auth'

export async function getIngredients(
    authFetch: AuthFetch
): Promise<Ingredient[]> {
    const response = await authFetch('http://localhost:8080/ingredient', {
        method: 'GET',
    })

    if (!response.ok) {
        throw new Error('食材一覧の取得に失敗しました')
    }

    return await response.json()
}
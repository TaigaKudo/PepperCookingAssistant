import type { User } from '../types/user'
import type { AuthFetch } from '../types/auth'

export async function getMe(
    authFetch: AuthFetch
): Promise<User>{
    const response = await authFetch(
        'http://localhost:8080/users/me'
    )

    if(!response.ok){
        throw new Error('ユーザーの取得に失敗しました')
    }

    return await response.json()
}
import type { EmailChangeRequest, PasswordChangeRequest, User } from '../types/user'
import type { AuthFetch } from '../types/auth'
import { getCsrfToken } from './csrf'

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

export async function changeEmail(
    authFetch: AuthFetch,
    request: EmailChangeRequest
): Promise<void>{
    const csrfToken = await getCsrfToken()

    const response = await authFetch(
        'http://localhost:8080/users/me/email',{
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
        throw new Error('メールアドレスの変更に失敗しました')
    }
}

export async function changePassword(
    authFetch: AuthFetch,
    request: PasswordChangeRequest
): Promise<void>{
    const csrfToken = await getCsrfToken()

    const response = await authFetch(
        'http://localhost:8080/users/me/password',
        {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(request),
        }
    )

    if(!response.ok){
        throw new Error('パスワード変更に失敗しました')
    }
}

export async function deleteAccount(
    authFetch: AuthFetch
): Promise<void> {
    console.log('deleteAccount 開始')
    const csrfToken = await getCsrfToken()

    const response = await authFetch(
        'http://localhost:8080/users/me',
        {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': csrfToken
            },
        }
    )

    if(!response.ok){
        throw new Error('アカウント削除に失敗しました')
    }
}
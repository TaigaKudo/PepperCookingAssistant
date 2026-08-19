import { getCookie } from '../utils/cookie'
import { getCsrfToken } from './csrf'
import type { TokenResponse } from '../types/auth'

export async function login(
    email: string,
    password: string
): Promise<TokenResponse> {
    await fetch('http://localhost:8080/auth/csrf',{
        method: 'GET',
        credentials: 'include'
    })

    const csrfToken = getCookie('XSRF-TOKEN')

    const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrfToken ?? ''
        },
        body: JSON.stringify({
            email,
            password
        })
    })

    if(!response.ok){
        throw new Error('ログインに失敗しました')
    }

    return await response.json()
}
export async function refresh(): Promise<TokenResponse>{
    const csrfToken = await getCsrfToken()

    const response = await fetch('http://localhost:8080/auth/refresh', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'X-XSRF-TOKEN': csrfToken,
        },
    })

    if(!response.ok){
        throw new Error('トークンの更新に失敗しました')
    }

    return await response.json()
}
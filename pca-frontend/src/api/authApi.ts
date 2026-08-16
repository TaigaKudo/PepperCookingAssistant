import { getCookie } from '../utils/cookie'
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
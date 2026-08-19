import { getCookie } from '../utils/cookie'

export async function getCsrfToken(): Promise<string> {
    await fetch('http://localhost:8080/auth/csrf', {
        method: 'GET',
        credentials: 'include',
    })
    const csrfToken = getCookie('XSRF-TOKEN')

    if(!csrfToken){
        throw new Error('CSRFトークンを取得できませんでした')
    }

    return csrfToken
}
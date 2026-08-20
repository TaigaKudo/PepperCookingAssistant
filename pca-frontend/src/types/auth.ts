export type TokenResponse = {
    accessToken: string
}

export type AuthFetch = (
    url: string,
    options?: RequestInit
) => Promise<Response>
import {
    createContext,
    useContext,
    useEffect,
    useState,
    useCallback
} from 'react'
import type { ReactNode } from 'react'
import { refresh } from '../api/authApi'

type AuthContextType = {
    accessToken: string | null
    setAccessToken: (token: string | null) => void
    isLoading: boolean
    authFetch: (
        url: string,
        options?: RequestInit
    ) => Promise<Response>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [accessToken, setAccessToken] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const authFetch = useCallback(
            async(
            url: string,
            options: RequestInit = {}
        ): Promise<Response> => {
            const response = await fetch(url, {
                ...options,
                headers: {
                    ...options.headers,
                    'Authorization': `Bearer ${accessToken}`
                },
            })

            if(response.status !== 401){
                return response
            }
            try{
                const refreshResult = await refresh()

                setAccessToken(refreshResult.accessToken)

                return await fetch(url, {
                    ...options,
                    headers: {
                        ...options.headers,
                        'Authorization': `Bearer ${refreshResult.accessToken}`,
                    },
                })
            } catch {
                setAccessToken(null)

                throw new Error('認証の有効期限が切れました')
            }
        }, [accessToken]
    )

    useEffect(() => {
        const restoreAuth = async () => {
            try {
                const result = await refresh()

                setAccessToken(result.accessToken)
            } catch {
                setAccessToken(null)
            } finally {
                setIsLoading(false)
            }
        }

        restoreAuth()
    }, [])

    return (
        <AuthContext.Provider
            value={{
                accessToken,
                setAccessToken,
                isLoading,
                authFetch,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth(){
    const context = useContext(AuthContext)

    if(!context){
        throw new Error('useAuth must be used within AuthProvider')
    }

    return context
}
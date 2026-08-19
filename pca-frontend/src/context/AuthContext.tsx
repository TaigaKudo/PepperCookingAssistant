import {
    createContext,
    useContext,
    useEffect,
    useState
} from 'react'
import type { ReactNode } from 'react'
import { refresh } from '../api/authApi'

type AuthContextType = {
    accessToken: string | null
    setAccessToken: (token: string | null) => void
    isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [accessToken, setAccessToken] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(true)

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
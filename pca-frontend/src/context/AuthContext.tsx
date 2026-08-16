import { createContext, useContext, useState } from 'react'
import type { ReactNode } from 'react'

type AuthContextType = {
    accessToken: string | null
    setAccessToken: (token: string | null) => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode}){
    const [accessToken, setAccessToken] = useState<string | null>(null)

    return (
        <AuthContext.Provider value={{ accessToken, setAccessToken }}>
            { children }
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
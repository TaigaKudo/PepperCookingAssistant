import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { getMe } from '../api/userApi'
import type { User } from '../types/user'

function SettingPage(){
    const { authFetch } = useAuth()

    const [user, setUser] = useState<User | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const fetchUser = async () => {
            try{
                setIsLoading(true)
                setError(null)

                const fetchedUser = await getMe(authFetch)

                setUser(fetchedUser)
            } catch {
                setError('ユーザー情報の取得失敗しました')
            } finally {
                setIsLoading(false)
            }
        }

        fetchUser()
    }, [authFetch])

    if(isLoading){
        return <p>ユーザー情報を読み込み中...</p>
    }

    if(error){
        return <p>{error}</p>
    }

    return (
        <section>
            <h2>ユーザー設定</h2>

            <p>
                メールアドレス：{user?.email}
            </p>
        </section>
    )
}

export default SettingPage
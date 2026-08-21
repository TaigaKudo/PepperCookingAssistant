import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { changeEmail, changePassword, deleteAccount, getMe } from '../api/userApi'
import type { User } from '../types/user'
import { useNavigate } from 'react-router-dom'

function SettingPage(){
    const { authFetch, setAccessToken } = useAuth()

    const [user, setUser] = useState<User | null>(null)
    const [newEmail, setNewEmail] = useState('')
    const [emailCurrentPassword, setEmailCurrentPassword] = useState('')
    const [passwordCurrentPassword, setPasswordCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [message, setMessage] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()

    const handleEmailChange = async (
        e: React.SubmitEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        try{
            setError(null)
            setMessage(null)

            await changeEmail(authFetch, {
                newEmail,
                currentPassword: emailCurrentPassword
            })

            setMessage('メールアドレスを変更しました')
        } catch {
            setError('メールアドレスの変更に失敗しました')
        }
    }

    const handleDeleteAccount = async () => {
        const confirmed = window.confirm(
            '本当にアカウントを削除しますか？'
        )

        if(!confirmed){
            return
        }

        try{
            setError(null)

            await deleteAccount(authFetch)

            setAccessToken(null)

            navigate('/login')
        } catch {
            setError('アカウント削除に失敗しました')
        }
    }

    const handlePasswordChange = async (
        e: React.SubmitEvent<HTMLElement>
    ) => {
        e.preventDefault()

        try{
            setError(null)
            setMessage(null)

            await changePassword(authFetch, {
                currentPassword: passwordCurrentPassword,
                newPassword
            })

            setMessage('パスワードを変更しました')

            setPasswordCurrentPassword('')
            setNewPassword('')
        } catch {
            setError('パスワード変更に失敗しました')
        }
    }

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

            <form onSubmit={handleEmailChange}>
                <div>
                    <label htmlFor="email">
                        新しいメールアドレス：
                    </label>

                    <input
                        id="newEmail"
                        type="email"
                        value={newEmail}
                        onChange={(e) => setNewEmail(e.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="currentPassword">
                        現在のパスワード：
                    </label>
                    <input
                        id="currentPassword"
                        type="password"
                        value={emailCurrentPassword}
                        onChange={(e) => setEmailCurrentPassword(e.target.value)}
                    />
                </div>

                <button type="submit">
                    メールアドレスを変更
                </button>
            </form>

            <form onSubmit={handlePasswordChange}>
                <div>
                    <label htmlFor="currentPassword">
                        現在のパスワード：
                    </label>

                    <input
                        id="currentPassword"
                        type="password"
                        value={passwordCurrentPassword}
                        onChange={(e) => setPasswordCurrentPassword(e.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="newPassword">
                        新しいパスワード：
                    </label>

                    <input
                        id="newPassword"
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                    />
                </div>

                <button type="submit">
                    パスワードを変更
                </button>
            </form>

            {message && <p>{message}</p>}

            <form>
                <div>
                    <button
                    type="button"
                    onClick={handleDeleteAccount}
                    >
                        アカウントを削除
                    </button>
                </div>
            </form>
        </section>
    )
}

export default SettingPage
import { useState } from 'react'
import { login } from '../api/authApi'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

function LoginPage() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate()

    const { setAccessToken } = useAuth()

    const handleSubmit = async (
        e: React.SubmitEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        const result = await login(email, password)

        setAccessToken(result.accessToken)

        navigate('/stocks')
    }

    return (
        <main className="login-page">
            <section className="login-card">
                <h1 className="login-title">ログイン</h1>

                <form
                    className="login-form"
                    onSubmit={handleSubmit}
                    >
                    <div className="form-field">
                        <label htmlFor="email">メールアドレス</label>
                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="password">パスワード</label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    <button type="submit">
                        ログイン
                    </button>
                </form>
            </section>
        </main>
    )
}

export default LoginPage
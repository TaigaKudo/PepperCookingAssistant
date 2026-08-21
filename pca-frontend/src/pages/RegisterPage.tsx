import { useNavigate } from "react-router-dom"
import { useState } from "react"
import { register } from "../api/authApi"

function RegisterPage (){
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [passwordCheck, setPasswordCheck] = useState('')
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()

    const handleRegister = async (
        e: React.SubmitEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        if(password !== passwordCheck){
            setError('パスワードが一致しません')
            return
        }

        try{
            setError(null)
            await register(name, email, password)
            navigate('/login')
        } catch {
            setError('新規登録に失敗しました')
        }

    }

    return (
        <main>
            <h1>新規登録</h1>

            <form onSubmit={handleRegister}>
                {error && <p>{error}</p>}
                <div>
                    <label htmlFor="name">
                        ニックネーム
                    </label>
                    <input
                        id="name"
                        onChange={(e)=>setName(e.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="email">
                        メールアドレス
                    </label>
                    <input
                        id="email"
                        type="email"
                        onChange={(e)=>setEmail(e.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="password">
                        パスワード
                    </label>
                    <input
                        id="password"
                        type="password"
                        onChange={(e)=>setPassword(e.target.value)}
                    />
                </div>

                <div>
                    <label htmlFor="passwordCheck">
                        パスワードを再入力してください
                    </label>
                    <input
                        id="passwordCheck"
                        type="password"
                        onChange={(e)=>setPasswordCheck(e.target.value)}
                    />
                </div>

                <button type="submit">
                    登録する
                </button>

            </form>
        </main>
    )
}

export default RegisterPage
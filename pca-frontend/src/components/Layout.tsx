import { Outlet, Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { logout } from '../api/authApi'

function Layout() {
    const { setAccessToken } = useAuth()
    const navigate = useNavigate()
    const handleLogout = async () => {
        await logout()

        setAccessToken(null)

        navigate('/login')
    }

    return(
        <>
            <header>
                <h1>Peper Cooking Assistant</h1>

                <nav>
                    <Link to="/stocks">在庫</Link>
                </nav>

                <button onClick={handleLogout}>
                    ログアウト
                </button>
            </header>

            <main>
                <Outlet />
            </main>
        </>
    )
}

export default Layout
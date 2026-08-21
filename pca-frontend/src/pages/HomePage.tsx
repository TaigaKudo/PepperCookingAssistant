import { Link } from 'react-router-dom'

function HomePage(){
    return(
        <section>
            <h2>PCA ホーム</h2>

            <ul>
                <li>
                    <Link to="/stocks">
                        在庫管理                    
                    </Link>
                </li>

                <li>
                    レシピ
                </li>

                <li>
                    料理提案
                </li>

                <li>
                    <Link to="/settings">
                        ユーザー設定
                    </Link>
                </li>
            </ul>
        </section>
    )
}

export default HomePage
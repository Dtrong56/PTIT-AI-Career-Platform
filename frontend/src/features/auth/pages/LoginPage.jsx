
import { useState } from 'react'
import { login } from '../services/authService'

function LoginPage({ onLoginSuccess }) {
    const [studentId, setStudentId] = useState('')
    const [password, setPassword] = useState('')
    const [showPassword, setShowPassword] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    async function handleLogin(event) {
        event.preventDefault()
        setError('')

        if (!studentId.trim() || !password) {
            setError('Vui lòng nhập tài khoản và mật khẩu.')
            return
        }

        setLoading(true)

        try {
            const data = await login(studentId, password)

            if (!['STUDENT', 'FACULTY'].includes(data.role)) {
                setError('Tài khoản chưa được gán vai trò hợp lệ.')
                return
            }

            onLoginSuccess(data)
            setPassword('')
        } catch (err) {
            if (err.status === 401) {
                setError('Tài khoản hoặc mật khẩu không chính xác.')
            } else if (err.status === 403) {
                setError('Tài khoản không có quyền đăng nhập.')
            } else {
                setError(
                    err.message ||
                    'Không kết nối được máy chủ. Hãy kiểm tra API Gateway và Identity Service.'
                )
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="login-page">
            <section className="login-card">

                <div className="brand">
                    PTIT
                </div>

                <h1>Đăng nhập</h1>

                <p className="description">
                    Hệ thống hỗ trợ định hướng và phát triển nghề nghiệp
                    cho sinh viên.
                </p>

                <form onSubmit={handleLogin}>

                    <div className="form-group">
                        <label htmlFor="studentId">
                            MSSV hoặc tên đăng nhập
                        </label>

                        <input
                            id="studentId"
                            type="text"
                            autoComplete="username"
                            placeholder="Nhập MSSV hoặc tài khoản Khoa"
                            value={studentId}
                            onChange={(event) =>
                                setStudentId(event.target.value)
                            }
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">
                            Mật khẩu
                        </label>

                        <input
                            id="password"
                            type={showPassword ? 'text' : 'password'}
                            autoComplete="current-password"
                            placeholder="Nhập mật khẩu"
                            value={password}
                            onChange={(event) =>
                                setPassword(event.target.value)
                            }
                            required
                        />

                        <label className="show-password">
                            <input
                                type="checkbox"
                                checked={showPassword}
                                onChange={(event) =>
                                    setShowPassword(event.target.checked)
                                }
                            />
                            Hiển thị mật khẩu
                        </label>
                    </div>

                    {error && (
                        <p className="error-message" role="alert">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        className="login-button"
                        disabled={loading}
                    >
                        {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                    </button>

                </form>

                <p className="footer-text">
                    Học viện Công nghệ
                    Bưu chính Viễn thông
                </p>

            </section>
        </main>
    )
}

export default LoginPage

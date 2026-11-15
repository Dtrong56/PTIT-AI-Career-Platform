
import { useState } from 'react'
import { changePassword } from '../services/authService'

function ChangePasswordPage({
    accessToken,
    onSuccess,
    onLogout,
}) {
    const [currentPassword, setCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [showPassword, setShowPassword] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    async function handleSubmit(event) {
        event.preventDefault()
        setError('')

        if (!currentPassword || !newPassword || !confirmPassword) {
            setError('Vui lòng nhập đầy đủ thông tin.')
            return
        }

        if (newPassword !== confirmPassword) {
            setError('Mật khẩu xác nhận không khớp.')
            return
        }

        if (currentPassword === newPassword) {
            setError('Mật khẩu mới phải khác mật khẩu hiện tại.')
            return
        }

        setLoading(true)

        try {
            const data = await changePassword(
                accessToken,
                currentPassword,
                newPassword
            )

            onSuccess(data)
        } catch (err) {
            setError(err.message || 'Đổi mật khẩu thất bại.')
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

                <h1>Đổi mật khẩu</h1>

                <p className="description">
                    Đây là lần đăng nhập đầu tiên.
                    Vui lòng đổi mật khẩu để tiếp tục sử dụng hệ thống.
                </p>

                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label htmlFor="currentPassword">
                            Mật khẩu hiện tại
                        </label>

                        <input
                            id="currentPassword"
                            type={showPassword ? 'text' : 'password'}
                            autoComplete="current-password"
                            placeholder="Nhập mật khẩu hiện tại"
                            value={currentPassword}
                            onChange={(e) =>
                                setCurrentPassword(e.target.value)
                            }
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="newPassword">
                            Mật khẩu mới
                        </label>

                        <input
                            id="newPassword"
                            type={showPassword ? 'text' : 'password'}
                            autoComplete="new-password"
                            placeholder="Nhập mật khẩu mới"
                            value={newPassword}
                            onChange={(e) =>
                                setNewPassword(e.target.value)
                            }
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">
                            Xác nhận mật khẩu mới
                        </label>

                        <input
                            id="confirmPassword"
                            type={showPassword ? 'text' : 'password'}
                            autoComplete="new-password"
                            placeholder="Nhập lại mật khẩu mới"
                            value={confirmPassword}
                            onChange={(e) =>
                                setConfirmPassword(e.target.value)
                            }
                            required
                        />
                    </div>

                    <label className="show-password">
                        <input
                            type="checkbox"
                            checked={showPassword}
                            onChange={(e) =>
                                setShowPassword(e.target.checked)
                            }
                        />
                        Hiển thị mật khẩu
                    </label>

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
                        {loading
                            ? 'Đang cập nhật...'
                            : 'Xác nhận đổi mật khẩu'}
                    </button>

                </form>

                <button
                    type="button"
                    className="logout-button"
                    onClick={onLogout}
                >
                    Đăng xuất
                </button>

                <p className="footer-text">
                    PTIT
                </p>

            </section>
        </main>
    )
}

export default ChangePasswordPage

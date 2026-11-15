
import { useState } from 'react'
import { Link } from 'react-router-dom'
import {
    createStudentAccount,
    resetStudentPassword,
} from '../services/studentAccountService'
import '../../../styles/dashboard.css'

function FacultyStudentAccountsPage({ accessToken, onLogout }) {
    const [studentId, setStudentId] = useState('')
    const [fullName, setFullName] = useState('')
    const [email, setEmail] = useState('')

    const [resetStudentId, setResetStudentId] = useState('')

    const [creating, setCreating] = useState(false)
    const [resetting, setResetting] = useState(false)

    const [createMessage, setCreateMessage] = useState('')
    const [createError, setCreateError] = useState('')

    const [resetMessage, setResetMessage] = useState('')
    const [resetError, setResetError] = useState('')

    // Khoa tạo tài khoản sinh viên
    async function handleCreateStudent(event) {
        event.preventDefault()

        setCreateMessage('')
        setCreateError('')

        if (!studentId.trim() || !fullName.trim() || !email.trim()) {
            setCreateError('Vui lòng nhập đầy đủ thông tin sinh viên.')
            return
        }

        setCreating(true)

        try {
            await createStudentAccount(
                accessToken,
                studentId,
                fullName,
                email
            )

            setCreateMessage(
                `Đã tạo tài khoản cho sinh viên ${studentId.trim()} thành công.`
            )

            setStudentId('')
            setFullName('')
            setEmail('')
        } catch (error) {
            if (error.status === 401) {
                setCreateError('Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.')
            } else if (error.status === 403) {
                setCreateError('Tài khoản không có quyền tạo sinh viên.')
            } else {
                setCreateError(
                    error.message || 'Không thể tạo tài khoản sinh viên.'
                )
            }
        } finally {
            setCreating(false)
        }
    }

    // Khoa đặt lại mật khẩu sinh viên
    async function handleResetPassword(event) {
        event.preventDefault()

        setResetMessage('')
        setResetError('')

        const id = resetStudentId.trim()

        if (!id) {
            setResetError('Vui lòng nhập mã số sinh viên.')
            return
        }

        const confirmed = window.confirm(
            `Bạn có chắc muốn đặt lại mật khẩu cho sinh viên ${id}?`
        )

        if (!confirmed) return

        setResetting(true)

        try {
            await resetStudentPassword(accessToken, id)

            setResetMessage(
                `Đã đặt lại mật khẩu cho sinh viên ${id} thành công.`
            )

            setResetStudentId('')
        } catch (error) {
            if (error.status === 401) {
                setResetError('Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.')
            } else if (error.status === 403) {
                setResetError('Tài khoản không có quyền đặt lại mật khẩu.')
            } else {
                setResetError(
                    error.message || 'Không thể đặt lại mật khẩu.'
                )
            }
        } finally {
            setResetting(false)
        }
    }

    return (
        <main className="dashboard-shell">
            <header className="dashboard-header">
                <div>
                    <div className="dashboard-brand">
                        PTIT
                    </div>

                    <span className="dashboard-role">
                        Quản lý tài khoản sinh viên
                    </span>
                </div>

                <button
                    type="button"
                    className="dashboard-logout"
                    onClick={onLogout}
                >
                    Đăng xuất
                </button>
            </header>

            <section className="dashboard-content">

                <Link
                    to="/faculty/dashboard"
                    className="back-button"
                >
                    <span>←</span>
                    Quay lại Dashboard Khoa
                </Link>

                <h1>Quản lý tài khoản sinh viên</h1>

                <p className="dashboard-intro">
                    Khoa thực hiện tạo tài khoản mới và đặt lại
                    mật khẩu cho sinh viên.
                </p>

                <div className="dashboard-grid">

                    {/* TẠO TÀI KHOẢN */}
                    <article className="dashboard-card">
                        <h2>Tạo tài khoản sinh viên</h2>

                        <form onSubmit={handleCreateStudent}>

                            <div className="form-group">
                                <label htmlFor="create-student-id">
                                    Mã số sinh viên
                                </label>

                                <input
                                    id="create-student-id"
                                    type="text"
                                    placeholder="Nhập MSSV"
                                    value={studentId}
                                    onChange={(e) =>
                                        setStudentId(e.target.value)
                                    }
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="create-full-name">
                                    Họ và tên sinh viên
                                </label>

                                <input
                                    id="create-full-name"
                                    type="text"
                                    placeholder="Nhập họ và tên"
                                    value={fullName}
                                    onChange={(e) =>
                                        setFullName(e.target.value)
                                    }
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="create-email">
                                    Email
                                </label>

                                <input
                                    id="create-email"
                                    type="email"
                                    placeholder="Nhập email sinh viên"
                                    value={email}
                                    onChange={(e) =>
                                        setEmail(e.target.value)
                                    }
                                    required
                                />
                            </div>

                            {createError && (
                                <p className="error-message" role="alert">
                                    {createError}
                                </p>
                            )}

                            {createMessage && (
                                <p className="notice" role="status">
                                    {createMessage}
                                </p>
                            )}

                            <button
                                type="submit"
                                className="login-button"
                                disabled={creating}
                            >
                                {creating
                                    ? 'Đang tạo tài khoản...'
                                    : 'Tạo tài khoản'}
                            </button>

                        </form>
                    </article>

                    {/* ĐẶT LẠI MẬT KHẨU */}
                    <article className="dashboard-card">
                        <h2>Đặt lại mật khẩu sinh viên</h2>

                        <p>
                            Nhập mã số sinh viên cần đặt lại mật khẩu.
                            Sinh viên phải đổi mật khẩu khi đăng nhập lại.
                        </p>

                        <form onSubmit={handleResetPassword}>

                            <div className="form-group">
                                <label htmlFor="reset-student-id">
                                    Mã số sinh viên
                                </label>

                                <input
                                    id="reset-student-id"
                                    type="text"
                                    placeholder="Nhập MSSV cần đặt lại"
                                    value={resetStudentId}
                                    onChange={(e) =>
                                        setResetStudentId(e.target.value)
                                    }
                                    required
                                />
                            </div>

                            {resetError && (
                                <p className="error-message" role="alert">
                                    {resetError}
                                </p>
                            )}

                            {resetMessage && (
                                <p className="notice" role="status">
                                    {resetMessage}
                                </p>
                            )}

                            <button
                                type="submit"
                                className="login-button"
                                disabled={resetting}
                            >
                                {resetting
                                    ? 'Đang đặt lại mật khẩu...'
                                    : 'Đặt lại mật khẩu'}
                            </button>

                        </form>
                    </article>

                </div>
            </section>
        </main>
    )
}

export default FacultyStudentAccountsPage

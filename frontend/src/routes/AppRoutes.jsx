
import { useState } from 'react'
import {
    Navigate,
    Route,
    Routes,
    useNavigate,
} from 'react-router-dom'

import LoginPage from '../features/auth/pages/LoginPage'
import ChangePasswordPage from '../features/auth/pages/ChangePasswordPage'

import StudentDashboardPage from '../features/student/pages/StudentDashboardPage'
import FacultyDashboardPage from '../features/faculty/pages/FacultyDashboardPage'
import FacultyStudentAccountsPage from '../features/faculty/pages/FacultyStudentAccountsPage'

import ProtectedRoute, {
    getDashboardPath,
} from './ProtectedRoute'

// Key lưu phiên đăng nhập
const SESSION_STORAGE_KEY = 'ptit_session'

// Khôi phục phiên đăng nhập từ localStorage
function getSavedSession() {
    try {
        const savedData = localStorage.getItem(SESSION_STORAGE_KEY)

        if (!savedData) {
            return null
        }

        const savedSession = JSON.parse(savedData)

        // Kiểm tra dữ liệu phiên đăng nhập
        if (
            !savedSession?.accessToken ||
            !['STUDENT', 'FACULTY'].includes(savedSession.role)
        ) {
            localStorage.removeItem(SESSION_STORAGE_KEY)
            return null
        }

        return {
            accessToken: savedSession.accessToken,
            role: savedSession.role,
            mustChangePassword:
                savedSession.mustChangePassword === true,
        }
    } catch (error) {
        localStorage.removeItem(SESSION_STORAGE_KEY)
        return null
    }
}

function AppRoutes() {

    // Khôi phục session ngay khi ứng dụng khởi động
    const [session, setSession] = useState(() => getSavedSession())

    const navigate = useNavigate()

    const homePath = !session
        ? '/login'
        : session.mustChangePassword
            ? '/change-password'
            : getDashboardPath(session.role)

    // Xử lý sau khi đăng nhập thành công
    function handleLoginSuccess(data) {

        const newSession = {
            accessToken: data.accessToken,
            role: data.role,
            mustChangePassword: data.mustChangePassword,
        }

        // Lưu session vào localStorage
        localStorage.setItem(
            SESSION_STORAGE_KEY,
            JSON.stringify(newSession)
        )

        setSession(newSession)

        if (newSession.mustChangePassword) {
            navigate('/change-password', { replace: true })
        } else {
            navigate(getDashboardPath(newSession.role), {
                replace: true,
            })
        }
    }

    // Xử lý sau khi đổi mật khẩu thành công
    function handlePasswordChanged(data) {

        if (!session) {
            navigate('/login', { replace: true })
            return
        }

        const updatedSession = {
            ...session,
            accessToken: data?.accessToken || session.accessToken,
            role: data?.role || session.role,
            mustChangePassword:
                data?.mustChangePassword ?? false,
        }

        // Cập nhật session mới vào localStorage
        localStorage.setItem(
            SESSION_STORAGE_KEY,
            JSON.stringify(updatedSession)
        )

        setSession(updatedSession)

        if (updatedSession.mustChangePassword) {
            navigate('/change-password', { replace: true })
        } else {
            navigate(getDashboardPath(updatedSession.role), {
                replace: true,
            })
        }
    }

    // Đăng xuất
    function handleLogout() {

        // Xóa session đã lưu
        localStorage.removeItem(SESSION_STORAGE_KEY)

        setSession(null)

        navigate('/login', { replace: true })
    }

    return (
        <Routes>

            {/* Trang mặc định */}
            <Route
                path="/"
                element={<Navigate to={homePath} replace />}
            />

            {/* Trang đăng nhập */}
            <Route
                path="/login"
                element={
                    session ? (
                        <Navigate to={homePath} replace />
                    ) : (
                        <LoginPage
                            onLoginSuccess={handleLoginSuccess}
                        />
                    )
                }
            />

            {/* Trang đổi mật khẩu lần đầu */}
            <Route
                path="/change-password"
                element={
                    !session ? (
                        <Navigate to="/login" replace />
                    ) : !session.mustChangePassword ? (
                        <Navigate
                            to={getDashboardPath(session.role)}
                            replace
                        />
                    ) : (
                        <ChangePasswordPage
                            accessToken={session.accessToken}
                            onSuccess={handlePasswordChanged}
                            onLogout={handleLogout}
                        />
                    )
                }
            />

            {/* Dashboard Sinh viên */}
            <Route
                path="/student/dashboard"
                element={
                    <ProtectedRoute
                        session={session}
                        requiredRole="STUDENT"
                    >
                        <StudentDashboardPage
                            onLogout={handleLogout}
                        />
                    </ProtectedRoute>
                }
            />
            {/* TEST giao diện Dashboard Sinh viên */}
            <Route
                path="/test-student-dashboard"
                element={
                    <StudentDashboardPage
                        onLogout={() => {}}
                    />
                }
            />

            {/* Dashboard Khoa */}
            <Route
                path="/faculty/dashboard"
                element={
                    <ProtectedRoute
                        session={session}
                        requiredRole="FACULTY"
                    >
                        <FacultyDashboardPage
                            onLogout={handleLogout}
                        />
                    </ProtectedRoute>
                }
            />

            {/* Khoa quản lý tài khoản sinh viên */}
            <Route
                path="/faculty/students"
                element={
                    <ProtectedRoute
                        session={session}
                        requiredRole="FACULTY"
                    >
                        <FacultyStudentAccountsPage
                            accessToken={session?.accessToken}
                            onLogout={handleLogout}
                        />
                    </ProtectedRoute>
                }
            />

            {/* Đường dẫn không tồn tại */}
            <Route
                path="*"
                element={<Navigate to={homePath} replace />}
            />

        </Routes>
    )
}

export default AppRoutes

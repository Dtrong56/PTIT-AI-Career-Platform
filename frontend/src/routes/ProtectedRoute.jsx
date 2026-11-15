
import { Navigate } from 'react-router-dom'

export function getDashboardPath(role) {
    if (role === 'STUDENT') {
        return '/student/dashboard'
    }

    if (role === 'FACULTY') {
        return '/faculty/dashboard'
    }

    return '/login'
}

function ProtectedRoute({ session, requiredRole, children }) {
    if (!session) {
        return <Navigate to="/login" replace />
    }

    if (session.mustChangePassword) {
        return <Navigate to="/change-password" replace />
    }

    if (session.role !== requiredRole) {
        return (
            <Navigate
                to={getDashboardPath(session.role)}
                replace
            />
        )
    }

    return children
}

export default ProtectedRoute

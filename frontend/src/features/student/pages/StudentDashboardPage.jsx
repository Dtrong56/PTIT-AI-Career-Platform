import { useState } from 'react'
import '../../../styles/dashboard.css'
import CVJDAnalysisPage from './CVJDAnalysisPage'

const menuItems = [
    { id: 'overview', label: 'Tổng quan' },
    { id: 'cv', label: 'Quản lý CV' },
    { id: 'jd', label: 'Quản lý JD' },
    { id: 'analysis', label: 'Phân tích CV-JD' },
    { id: 'history', label: 'Lịch sử phân tích' },
]

function DashboardOverview() {
    return (
        <section className="dashboard-overview">
            <div className="dashboard-page-heading">
                <h1>Trang dành cho Sinh viên</h1>
                <p>
                    Tổng quan quá trình phân tích CV
                    và mức độ đáp ứng với các vị trí công việc.
                </p>
            </div>

            <section className="dashboard-stat-section">
                <h2>Tổng quan hoạt động</h2>

                <div className="dashboard-grid">
                    <article className="dashboard-card">
                        <h3>Số CV đã tải lên</h3>
                        <p>--</p>
                    </article>

                    <article className="dashboard-card">
                        <h3>Số JD đã tải lên</h3>
                        <p>--</p>
                    </article>

                    <article className="dashboard-card">
                        <h3>Lượt phân tích</h3>
                        <p>--</p>
                    </article>

                    <article className="dashboard-card">
                        <h3>Match Score trung bình</h3>
                        <p>--</p>
                    </article>
                </div>
            </section>
        </section>
    )
}

function StudentDashboardPage({ onLogout }) {
    const [activeMenu, setActiveMenu] = useState('overview')

    const activeItem = menuItems.find(
        (item) => item.id === activeMenu
    )

    const renderContent = () => {
        if (activeMenu === 'overview') {
            return <DashboardOverview />
        }

        if (activeMenu === 'analysis') {
            return <CVJDAnalysisPage />
        }

        return (
            <section className="dashboard-placeholder">
                <h1>{activeItem?.label}</h1>
                <p>
                    Chức năng này sẽ được triển khai ở bước tiếp theo.
                </p>
            </section>
        )
    }

    return (
        <div className="dashboard-shell">
            <aside className="dashboard-sidebar">
                <div className="dashboard-brand">
                    <div className="dashboard-brand-logo">PTIT</div>
                    <div>
                        <h2>PTIT</h2>
                        <span>Cổng thông tin sinh viên</span>
                    </div>
                </div>

                <div className="dashboard-menu-heading">
                    MENU
                </div>

                <nav className="dashboard-nav">
                    {menuItems.map((item) => (
                        <button
                            key={item.id}
                            type="button"
                            className={`dashboard-nav-item ${
                                activeMenu === item.id ? 'active' : ''
                            }`}
                            onClick={() => setActiveMenu(item.id)}
                        >
                            {item.label}
                        </button>
                    ))}
                </nav>

                <button
                    type="button"
                    className="dashboard-logout"
                    onClick={onLogout}
                >
                    Đăng xuất
                </button>
            </aside>

            <main className="dashboard-main">
                <header className="dashboard-topbar">
                    <span>{activeItem?.label}</span>
                    <span className="dashboard-user-role">
                        Sinh viên
                    </span>
                </header>

                <div className="dashboard-content">
                    {renderContent()}
                </div>
            </main>
        </div>
    )
}

export default StudentDashboardPage
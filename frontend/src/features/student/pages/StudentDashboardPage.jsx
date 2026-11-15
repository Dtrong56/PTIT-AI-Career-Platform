
import '../../../styles/dashboard.css'

function StudentDashboardPage({ onLogout }) {
    return (
        <main className="dashboard-shell">
            <header className="dashboard-header">
                <div>
                    <div className="dashboard-brand">
                        PTIT
                    </div>
                    <span className="dashboard-role">
                        Cổng thông tin sinh viên
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
                <h1>Trang dành cho Sinh Viên</h1>

                <p className="dashboard-intro">
                    Tổng quan quá trình phân tích CV
                    và mức độ đáp ứng với các vị trí công việc.
                </p>

                <div className="dashboard-grid">
                    <article className="dashboard-card">
                        <h2>Số CV đã tải lên</h2>
                        <p>Chưa kết nối dữ liệu thống kê.</p>
                    </article>

                    <article className="dashboard-card">
                        <h2>Số JD đã tải lên</h2>
                        <p>Chưa kết nối dữ liệu thống kê.</p>
                    </article>

                    <article className="dashboard-card">
                        <h2>Số lần phân tích CV–JD</h2>
                        <p>Chưa kết nối dữ liệu thống kê.</p>
                    </article>

                    <article className="dashboard-card">
                        <h2>Radar Match Score theo vị trí</h2>
                        <p>
                            Chưa có dữ liệu Match Score
                            để hiển thị biểu đồ.
                        </p>
                    </article>
                </div>
            </section>
        </main>
    )
}

export default StudentDashboardPage

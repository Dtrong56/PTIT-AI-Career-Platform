
import { Link } from 'react-router-dom'
import '../../../styles/dashboard.css'

function FacultyDashboardPage({ onLogout }) {
    return (
        <main className="dashboard-shell">

            <header className="dashboard-header">
                <div>
                    <div className="dashboard-brand">
                        PTIT
                    </div>

                    <span className="dashboard-role">
                        Cổng thông tin Khoa
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


                <h1>Trang dành cho Khoa</h1>

                <p className="dashboard-intro">
                    Quản lý tài khoản sinh viên và
                    theo dõi các thông tin thống kê
                    của hệ thống.
                </p>

                {/* CHỨC NĂNG IDENTITY SERVICE */}
                <div
                    className="dashboard-card"
                    style={{ marginBottom: '24px' }}
                >
                    <h2>Quản lý tài khoản sinh viên</h2>

                    <p>
                        Khoa có thể tạo tài khoản mới
                        và đặt lại mật khẩu cho sinh viên.
                    </p>

                    <Link
                        to="/faculty/students"
                        style={{
                            display: 'inline-block',
                            padding: '12px 20px',
                            marginTop: '18px',
                            backgroundColor: '#b91c1c',
                            color: '#ffffff',
                            borderRadius: '8px',
                            textDecoration: 'none',
                            fontWeight: 600,
                        }}
                    >
                        Quản lý tài khoản sinh viên
                    </Link>
                </div>

                {/* KHUNG DASHBOARD KHOA - CHƯA KẾT NỐI DỮ LIỆU */}
                <div className="dashboard-grid">

                    <article className="dashboard-card">
                        <span className="dashboard-card-index">
                            01
                        </span>

                        <h2>Phổ kỹ năng sinh viên</h2>

                        <p>
                            Khu vực hiển thị thống kê kỹ năng
                            của sinh viên khi Dashboard Service
                            được kết nối.
                        </p>

                        <span className="dashboard-status">
                            Chưa kết nối dữ liệu
                        </span>
                    </article>

                    <article className="dashboard-card">
                        <span className="dashboard-card-index">
                            02
                        </span>

                        <h2>Kỹ năng còn thiếu phổ biến</h2>

                        <p>
                            Khu vực hiển thị các kỹ năng
                            còn thiếu từ kết quả Skill Gap.
                        </p>

                        <span className="dashboard-status">
                            Chưa kết nối dữ liệu
                        </span>
                    </article>

                </div>

            </section>

        </main>
    )
}

export default FacultyDashboardPage

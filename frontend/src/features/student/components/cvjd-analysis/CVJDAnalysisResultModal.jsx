
function CVJDAnalysisResultModal({ cv, jd, onClose }) {
    // Dữ liệu minh họa giao diện, chưa phải kết quả AI thực tế.
    const mockResult = {
        matchScore: 78,
        summary:
            'CV có mức độ phù hợp khá tốt với vị trí tuyển dụng. Ứng viên có nền tảng kỹ thuật phù hợp, tuy nhiên vẫn còn một số kỹ năng và yêu cầu cần bổ sung.',
        matchedSkills: [
            {
                name: 'JavaScript',
                level: 'Tốt',
                description: 'Có kinh nghiệm và thể hiện rõ trong CV.',
            },
            {
                name: 'ReactJS',
                level: 'Tốt',
                description: 'Có đề cập đến công nghệ trong các dự án.',
            },
            {
                name: 'HTML / CSS',
                level: 'Tốt',
                description: 'Đáp ứng các yêu cầu frontend cơ bản.',
            },
        ],
        missingSkills: [
            {
                name: 'TypeScript',
                description: 'Chưa thể hiện rõ kinh nghiệm sử dụng.',
            },
            {
                name: 'Unit Testing',
                description: 'CV chưa đề cập đến kỹ năng kiểm thử.',
            },
        ],
        strengths: [
            'Có nền tảng kiến thức phù hợp với vị trí ứng tuyển.',
            'Có các dự án liên quan đến phát triển giao diện web.',
            'CV trình bày được các công nghệ đã sử dụng.',
        ],
        improvements: [
            'Bổ sung kinh nghiệm hoặc dự án sử dụng TypeScript nếu có.',
            'Làm rõ vai trò và kết quả đạt được trong từng dự án.',
            'Bổ sung thông tin về kiểm thử phần mềm nếu có kinh nghiệm.',
        ],
    }

    const scoreLevel =
        mockResult.matchScore >= 80
            ? 'Cao'
            : mockResult.matchScore >= 60
                ? 'Khá'
                : 'Thấp'

    return (
        <div
            className="cvjd-result-overlay"
            onMouseDown={(event) => {
                if (event.target === event.currentTarget) {
                    onClose()
                }
            }}
        >
            <div
                className="cvjd-result-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="cvjd-result-title"
            >
                {/* Header */}
                <div className="cvjd-result-header">
                    <div>
                        <span className="cvjd-result-eyebrow">
                            CV - JD ANALYSIS
                        </span>

                        <h2 id="cvjd-result-title">
                            Kết quả phân tích CV-JD
                        </h2>

                        <p>
                            Báo cáo mức độ phù hợp giữa hồ sơ ứng viên
                            và vị trí tuyển dụng.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="cvjd-result-close"
                        onClick={onClose}
                        aria-label="Đóng kết quả"
                    >
                        ×
                    </button>
                </div>

                {/* Selected information */}
                <div className="cvjd-result-context">
                    <div>
                        <span>CV được phân tích</span>
                        <strong title={cv?.name}>
                            {cv?.name || 'CV đã chọn'}
                        </strong>
                    </div>

                    <div>
                        <span>Vị trí tuyển dụng</span>
                        <strong title={jd?.name}>
                            {jd?.name || 'JD đã chọn'}
                        </strong>
                    </div>
                </div>

                <div className="cvjd-result-body">
                    {/* Score */}
                    <section className="cvjd-result-score-section">
                        <div className="cvjd-score-circle">
                            <div>
                                <strong>{mockResult.matchScore}%</strong>
                                <span>Match Score</span>
                            </div>
                        </div>

                        <div className="cvjd-score-description">
                            <span className="cvjd-score-label">
                                Mức độ phù hợp: {scoreLevel}
                            </span>

                            <h3>CV đáp ứng khá tốt yêu cầu công việc</h3>

                            <p>{mockResult.summary}</p>
                        </div>
                    </section>

                    {/* Skill analysis */}
                    <section className="cvjd-result-section">
                        <div className="cvjd-result-section-heading">
                            <div>
                                <h3>Phân tích kỹ năng</h3>
                                <p>
                                    So sánh kỹ năng trong CV với yêu cầu JD.
                                </p>
                            </div>
                        </div>

                        <div className="cvjd-skill-grid">
                            <div className="cvjd-skill-card matched">
                                <div className="cvjd-skill-card-heading">
                                    <h4>Kỹ năng phù hợp</h4>
                                    <span>
                                        {mockResult.matchedSkills.length}
                                    </span>
                                </div>

                                {mockResult.matchedSkills.map((skill) => (
                                    <div
                                        className="cvjd-skill-item"
                                        key={skill.name}
                                    >
                                        <div>
                                            <strong>{skill.name}</strong>
                                            <span>{skill.level}</span>
                                        </div>

                                        <p>{skill.description}</p>
                                    </div>
                                ))}
                            </div>

                            <div className="cvjd-skill-card missing">
                                <div className="cvjd-skill-card-heading">
                                    <h4>Kỹ năng cần bổ sung</h4>
                                    <span>
                                        {mockResult.missingSkills.length}
                                    </span>
                                </div>

                                {mockResult.missingSkills.map((skill) => (
                                    <div
                                        className="cvjd-skill-item"
                                        key={skill.name}
                                    >
                                        <div>
                                            <strong>{skill.name}</strong>
                                        </div>

                                        <p>{skill.description}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </section>

                    {/* Strengths */}
                    <section className="cvjd-result-section">
                        <div className="cvjd-result-section-heading">
                            <div>
                                <h3>Điểm mạnh của CV</h3>
                                <p>
                                    Những nội dung đang đáp ứng tốt yêu cầu
                                    tuyển dụng.
                                </p>
                            </div>
                        </div>

                        <ul className="cvjd-result-list strengths">
                            {mockResult.strengths.map((item, index) => (
                                <li key={index}>
                                    <span>✓</span>
                                    {item}
                                </li>
                            ))}
                        </ul>
                    </section>

                    {/* Improvements */}
                    <section className="cvjd-result-section">
                        <div className="cvjd-result-section-heading">
                            <div>
                                <h3>Đề xuất cải thiện</h3>
                                <p>
                                    Một số gợi ý giúp hồ sơ thể hiện năng lực
                                    rõ ràng hơn.
                                </p>
                            </div>
                        </div>

                        <ul className="cvjd-result-list improvements">
                            {mockResult.improvements.map((item, index) => (
                                <li key={index}>
                                    <span>{index + 1}</span>
                                    {item}
                                </li>
                            ))}
                        </ul>
                    </section>

                    <div className="cvjd-result-disclaimer">
                        Đây là kết quả minh họa phục vụ kiểm thử giao diện,
                        không phải kết quả phân tích thực tế từ hệ thống AI.
                    </div>
                </div>

                {/* Footer */}
                <div className="cvjd-result-footer">
                    <button
                        type="button"
                        className="cvjd-result-done"
                        onClick={onClose}
                    >
                        Đóng kết quả
                    </button>
                </div>
            </div>
        </div>
    )
}

export default CVJDAnalysisResultModal

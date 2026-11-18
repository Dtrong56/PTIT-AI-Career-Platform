import { useState } from 'react'
import '../../../styles/cvjd-analysis.css'
import CVJDSelectionModal from '../components/cvjd-analysis/CVJDSelectionModal'
import CVJDAnalysisResultModal from '../components/cvjd-analysis/CVJDAnalysisResultModal'

function CVJDAnalysisPage() {
    const [selectedCV, setSelectedCV] = useState(null)
    const [selectedJD, setSelectedJD] = useState(null)
    const [activeModal, setActiveModal] = useState(null)
    const [showResultModal, setShowResultModal] = useState(false)

    const openSelectionModal = (type) => {
        setActiveModal(type)
    }

    const closeModal = () => {
        setActiveModal(null)
    }

    const handleAnalysis = () => {
        if (!selectedCV || !selectedJD) {
            return
        }

        // Chỉ mở popup kết quả mẫu, chưa gọi API phân tích.
        setShowResultModal(true)
    }

    return (
        <div className="cvjd-page">

            {/* Page heading */}
            <div className="cvjd-page-heading">
                <div>
                    <span className="cvjd-eyebrow">
                        CV - JD ANALYSIS
                    </span>

                    <h1>Phân tích CV-JD</h1>

                    <p>
                        Đánh giá mức độ phù hợp giữa hồ sơ năng lực
                        của bạn và yêu cầu của vị trí công việc.
                    </p>
                </div>
            </div>

            {/* Selection section */}
            <section className="cvjd-selection-section">

                <div className="cvjd-section-heading">
                    <div>
                        <h2>Lựa chọn dữ liệu phân tích</h2>
                        <p>
                            Chọn một CV và một JD để bắt đầu phân tích.
                        </p>
                    </div>
                </div>

                <div className="cvjd-selection-grid">

                    {/* CV selection */}
                    <article className="cvjd-selection-card">
                        <div className="cvjd-card-header">
                            <div className="cvjd-card-icon cvjd-cv-icon">
                                <span>CV</span>
                            </div>

                            <div>
                                <h3>Hồ sơ CV</h3>
                                <p>Curriculum Vitae</p>
                            </div>
                        </div>

                        {selectedCV ? (
                            <div className="cvjd-selected-file">
                                <div className="cvjd-file-icon">
                                    PDF
                                </div>

                                <div className="cvjd-file-info">
                                    <strong>{selectedCV.name}</strong>
                                    <span>Đã chọn</span>
                                </div>

                                <button
                                    type="button"
                                    className="cvjd-change-button"
                                    onClick={() => openSelectionModal('cv')}
                                >
                                    Thay đổi
                                </button>
                            </div>
                        ) : (
                            <div className="cvjd-empty-selection">
                                <div className="cvjd-empty-icon">
                                    <span>CV</span>
                                </div>

                                <strong>Chưa chọn CV</strong>

                                <p>
                                    Chọn hồ sơ CV đã tải lên để
                                    thực hiện phân tích.
                                </p>
                            </div>
                        )}

                        <button
                            type="button"
                            className="cvjd-select-button"
                            onClick={() => openSelectionModal('cv')}
                        >
                            <span>+</span>
                            {selectedCV ? 'Chọn CV khác' : 'Chọn CV'}
                        </button>
                    </article>

                    {/* JD selection */}
                    <article className="cvjd-selection-card">
                        <div className="cvjd-card-header">
                            <div className="cvjd-card-icon cvjd-jd-icon">
                                <span>JD</span>
                            </div>

                            <div>
                                <h3>Mô tả công việc</h3>
                                <p>Job Description</p>
                            </div>
                        </div>

                        {selectedJD ? (
                            <div className="cvjd-selected-file">
                                <div className="cvjd-file-icon cvjd-jd-file-icon">
                                    JD
                                </div>

                                <div className="cvjd-file-info">
                                    <strong>{selectedJD.name}</strong>
                                    <span>Đã chọn</span>
                                </div>

                                <button
                                    type="button"
                                    className="cvjd-change-button"
                                    onClick={() => openSelectionModal('jd')}
                                >
                                    Thay đổi
                                </button>
                            </div>
                        ) : (
                            <div className="cvjd-empty-selection">
                                <div className="cvjd-empty-icon cvjd-jd-empty-icon">
                                    <span>JD</span>
                                </div>

                                <strong>Chưa chọn JD</strong>

                                <p>
                                    Chọn mô tả công việc của vị trí
                                    bạn muốn ứng tuyển.
                                </p>
                            </div>
                        )}

                        <button
                            type="button"
                            className="cvjd-select-button"
                            onClick={() => openSelectionModal('jd')}
                        >
                            <span>+</span>
                            {selectedJD ? 'Chọn JD khác' : 'Chọn JD'}
                        </button>
                    </article>
                </div>
            </section>

            {/* Analysis action */}
            <section className="cvjd-analysis-action">
                <div className="cvjd-action-info">
                    <div className="cvjd-action-icon">
                        <span>AI</span>
                    </div>

                    <div>
                        <h3>Sẵn sàng phân tích?</h3>
                        <p>
                            Hệ thống sẽ đánh giá mức độ tương thích
                            giữa CV và yêu cầu công việc.
                        </p>
                    </div>
                </div>

                <button
                    type="button"
                    className="cvjd-analysis-button"
                    disabled={!selectedCV || !selectedJD}
                    onClick={handleAnalysis}
                >
                    Phân tích CV-JD
                    <span>→</span>
                </button>
            </section>

            {/* Selection modal component */}
            {activeModal && (
                <CVJDSelectionModal
                    type={activeModal}
                    onClose={() => setActiveModal(null)}
                    onSelect={(item) => {
                        if (activeModal === 'cv') {
                            setSelectedCV(item)
                        } else {
                            setSelectedJD(item)
                        }

                        setActiveModal(null)
                    }}
                />
            )}

            {/* Analysis result component */}
            {showResultModal && (
                <CVJDAnalysisResultModal
                    cv={selectedCV}
                    jd={selectedJD}
                    onClose={() => setShowResultModal(false)}
                />
            )}
        </div>
    )
}

export default CVJDAnalysisPage
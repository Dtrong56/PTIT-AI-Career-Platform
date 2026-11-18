
import { useState } from 'react'

function CVUploadForm({ onSubmit, onCancel }) {
    const [file, setFile] = useState(null)
    const [error, setError] = useState('')

    const handleFileChange = (event) => {
        const selectedFile = event.target.files?.[0]

        setError('')
        setFile(selectedFile || null)
    }

    const handleSubmit = (event) => {
        event.preventDefault()

        if (!file) {
            setError('Vui lòng chọn file CV trước khi tiếp tục.')
            return
        }

        onSubmit(file)
    }

    const formatFileSize = (bytes) => {
        if (bytes < 1024 * 1024) {
            return `${(bytes / 1024).toFixed(1)} KB`
        }

        return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
    }

    return (
        <form className="cvjd-form" onSubmit={handleSubmit}>
            <div className="cvjd-form-heading">
                <h3>Tải lên CV mới</h3>
                <p>
                    Chọn hồ sơ CV từ thiết bị của bạn để bổ sung vào
                    danh sách.
                </p>
            </div>

            <div className="cvjd-form-group">
                <label>
                    File CV <span>*</span>
                </label>

                <label className="cvjd-upload-area">
                    <input
                        type="file"
                        accept=".pdf,.doc,.docx"
                        onChange={handleFileChange}
                    />

                    <span className="cvjd-upload-icon">
                        <svg
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="1.7"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        >
                            <path d="M12 16V4" />
                            <path d="m7 9 5-5 5 5" />
                            <path d="M4 16v3a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-3" />
                        </svg>
                    </span>

                    <strong>
                        {file
                            ? 'Thay đổi file đã chọn'
                            : 'Nhấn để chọn file CV'}
                    </strong>

                    <span className="cvjd-upload-hint">
                        Hỗ trợ PDF, DOC, DOCX
                    </span>
                </label>

                {file && (
                    <div className="cvjd-uploaded-file">
                        <div className="cvjd-uploaded-file-icon">
                            PDF
                        </div>

                        <div className="cvjd-uploaded-file-info">
                            <strong title={file.name}>
                                {file.name}
                            </strong>
                            <span>{formatFileSize(file.size)}</span>
                        </div>

                        <button
                            type="button"
                            className="cvjd-remove-file"
                            onClick={() => setFile(null)}
                            aria-label="Xóa file đã chọn"
                        >
                            ×
                        </button>
                    </div>
                )}

                {error && (
                    <p className="cvjd-form-error">{error}</p>
                )}
            </div>

            <div className="cvjd-form-note">
                <strong>Lưu ý</strong>
                <p>
                    File được chọn chỉ được lưu tạm trong giao diện.
                    Chức năng tải lên máy chủ sẽ được tích hợp sau.
                </p>
            </div>

            <div className="cvjd-form-actions">
                <button
                    type="button"
                    className="cvjd-form-cancel"
                    onClick={onCancel}
                >
                    Quay lại
                </button>

                <button
                    type="submit"
                    className="cvjd-form-submit"
                    disabled={!file}
                >
                    Xác nhận
                </button>
            </div>
        </form>
    )
}

export default CVUploadForm

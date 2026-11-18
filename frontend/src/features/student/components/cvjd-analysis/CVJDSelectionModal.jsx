
import { useMemo, useState } from 'react'
import CVUploadForm from './CVUploadForm'
import JDCreateForm from './JDCreateForm'

const initialCVs = [
    {
        id: 1,
        name: 'CV_Frontend_Developer.pdf',
        description: 'Frontend Developer',
        createdAt: '15/11/2026',
        status: 'COMPLETED',
    },
    {
        id: 2,
        name: 'CV_Backend_Java.pdf',
        description: 'Backend Java Developer',
        createdAt: '14/11/2026',
        status: 'PENDING',
    },
    {
        id: 3,
        name: 'CV_Intern.pdf',
        description: 'Software Engineer Intern',
        createdAt: '12/11/2026',
        status: 'FAILED',
    },
    {
        id: 4,
        name: 'CV_Data_Analyst.pdf',
        description: 'Data Analyst',
        createdAt: '10/11/2026',
        status: 'COMPLETED',
    },
]

const initialJDs = [
    {
        id: 1,
        name: 'Frontend Developer Intern',
        description: 'ABC Technology',
        createdAt: '16/11/2026',
        status: 'COMPLETED',
    },
    {
        id: 2,
        name: 'Java Backend Developer',
        description: 'XYZ Solutions',
        createdAt: '15/11/2026',
        status: 'PENDING',
    },
    {
        id: 3,
        name: 'Data Analyst Intern',
        description: 'ABC Technology',
        createdAt: '13/11/2026',
        status: 'FAILED',
    },
    {
        id: 4,
        name: 'ReactJS Developer',
        description: 'QRS Software',
        createdAt: '11/11/2026',
        status: 'COMPLETED',
    },
]

const statusConfig = {
    COMPLETED: {
        label: 'Hoàn tất',
        className: 'completed',
    },
    PENDING: {
        label: 'Đang xử lý',
        className: 'pending',
    },
    FAILED: {
        label: 'Thất bại',
        className: 'failed',
    },
}

function CVJDSelectionModal({ type, onClose, onSelect }) {
    const isCV = type === 'cv'

    const [cvs, setCvs] = useState(initialCVs)
    const [jds, setJds] = useState(initialJDs)
    const [searchTerm, setSearchTerm] = useState('')
    const [activeForm, setActiveForm] = useState(null)

    const data = isCV ? cvs : jds

    const filteredData = useMemo(() => {
        const keyword = searchTerm.trim().toLowerCase()

        if (!keyword) return data

        return data.filter((item) =>
            `${item.name} ${item.description}`
                .toLowerCase()
                .includes(keyword)
        )
    }, [data, searchTerm])

    const handleRefresh = () => {
        setSearchTerm('')
    }

    const handleAddCV = (file) => {
        const newCV = {
            id: Date.now(),
            name: file.name,
            description: 'CV mới tải lên',
            createdAt: new Date().toLocaleDateString('vi-VN'),
            status: 'PENDING',
            file,
        }

        setCvs((previous) => [newCV, ...previous])
        setActiveForm(null)
        setSearchTerm('')
    }

    const handleCreateJD = (formData) => {
        const newJD = {
            id: Date.now(),
            name: formData.title,
            description: formData.company,
            createdAt: new Date().toLocaleDateString('vi-VN'),
            status: 'PENDING',
            details: formData,
        }

        setJds((previous) => [newJD, ...previous])
        setActiveForm(null)
        setSearchTerm('')
    }

    const handleAddNew = () => {
        setActiveForm(isCV ? 'cv' : 'jd')
    }

    return (
        <div
            className="cvjd-modal-overlay"
            onMouseDown={(event) => {
                if (event.target === event.currentTarget) {
                    onClose()
                }
            }}
        >
            <div
                className={`cvjd-modal cvjd-selection-modal ${
                    activeForm ? 'cvjd-form-modal' : ''
                }`}
                role="dialog"
                aria-modal="true"
                aria-labelledby="cvjd-modal-title"
            >
                <div className="cvjd-modal-header">
                    <div>
                        <h2 id="cvjd-modal-title">
                            {activeForm
                                ? isCV
                                    ? 'Tải lên CV mới'
                                    : 'Tạo JD mới'
                                : isCV
                                    ? 'Lựa chọn CV'
                                    : 'Lựa chọn JD'}
                        </h2>

                        <p>
                            {activeForm
                                ? 'Bổ sung dữ liệu mới vào danh sách.'
                                : isCV
                                    ? 'Chọn hồ sơ CV bạn muốn sử dụng để phân tích.'
                                    : 'Chọn mô tả công việc bạn muốn sử dụng để phân tích.'}
                        </p>
                    </div>

                    <button
                        type="button"
                        className="cvjd-modal-close"
                        onClick={onClose}
                        aria-label="Đóng"
                    >
                        ×
                    </button>
                </div>

                {activeForm ? (
                    <div className="cvjd-modal-form-body">
                        {isCV ? (
                            <CVUploadForm
                                onSubmit={handleAddCV}
                                onCancel={() => setActiveForm(null)}
                            />
                        ) : (
                            <JDCreateForm
                                onSubmit={handleCreateJD}
                                onCancel={() => setActiveForm(null)}
                            />
                        )}
                    </div>
                ) : (
                    <>
                        <div className="cvjd-modal-toolbar">
                            <div className="cvjd-search-box">
                                <span>⌕</span>

                                <input
                                    type="text"
                                    placeholder={
                                        isCV
                                            ? 'Tìm kiếm tên CV...'
                                            : 'Tìm kiếm tên JD hoặc công ty...'
                                    }
                                    value={searchTerm}
                                    onChange={(event) =>
                                        setSearchTerm(event.target.value)
                                    }
                                />

                                {searchTerm && (
                                    <button
                                        type="button"
                                        onClick={() => setSearchTerm('')}
                                        aria-label="Xóa tìm kiếm"
                                    >
                                        ×
                                    </button>
                                )}
                            </div>

                            <button
                                type="button"
                                className="cvjd-refresh-button"
                                onClick={handleRefresh}
                            >
                                ↻ Làm mới
                            </button>
                        </div>

                        <div className="cvjd-status-legend">
                            <span>
                                <i className="cvjd-status-dot completed" />
                                Hoàn tất
                            </span>

                            <span>
                                <i className="cvjd-status-dot pending" />
                                Đang xử lý
                            </span>

                            <span>
                                <i className="cvjd-status-dot failed" />
                                Thất bại
                            </span>
                        </div>

                        <div className="cvjd-table-wrapper">
                            <table className="cvjd-table">
                                <thead>
                                    <tr>
                                        <th>
                                            {isCV
                                                ? 'Tên CV'
                                                : 'Vị trí tuyển dụng'}
                                        </th>
                                        <th>
                                            {isCV
                                                ? 'Loại hồ sơ'
                                                : 'Công ty'}
                                        </th>
                                        <th>Ngày tạo</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {filteredData.length > 0 ? (
                                        filteredData.map((item) => {
                                            const status =
                                                statusConfig[item.status]

                                            const isCompleted =
                                                item.status === 'COMPLETED'

                                            return (
                                                <tr key={item.id}>
                                                    <td>
                                                        <div className="cvjd-table-name">
                                                            <span
                                                                className={`cvjd-table-file-icon ${
                                                                    isCV
                                                                        ? 'cv'
                                                                        : 'jd'
                                                                }`}
                                                            >
                                                                {isCV ? 'PDF' : 'JD'}
                                                            </span>

                                                            <strong title={item.name}>
                                                                {item.name}
                                                            </strong>
                                                        </div>
                                                    </td>

                                                    <td>{item.description}</td>
                                                    <td>{item.createdAt}</td>

                                                    <td>
                                                        <span
                                                            className={`cvjd-status-badge ${status.className}`}
                                                        >
                                                            {status.label}
                                                        </span>
                                                    </td>

                                                    <td>
                                                        <button
                                                            type="button"
                                                            className="cvjd-row-select-button"
                                                            disabled={!isCompleted}
                                                            onClick={() =>
                                                                onSelect(item)
                                                            }
                                                        >
                                                            Chọn
                                                        </button>
                                                    </td>
                                                </tr>
                                            )
                                        })
                                    ) : (
                                        <tr>
                                            <td
                                                colSpan="5"
                                                className="cvjd-no-results"
                                            >
                                                Không tìm thấy dữ liệu phù hợp.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>

                        <div className="cvjd-add-new-section">
                            <div>
                                <strong>
                                    Chưa có {isCV ? 'CV phù hợp' : 'JD phù hợp'}?
                                </strong>

                                <p>
                                    {isCV
                                        ? 'Bạn có thể tải lên một hồ sơ CV mới.'
                                        : 'Bạn có thể tạo JD mới bằng cách nhập mô tả công việc.'}
                                </p>
                            </div>

                            <button
                                type="button"
                                className="cvjd-add-new-button"
                                onClick={handleAddNew}
                            >
                                + {isCV ? 'Tải CV mới' : 'Tạo JD mới'}
                            </button>
                        </div>

                        <div className="cvjd-modal-footer">
                            <span>{filteredData.length} bản ghi</span>

                            <button
                                type="button"
                                className="cvjd-modal-cancel"
                                onClick={onClose}
                            >
                                Đóng
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    )
}

export default CVJDSelectionModal

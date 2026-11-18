
import { useState } from 'react'

function JDCreateForm({ onSubmit, onCancel }) {
    const [formData, setFormData] = useState({
        title: '',
        company: '',
        description: '',
        requirements: '',
    })

    const [error, setError] = useState('')

    const handleChange = (event) => {
        const { name, value } = event.target

        setFormData((previous) => ({
            ...previous,
            [name]: value,
        }))

        setError('')
    }

    const handleSubmit = (event) => {
        event.preventDefault()

        if (
            !formData.title.trim() ||
            !formData.company.trim() ||
            !formData.description.trim()
        ) {
            setError('Vui lòng nhập đầy đủ các trường bắt buộc.')
            return
        }

        onSubmit({
            ...formData,
            title: formData.title.trim(),
            company: formData.company.trim(),
            description: formData.description.trim(),
            requirements: formData.requirements.trim(),
        })
    }

    return (
        <form className="cvjd-form" onSubmit={handleSubmit}>
            <div className="cvjd-form-heading">
                <h3>Tạo JD mới</h3>
                <p>
                    Nhập thông tin mô tả công việc để bổ sung vào
                    danh sách JD.
                </p>
            </div>

            <div className="cvjd-form-grid">
                <div className="cvjd-form-group">
                    <label htmlFor="jd-title">
                        Vị trí tuyển dụng <span>*</span>
                    </label>

                    <input
                        id="jd-title"
                        name="title"
                        type="text"
                        placeholder="Ví dụ: Frontend Developer"
                        value={formData.title}
                        onChange={handleChange}
                    />
                </div>

                <div className="cvjd-form-group">
                    <label htmlFor="jd-company">
                        Công ty <span>*</span>
                    </label>

                    <input
                        id="jd-company"
                        name="company"
                        type="text"
                        placeholder="Nhập tên công ty"
                        value={formData.company}
                        onChange={handleChange}
                    />
                </div>
            </div>

            <div className="cvjd-form-group">
                <label htmlFor="jd-description">
                    Mô tả công việc <span>*</span>
                </label>

                <textarea
                    id="jd-description"
                    name="description"
                    rows="5"
                    placeholder="Nhập nội dung mô tả công việc..."
                    value={formData.description}
                    onChange={handleChange}
                />
            </div>

            <div className="cvjd-form-group">
                <label htmlFor="jd-requirements">
                    Yêu cầu ứng viên
                </label>

                <textarea
                    id="jd-requirements"
                    name="requirements"
                    rows="4"
                    placeholder="Nhập các yêu cầu về kỹ năng, kinh nghiệm, kiến thức..."
                    value={formData.requirements}
                    onChange={handleChange}
                />
            </div>

            {error && (
                <p className="cvjd-form-error">{error}</p>
            )}

            <div className="cvjd-form-note">
                <strong>Lưu ý</strong>
                <p>
                    JD sẽ được thêm vào danh sách ở trạng thái chờ xử lý.
                    Chức năng lưu và preprocessing sẽ được tích hợp sau.
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
                >
                    Tạo JD
                </button>
            </div>
        </form>
    )
}

export default JDCreateForm

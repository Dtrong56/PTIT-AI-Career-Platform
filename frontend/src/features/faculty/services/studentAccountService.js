
async function sendFacultyRequest(path, accessToken, body = null) {
    if (!accessToken) {
        throw new Error('Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.')
    }

    const headers = {
        Authorization: `Bearer ${accessToken}`,
    }

    if (body !== null) {
        headers['Content-Type'] = 'application/json'
    }

    const response = await fetch(path, {
        method: 'POST',
        headers,
        ...(body !== null ? { body: JSON.stringify(body) } : {}),
    })

    if (!response.ok) {
        const data = await response.json().catch(() => null)

        const error = new Error(
            data?.message || 'Yêu cầu không thành công.'
        )

        error.status = response.status
        throw error
    }

    return true
}

// Khoa tạo tài khoản sinh viên
export async function createStudentAccount(
    accessToken,
    studentId,
    fullName,
    email
) {
    return sendFacultyRequest(
        '/api/faculty/students',
        accessToken,
        {
            studentId: studentId.trim(),
            fullName: fullName.trim(),
            email: email.trim(),
        }
    )
}

// Khoa đặt lại mật khẩu sinh viên
export async function resetStudentPassword(
    accessToken,
    studentId
) {
    const id = encodeURIComponent(studentId.trim())

    return sendFacultyRequest(
        `/api/faculty/students/${id}/reset-password`,
        accessToken
    )
}

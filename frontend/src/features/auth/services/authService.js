
export async function login(studentId, password) {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            studentId: studentId.trim(),
            password,
        }),
    })

    const data = await response.json().catch(() => null)

    if (!response.ok) {
        const error = new Error(
            data?.message || 'Đăng nhập thất bại.'
        )
        error.status = response.status
        throw error
    }

    if (!data?.accessToken) {
        throw new Error('Máy chủ không trả về token đăng nhập.')
    }

    return data
}


export async function changePassword(
    accessToken,
    currentPassword,
    newPassword
) {
    const response = await fetch('/api/auth/change-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({
            currentPassword,
            newPassword,
        }),
    })

    const data = await response.json().catch(() => null)

    if (!response.ok) {
        const error = new Error(
            data?.message || 'Đổi mật khẩu thất bại.'
        )
        error.status = response.status
        throw error
    }

    return data
}


package com.ptit.identity_service.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}

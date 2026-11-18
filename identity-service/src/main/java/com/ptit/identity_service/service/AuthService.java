
package com.ptit.identity_service.service;

import com.ptit.identity_service.dto.LoginRequest;
import com.ptit.identity_service.dto.LoginResponse;
import com.ptit.identity_service.entity.Account;
import com.ptit.identity_service.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.ptit.identity_service.dto.ChangePasswordRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final long expirationSeconds;

        public AuthService(
                        AccountRepository accountRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {

                this.accountRepository = accountRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.expirationSeconds = expirationSeconds;
        }

        public LoginResponse login(LoginRequest request) {

                // 1. Kiểm tra dữ liệu đầu vào
                if (request == null
                                || request.studentId() == null
                                || request.studentId().isBlank()
                                || request.password() == null
                                || request.password().isBlank()) {

                        throw new IllegalArgumentException(
                                        "MSSV và mật khẩu không được để trống");
                }

                // 2. Tìm tài khoản theo MSSV
                String studentId = request.studentId().trim();

                Account account = accountRepository.findByUsername(studentId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "MSSV hoặc mật khẩu không chính xác"));

                // 3. Kiểm tra tài khoản có đang hoạt động không
                if (!account.isEnabled()) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Tài khoản đã bị khóa");
                }

                // 4. Kiểm tra mật khẩu bằng BCrypt
                if (account.getPasswordHash() == null
                                || !passwordEncoder.matches(
                                                request.password(),
                                                account.getPasswordHash())) {

                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "MSSV hoặc mật khẩu không chính xác");
                }

                // 5. Tạo JWT
                String tokenStudentId = "STUDENT".equals(account.getRole())
                                ? account.getUsername()
                                : null;

                String token = jwtService.generateToken(
                                account.getUsername(),
                                account.getRole(),
                                account.isMustChangePassword(),
                                tokenStudentId);

                // 6. Trả kết quả đăng nhập

                return new LoginResponse(
                                token,
                                "Bearer",
                                expirationSeconds,
                                account.getRole(),
                                account.isMustChangePassword());
        }

        // ĐỔI MẬT KHẨU LẦN ĐẦU
        @Transactional
        public LoginResponse changePassword(
                        String username,
                        ChangePasswordRequest request) {

                // 1. Kiểm tra dữ liệu đầu vào
                if (request == null
                                || request.currentPassword() == null
                                || request.currentPassword().isBlank()
                                || request.newPassword() == null
                                || request.newPassword().isBlank()) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Mật khẩu không được để trống");
                }

                // 2. Tìm tài khoản theo MSSV
                Account account = accountRepository.findByUsername(username)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Tài khoản không tồn tại"));

                // 3. Kiểm tra tài khoản có hoạt động không
                if (!account.isEnabled()) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Tài khoản đã bị khóa");
                }

                // 4. Kiểm tra có phải tài khoản cần đổi mật khẩu lần đầu
                if (!account.isMustChangePassword()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Tài khoản không yêu cầu đổi mật khẩu lần đầu");
                }

                // 5. Kiểm tra mật khẩu hiện tại
                if (!passwordEncoder.matches(
                                request.currentPassword(),
                                account.getPasswordHash())) {

                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Mật khẩu hiện tại không chính xác");
                }

                // 6. Kiểm tra mật khẩu mới không trùng mật khẩu cũ
                if (passwordEncoder.matches(
                                request.newPassword(),
                                account.getPasswordHash())) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Mật khẩu mới phải khác mật khẩu cũ");
                }

                // 7. Mã hóa mật khẩu mới bằng BCrypt
                account.setPasswordHash(
                                passwordEncoder.encode(request.newPassword()));

                // 8. Đánh dấu đã đổi mật khẩu lần đầu
                account.setMustChangePassword(false);

                // 9. Lưu tài khoản vào database
                accountRepository.save(account);

                // 10. Tạo JWT mới
                String tokenStudentId = "STUDENT".equals(account.getRole())
                                ? account.getUsername()
                                : null;

                String token = jwtService.generateToken(
                                account.getUsername(),
                                account.getRole(),
                                account.isMustChangePassword(),
                                tokenStudentId);

                // 11. Trả kết quả
                return new LoginResponse(
                                token,
                                "Bearer",
                                expirationSeconds,
                                account.getRole(),
                                false);
        }
}


package com.ptit.identity_service.service;

import com.ptit.identity_service.repository.AccountRepository;
import com.ptit.identity_service.repository.UserInfoRepository;
import com.ptit.identity_service.dto.CreateStudentAccountRequest;
import com.ptit.identity_service.entity.Account;
import com.ptit.identity_service.entity.UserInfo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class StudentAccountService {

    private final AccountRepository accountRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentAccountService(
            AccountRepository accountRepository,
            UserInfoRepository userInfoRepository,
            PasswordEncoder passwordEncoder) {

        this.accountRepository = accountRepository;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Kiểm tra sinh viên đã tồn tại
    public boolean studentExists(String studentId) {

        if (studentId == null || studentId.isBlank()) {
            return false;
        }

        String normalizedId = studentId.trim();

        return accountRepository.existsByUsername(normalizedId)
                || userInfoRepository.existsByStudentId(normalizedId);
    }

    // Mã hóa mật khẩu
    private String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // KHOA TẠO TÀI KHOẢN SINH VIÊN
    @Transactional
    public void createStudentAccount(CreateStudentAccountRequest request) {

        // 1. Kiểm tra request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Thông tin tài khoản không được để trống");
        }

        // 2. Kiểm tra MSSV
        String studentId = request.getStudentId();

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "MSSV không được để trống");
        }

        studentId = studentId.trim();

        if (studentExists(studentId)) {
            throw new IllegalArgumentException(
                    "MSSV đã tồn tại");
        }

        // 3. Kiểm tra họ tên
        String fullName = request.getFullName();

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Họ tên sinh viên không được để trống");
        }

        fullName = fullName.trim();

        // 4. Kiểm tra email
        String email = request.getEmail();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email không được để trống");
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Email sinh viên không hợp lệ");
        }

        if (userInfoRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng");
        }

        // 5. Tạo Account
        Account account = new Account();

        account.setUsername(studentId);
        account.setRole("STUDENT");
        account.setEnabled(true);
        account.setMustChangePassword(true);
        account.setPasswordHash(hashPassword("123@Ptit"));

        // 6. Tạo UserInfo
        UserInfo userInfo = new UserInfo();

        userInfo.setStudentId(studentId);
        userInfo.setFullName(fullName);
        userInfo.setEmail(email);
        userInfo.setAccount(account);

        // 7. Lưu database
        accountRepository.save(account);
        userInfoRepository.save(userInfo);
    }

    // KHOA ĐẶT LẠI MẬT KHẨU SINH VIÊN
    @Transactional
    public void resetStudentPassword(String studentId) {

        // 1. Kiểm tra MSSV
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "MSSV không được để trống");
        }

        studentId = studentId.trim();

        // 2. Tìm tài khoản
        Account account = accountRepository.findByUsername(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Không tìm thấy tài khoản sinh viên"));

        // 3. Kiểm tra tài khoản có phải sinh viên
        if (!"STUDENT".equals(account.getRole())) {
            throw new IllegalArgumentException(
                    "Tài khoản không phải sinh viên");
        }

        // 4. Đặt lại mật khẩu mặc định
        account.setPasswordHash(hashPassword("123@Ptit"));

        // 5. Bắt buộc đổi mật khẩu lần tiếp theo
        account.setMustChangePassword(true);

        // 6. Lưu database
        accountRepository.save(account);
    }
}

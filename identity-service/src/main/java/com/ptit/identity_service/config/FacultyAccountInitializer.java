
package com.ptit.identity_service.config;

import com.ptit.identity_service.entity.Account;
import com.ptit.identity_service.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class FacultyAccountInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${FACULTY_BOOTSTRAP_USERNAME:}")
    private String username;

    @Value("${FACULTY_BOOTSTRAP_PASSWORD:}")
    private String initialPassword;

    public FacultyAccountInitializer(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {

        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Chỉ khởi tạo khi có thông tin cấu hình
        if (username == null || username.isBlank()
                || initialPassword == null || initialPassword.isBlank()) {
            return;
        }

        String facultyUsername = username.trim();

        // Kiểm tra tài khoản đã tồn tại
        var existingAccount = accountRepository
                .findByUsername(facultyUsername);

        if (existingAccount.isPresent()) {

            if (!"FACULTY".equals(existingAccount.get().getRole())) {
                throw new IllegalStateException(
                        "Username đã được sử dụng bởi tài khoản khác");
            }

            System.out.println("FACULTY account already exists.");
            return;
        }

        if (initialPassword.length() < 12) {
            throw new IllegalStateException(
                    "Initial faculty password must contain at least 12 characters");
        }

        // Tạo tài khoản FACULTY đầu tiên
        Account account = new Account();

        account.setUsername(facultyUsername);
        account.setPasswordHash(
                passwordEncoder.encode(initialPassword));

        account.setRole("FACULTY");
        account.setEnabled(true);
        account.setMustChangePassword(true);

        accountRepository.save(account);

        System.out.println(
                "Initial FACULTY account created: " + facultyUsername);
    }
}

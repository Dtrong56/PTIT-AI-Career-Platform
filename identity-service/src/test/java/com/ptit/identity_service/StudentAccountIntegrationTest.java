
package com.ptit.identity_service;

import com.ptit.identity_service.service.StudentAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ptit.identity_service.repository.AccountRepository;
import com.ptit.identity_service.repository.UserInfoRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentAccountIntegrationTest {

    @Autowired
    private StudentAccountService studentAccountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Test
    void contextLoads() {
    }
}

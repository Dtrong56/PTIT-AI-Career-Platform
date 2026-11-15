
package com.ptit.identity_service;

import com.ptit.identity_service.dto.CreateStudentAccountRequest;
import com.ptit.identity_service.repository.AccountRepository;
import com.ptit.identity_service.repository.UserInfoRepository;
import com.ptit.identity_service.service.StudentAccountService;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.verify;
import com.ptit.identity_service.entity.Account;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ptit.identity_service.entity.UserInfo;

public class StudentAccountServiceTest {

    @Test
    void shouldRejectEmptyStudentId() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);

        StudentAccountService service = new StudentAccountService(
                accountRepository,
                userInfoRepository,
                new BCryptPasswordEncoder());

        CreateStudentAccountRequest request = new CreateStudentAccountRequest();
        request.setStudentId("");
        request.setFullName("Nguyen Van A");
        request.setEmail("n24dccn150@student.ptithcm.edu.vn");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createStudentAccount(request));
    }

    @Test
    void shouldRejectDuplicateStudentId() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);

        org.mockito.Mockito.when(
                accountRepository.existsByUsername("N24DCCN150")).thenReturn(true);

        StudentAccountService service = new StudentAccountService(
                accountRepository,
                userInfoRepository,
                new BCryptPasswordEncoder());

        CreateStudentAccountRequest request = new CreateStudentAccountRequest();
        request.setStudentId("N24DCCN150");
        request.setFullName("Nguyen Van A");
        request.setEmail("n24dccn150@student.ptithcm.edu.vn");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createStudentAccount(request));
    }

    @Test
    void shouldCreateStudentAccountSuccessfully() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);

        StudentAccountService service = new StudentAccountService(
                accountRepository,
                userInfoRepository,
                new BCryptPasswordEncoder());

        CreateStudentAccountRequest request = new CreateStudentAccountRequest();
        request.setStudentId("N24DCCN150");
        request.setFullName("Nguyen Van A");
        request.setEmail("n24dccn150@student.ptithcm.edu.vn");

        service.createStudentAccount(request);

        ArgumentCaptor<UserInfo> userInfoCaptor = ArgumentCaptor.forClass(UserInfo.class);

        verify(userInfoRepository).save(userInfoCaptor.capture());

        UserInfo savedUserInfo = userInfoCaptor.getValue();

        assertEquals("N24DCCN150", savedUserInfo.getStudentId());
        assertEquals("Nguyen Van A", savedUserInfo.getFullName());
        assertEquals(
                "n24dccn150@student.ptithcm.edu.vn",
                savedUserInfo.getEmail());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepository).save(accountCaptor.capture());

        Account savedAccount = accountCaptor.getValue();

        assertEquals("N24DCCN150", savedAccount.getUsername());
        assertEquals("STUDENT", savedAccount.getRole());

        org.junit.jupiter.api.Assertions.assertSame(
                savedAccount,
                savedUserInfo.getAccount());

        org.junit.jupiter.api.Assertions.assertTrue(
                new BCryptPasswordEncoder().matches(
                        "123@Ptit",
                        savedAccount.getPasswordHash()));

    }

    @Test
    void shouldRejectInvalidEmail() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);

        StudentAccountService service = new StudentAccountService(
                accountRepository,
                userInfoRepository,
                new BCryptPasswordEncoder());

        CreateStudentAccountRequest request = new CreateStudentAccountRequest();
        request.setStudentId("N24DCCN150");
        request.setFullName("Nguyen Van A");
        request.setEmail("email-khong-hop-le");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createStudentAccount(request));
    }

    @Test
    void shouldResetStudentPasswordSuccessfully() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);

        Account account = new Account();
        account.setUsername("N24DCCN150");
        account.setRole("STUDENT");
        account.setPasswordHash("old-hash");
        account.setMustChangePassword(false);

        org.mockito.Mockito.when(
                accountRepository.findByUsername("N24DCCN150"))
                .thenReturn(java.util.Optional.of(account));

        StudentAccountService service = new StudentAccountService(
                accountRepository,
                userInfoRepository,
                new BCryptPasswordEncoder());

        service.resetStudentPassword("N24DCCN150");

        verify(accountRepository).save(account);

        org.junit.jupiter.api.Assertions.assertTrue(
                new BCryptPasswordEncoder().matches(
                        "123@Ptit",
                        account.getPasswordHash()));

        org.junit.jupiter.api.Assertions.assertTrue(
                account.isMustChangePassword());
    }

}

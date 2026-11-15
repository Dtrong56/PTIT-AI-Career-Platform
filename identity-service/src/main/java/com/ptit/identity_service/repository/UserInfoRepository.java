
package com.ptit.identity_service.repository;

import com.ptit.identity_service.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);
}

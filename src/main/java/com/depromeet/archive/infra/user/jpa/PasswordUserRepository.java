package com.depromeet.archive.infra.user.jpa;

import com.depromeet.archive.domain.user.entity.PasswordUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordUserRepository extends JpaRepository<PasswordUser, Long> {
    Optional<PasswordUser> findPasswordUserByMailAddress(String mailAddress);
}

package com.alexvait.accountingapi.usermanagement.repository;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByPublicId(String publicId);
}

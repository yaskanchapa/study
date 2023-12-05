package com.kse.wmsv2.oc_cs_003.repository;

import com.kse.wmsv2.oc_cs_003.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String userCd);

}

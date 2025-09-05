package com.kaiser.messenger_server.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Role;
import com.kaiser.messenger_server.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Page<User> findByRoleAndUsernameContainingIgnoreCase(Role role, String username, Pageable pageable);
}

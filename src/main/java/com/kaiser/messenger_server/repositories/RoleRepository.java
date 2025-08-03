package com.kaiser.messenger_server.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, String id);

    Optional<Role> findByName(String name);

    @EntityGraph(attributePaths = {"permission"})
    Optional<Role> findWithPermissionById(String id);
}

package com.kaiser.messenger_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Permission;
import com.kaiser.messenger_server.enums.ApiMethod;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsByApiPathAndMethod(String apiPath, ApiMethod method);

    boolean existsByApiPathAndMethodAndIdNot(String apiPath, ApiMethod method, String id);
}

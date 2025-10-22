package com.kaiser.messenger_server.modules.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.enums.ApiMethod;
import com.kaiser.messenger_server.modules.permission.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {
    boolean existsByApiPathAndMethod(String apiPath, ApiMethod method);

    boolean existsByApiPathAndMethodAndIdNot(String apiPath, ApiMethod method, String id);
}

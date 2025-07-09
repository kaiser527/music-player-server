package com.kaiser.messenger_server.services;

import java.util.HashSet;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.RoleRequest;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.RoleResponse;
import com.kaiser.messenger_server.entities.Permission;
import com.kaiser.messenger_server.entities.Role;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.mapper.RoleMapper;
import com.kaiser.messenger_server.repositories.PermissionRepository;
import com.kaiser.messenger_server.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @NonFinal
    @Value("${role.admin}")
    protected String ADMIN_ROLE;

    public RoleResponse createRole(RoleRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(roleRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.ROLE_EXIST);
        }

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if(permissions.size() == 0){
            throw new AppException(ErrorCode.PERMISSION_NOT_EXIST);
        }

        Role role = roleMapper.toCreateRole(request);
        
        role.setPermission(new HashSet<Permission>(permissions));
        role.setCreatedBy(authentication.getName());

        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse updateRole(String id, RoleRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));

        if(roleRepository.existsByNameAndIdNot(id, request.getName())){
            throw new AppException(ErrorCode.ROLE_EXIST);
        }

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if(permissions.size() == 0){
            throw new AppException(ErrorCode.PERMISSION_NOT_EXIST);
        }

        roleMapper.toUpdateRole(role, request);

        role.setPermission(new HashSet<Permission>(permissions));
        role.setUpdatedBy(authentication.getName());

        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }
    
    public PaginatedResponse<RoleResponse> getRolePaginated(Pageable page){
        Page<Role> rolePage = roleRepository.findAll(page);

        List<RoleResponse> roleResponses = rolePage.getContent()
            .stream()
            .map(roleMapper::toRoleResponse)
            .toList();

        return PaginatedResponse.<RoleResponse>builder()
            .pageNumber(rolePage.getNumber() + 1)
            .pageSize(rolePage.getSize())
            .totalPages(rolePage.getTotalPages())
            .data(roleResponses)
            .build();
    }

    public RoleResponse getSingleRole(String id){
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));

        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse deleteRole(String id){
        Role role = roleRepository.findWithPermissionsById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        
        if(role.getId().equals(ADMIN_ROLE)){
            throw new AppException(ErrorCode.DELETE_ADMIN_ROLE);
        }

        roleRepository.delete(role);

        return roleMapper.toRoleResponse(role);
    }
}

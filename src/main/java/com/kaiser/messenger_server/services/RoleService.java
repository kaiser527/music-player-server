package com.kaiser.messenger_server.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.role.RoleFilterRequest;
import com.kaiser.messenger_server.dto.request.role.RoleRequest;
import com.kaiser.messenger_server.dto.response.RoleResponse;
import com.kaiser.messenger_server.dto.response.share.PaginatedResponse;
import com.kaiser.messenger_server.entities.Permission;
import com.kaiser.messenger_server.entities.Role;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.mapper.RoleMapper;
import com.kaiser.messenger_server.repositories.PermissionRepository;
import com.kaiser.messenger_server.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    String ADMIN_ROLE;

    public RoleResponse createRole(RoleRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(roleRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.ROLE_EXIST);
        }

        request.getPermissionIds().forEach(item -> {
            if(!permissionRepository.existsById(item)){
                throw new AppException(ErrorCode.PERMISSION_NOT_EXIST);
            }
        });

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());

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

        request.getPermissionIds().forEach(item -> {
            if(!permissionRepository.existsById(item)){
                throw new AppException(ErrorCode.PERMISSION_NOT_EXIST);
            }
        });

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());

        roleMapper.toUpdateRole(role, request);

        role.setPermission(new HashSet<Permission>(permissions));
        role.setUpdatedBy(authentication.getName());

        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }
    
    public PaginatedResponse<RoleResponse> getRolePaginated(Pageable pageable, RoleFilterRequest filter){
        Specification<Role> spec = (root, query, cb) -> cb.conjunction();
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")
            );
        }
        if (filter.getIsActive() != null) {
            spec = spec.and((root, q, cb) ->
                cb.equal(root.get("isActive"), filter.getIsActive())
            );
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            LocalDateTime start = filter.getStartDate().atStartOfDay();             
            LocalDateTime end = filter.getEndDate().atTime(LocalTime.MAX);

            spec = spec.and((root, q, cb) ->
                cb.between(root.get("createdAt"), start, end)
            );
        }

        Sort sort = Sort.unsorted();
        if (filter.getSortByCreatedAt() != null) {
            sort = sort.and(Sort.by(filter.getSortByCreatedAt() ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt"));
        }
        if (filter.getSortByUpdatedAt() != null) {
            sort = sort.and(Sort.by(filter.getSortByUpdatedAt() ? Sort.Direction.ASC : Sort.Direction.DESC, "updatedAt"));
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Role> rolePage = roleRepository.findAll(spec, sortedPageable);

        List<RoleResponse> roleResponse = rolePage.getContent()
            .stream()
            .map(roleMapper::toRoleResponse)
            .toList();

        return PaginatedResponse.<RoleResponse>builder()
            .pageNumber(rolePage.getNumber() + 1)
            .pageSize(rolePage.getSize())
            .totalPages(rolePage.getTotalPages())
            .data(roleResponse)
            .build();
    }

    public RoleResponse getSingleRole(String id){
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));

        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse deleteRole(String id){
        Role role = roleRepository.findWithPermissionById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        
        if(role.getUser().size() > 0){
            throw new AppException(ErrorCode.RELATED_ROLE);
        }

        if(role.getId().equals(ADMIN_ROLE)){
            throw new AppException(ErrorCode.DELETE_ADMIN_ROLE);
        }

        roleRepository.delete(role);

        return roleMapper.toRoleResponse(role);
    }
}

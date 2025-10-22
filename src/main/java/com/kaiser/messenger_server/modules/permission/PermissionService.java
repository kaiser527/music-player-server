package com.kaiser.messenger_server.modules.permission;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.modules.permission.dto.PermissionFilterRequest;
import com.kaiser.messenger_server.modules.permission.dto.PermissionRequest;
import com.kaiser.messenger_server.modules.permission.dto.PermissionResponse;
import com.kaiser.messenger_server.modules.permission.entity.Permission;
import com.kaiser.messenger_server.utils.PaginatedResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean check = permissionRepository.existsByApiPathAndMethod(request.getApiPath(), request.getMethod());
        if(check){
            throw new AppException(ErrorCode.PERMISSION_EXIST);
        }

        Permission permission = permissionMapper.toCreatePermission(request);
        permission.setCreatedBy(authentication.getName());

        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    public PermissionResponse updatePermission(String id, PermissionRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXIST));

        boolean check = permissionRepository.existsByApiPathAndMethodAndIdNot(request.getApiPath(), request.getMethod(), id);

        if(check){
            throw new AppException(ErrorCode.PERMISSION_EXIST);
        }

        permissionMapper.toUpdatePermission(permission, request);
        permission.setUpdatedBy(authentication.getName());

        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    public PaginatedResponse<PermissionResponse> getPermissionPaginated(Pageable pageable, PermissionFilterRequest filter){
        Specification<Permission> spec = (root, query, cb) -> cb.conjunction();
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")
            );
        }
        if (filter.getApiPath() != null && !filter.getApiPath().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("apiPath")), "%" + filter.getApiPath().toLowerCase() + "%")
            );
        }
        if (filter.getModule() != null && !filter.getModule().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("module")), "%" + filter.getModule().toLowerCase() + "%")
            );
        }
        if (filter.getMethod() != null && !filter.getMethod().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("method")), "%" + filter.getMethod().toLowerCase() + "%")
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

        Page<Permission> permissionPage = permissionRepository.findAll(spec, sortedPageable);

        List<PermissionResponse> permissionResponse = permissionPage.getContent()
            .stream()
            .map(permissionMapper::toPermissionResponse)
            .toList();

        return PaginatedResponse.<PermissionResponse>builder()
            .pageNumber(permissionPage.getNumber() + 1)
            .pageSize(permissionPage.getSize())
            .totalPages(permissionPage.getTotalPages())
            .data(permissionResponse)
            .build();
    }

    public PermissionResponse deletePermission(String id){
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXIST));

        permissionRepository.delete(permission);

        return permissionMapper.toPermissionResponse(permission);
    }
}

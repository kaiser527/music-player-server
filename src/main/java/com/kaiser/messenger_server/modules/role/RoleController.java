package com.kaiser.messenger_server.modules.role;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kaiser.messenger_server.modules.role.dto.RoleFilterRequest;
import com.kaiser.messenger_server.modules.role.dto.RoleRequest;
import com.kaiser.messenger_server.modules.role.dto.RoleResponse;
import com.kaiser.messenger_server.utils.ApiResponse;
import com.kaiser.messenger_server.utils.PaginatedResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/role', 'POST')")
    ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request){
        RoleResponse result = roleService.createRole(request);

        return ApiResponse.<RoleResponse>builder()
            .message("Create role")
            .result(result)
            .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/role/:id', 'PATCH')")
    ApiResponse<RoleResponse> updateRole(@PathVariable("id") String id, @RequestBody @Valid RoleRequest request){
        RoleResponse result = roleService.updateRole(id, request);

        return ApiResponse.<RoleResponse>builder()
            .message("Update role")
            .result(result)
            .build();
    }

    @GetMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/role', 'GET')")
    ApiResponse<PaginatedResponse<RoleResponse>> getRolePaginated(@RequestParam int pageSize, @RequestParam int pageNumber, @ModelAttribute RoleFilterRequest filter){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PaginatedResponse<RoleResponse> result = roleService.getRolePaginated(pageable, filter);

        return ApiResponse.<PaginatedResponse<RoleResponse>>builder()
            .message("Fetch role paginate")
            .result(result)
            .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/role/:id', 'GET')")
    ApiResponse<RoleResponse> getSingleRole(@PathVariable("id") String id){
        RoleResponse result = roleService.getSingleRole(id);

        return ApiResponse.<RoleResponse>builder()
            .message("Fetch role by id")
            .result(result)
            .build(); 
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/role/:id', 'DELETE')")
    ApiResponse<RoleResponse> deleteRole(@PathVariable("id") String id){
        RoleResponse result = roleService.deleteRole(id);

        return ApiResponse.<RoleResponse>builder()
            .message("Delete role")
            .result(result)
            .build();
    }
}

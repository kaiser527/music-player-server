package com.kaiser.messenger_server.controllers;

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
import com.kaiser.messenger_server.dto.request.PermissionFilterRequest;
import com.kaiser.messenger_server.dto.request.PermissionRequest;
import com.kaiser.messenger_server.dto.response.ApiResponse;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.PermissionResponse;
import com.kaiser.messenger_server.services.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("permission")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/permission', 'POST')")
    ApiResponse<PermissionResponse> createPermission(@RequestBody @Valid PermissionRequest request){
        PermissionResponse result = permissionService.createPermission(request);

        return ApiResponse.<PermissionResponse>builder()
            .message("Create permission")
            .result(result)
            .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/permission/:id', 'PATCH')")
    ApiResponse<PermissionResponse> updatePermission(@PathVariable("id") String id, @RequestBody @Valid PermissionRequest request ){
        PermissionResponse result = permissionService.updatePermission(id, request);

        return ApiResponse.<PermissionResponse>builder()
            .message("Update permission")
            .result(result)
            .build();
    }

    @GetMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/permission', 'GET')")
    ApiResponse<PaginatedResponse<PermissionResponse>> getPermissionPaginated(@RequestParam int pageSize, @RequestParam int pageNumber, @ModelAttribute PermissionFilterRequest filter){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PaginatedResponse<PermissionResponse> result = permissionService.getPermissionPaginated(pageable, filter);

        return ApiResponse.<PaginatedResponse<PermissionResponse>>builder()
            .message("Fetch permission paginate")
            .result(result)
            .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/permission/:id', 'DELETE')")
    ApiResponse<PermissionResponse> deletePermission(@PathVariable("id") String id){
        PermissionResponse result = permissionService.deletePermission(id);

        return ApiResponse.<PermissionResponse>builder()
            .message("Delete permission")
            .result(result)
            .build();
    }
}

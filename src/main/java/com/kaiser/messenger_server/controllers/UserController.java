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
import com.kaiser.messenger_server.dto.request.user.CreateUserRequest;
import com.kaiser.messenger_server.dto.request.user.UpdateUserRequest;
import com.kaiser.messenger_server.dto.request.user.UserFilterRequest;
import com.kaiser.messenger_server.dto.response.share.ApiResponse;
import com.kaiser.messenger_server.dto.response.share.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.user.UserResponse;
import com.kaiser.messenger_server.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/user', 'POST')")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request){
        return ApiResponse.<UserResponse>builder()
            .message("Create user")
            .result(userService.createUser(request))
            .build();
    }

    @GetMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/user', 'GET')")
    ApiResponse<PaginatedResponse<UserResponse>> getUserPaginated(@RequestParam int pageSize, @RequestParam int pageNumber,  @ModelAttribute UserFilterRequest filter){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        return ApiResponse.<PaginatedResponse<UserResponse>>builder()
            .message("Fetch user paginate")
            .result(userService.getUserPaginated(pageable, filter))
            .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/user/:id', 'PATCH')")
    ApiResponse<UserResponse> updateUser(@PathVariable("id") String id, @RequestBody @Valid UpdateUserRequest request){
        return ApiResponse.<UserResponse>builder()
            .message("Update user")
            .result(userService.updateUser(id, request))
            .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/user/:id', 'DELETE')")
    ApiResponse<UserResponse> deleteUser(@PathVariable("id") String id){
        return ApiResponse.<UserResponse>builder()
            .message("Delete user")
            .result(userService.deleteUser(id))
            .build();
    }

    @GetMapping("/artist")
    ApiResponse<PaginatedResponse<UserResponse>> getArtistPaginate(@RequestParam int pageSize, @RequestParam int pageNumber, @RequestParam String username){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        return ApiResponse.<PaginatedResponse<UserResponse>>builder()
            .message("Fetch artist paginate")
            .result(userService.getArtistPaginate(pageable, username))
            .build();
    }
}

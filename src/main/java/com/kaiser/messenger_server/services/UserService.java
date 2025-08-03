package com.kaiser.messenger_server.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.CreateUserRequest;
import com.kaiser.messenger_server.dto.request.UpdateUserRequest;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.UserResponse;
import com.kaiser.messenger_server.entities.Role;
import com.kaiser.messenger_server.entities.User;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.mapper.UserMapper;
import com.kaiser.messenger_server.repositories.RoleRepository;
import com.kaiser.messenger_server.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${role.admin}")
    String ADMIN_ROLE;

    public UserResponse createUser(CreateUserRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        User user = userMapper.toCreateUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setCreatedBy(authentication.getName());
        user.setRole(role);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public PaginatedResponse<UserResponse> getUserPaginated(Pageable pageable){
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> userResponse = userPage.getContent()
            .stream()
            .map(userMapper::toUserResponse)
            .toList();

        return PaginatedResponse.<UserResponse>builder()
            .pageNumber(userPage.getNumber() + 1)
            .pageSize(userPage.getSize())
            .totalPages(userPage.getTotalPages())
            .data(userResponse)
            .build();
    }

    public UserResponse updateUser(String id, UpdateUserRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        userMapper.toUpdateUser(user, request);
        
        user.setUpdatedBy(authentication.getName());
        user.setRole(role);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public UserResponse deleteUser(String id){
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if(user.getRole().getId().equals(ADMIN_ROLE)){
            throw new AppException(ErrorCode.DELETE_ADMIN_USER);
        }

        userRepository.delete(user);

        return userMapper.toUserResponse(user);
    }

    public PaginatedResponse<UserResponse> getArtistPaginate(Pageable pageable, String username){
        Role role = roleRepository.findByName("ARTIST").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        Page<User> userPage = userRepository.findByRoleAndUsernameContainingIgnoreCase(role, username, pageable);     

        List<UserResponse> userResponse = userPage.getContent()
            .stream()
            .map(userMapper::toUserResponse)
            .toList();

        return PaginatedResponse.<UserResponse>builder()
            .pageNumber(userPage.getNumber() + 1)
            .pageSize(userPage.getSize())
            .totalPages(userPage.getTotalPages())
            .data(userResponse)
            .build();
    }
}

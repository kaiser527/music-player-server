package com.kaiser.messenger_server.configuration;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.kaiser.messenger_server.entities.User;
import com.kaiser.messenger_server.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null || user.getRole() == null || user.getRole().getIsActive() == false) {
            return false;
        }

        return user.getRole().getPermission().stream()
            .anyMatch(p -> 
                p.getApiPath().equals(targetType) && 
                p.getMethod().toString().equals(permission.toString())
            );
    }

    public boolean hasPermission(String apiPath, String method) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return hasPermission(authentication, null, apiPath, method);
    }
}
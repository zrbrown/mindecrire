package net.eightlives.mindy.config;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;

@Component
public class UserAuthorizationPermissionEvaluator implements PermissionEvaluator {

    private UserAuthorizationConfig userAuthorizationConfig;

    public UserAuthorizationPermissionEvaluator(UserAuthorizationConfig userAuthorizationConfig) {
        this.userAuthorizationConfig = userAuthorizationConfig;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return userAuthorizationConfig.getUserPermissions()
                .getOrDefault(authentication.getName(), Collections.emptyList())
                .contains(permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String targetType, Object permission) {
        return false;
    }
}

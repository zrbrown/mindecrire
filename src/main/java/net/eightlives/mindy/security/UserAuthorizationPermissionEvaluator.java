package net.eightlives.mindy.security;

import net.eightlives.mindy.config.custom.UserAuthorizationConfig;
import net.eightlives.mindy.security.Permission;
import net.eightlives.mindy.service.PostService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;

@Component
public class UserAuthorizationPermissionEvaluator implements PermissionEvaluator {

    private UserAuthorizationConfig userAuthorizationConfig;
    private PostService postService;

    public UserAuthorizationPermissionEvaluator(UserAuthorizationConfig userAuthorizationConfig,
                                                PostService postService) {
        this.userAuthorizationConfig = userAuthorizationConfig;
        this.postService = postService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        boolean hasPermission = hasPermission(authentication, (Permission) permission);

        if (targetDomainObject instanceof String) {
            hasPermission = hasPermission && ownsPost((String) targetDomainObject, authentication.getName());
        }

        return hasPermission;
    }

    private boolean hasPermission(Authentication authentication, Permission permission) {
        return userAuthorizationConfig.getUserPermissions()
                .getOrDefault(authentication.getName(), Collections.emptyList())
                .contains(permission);
    }

    private boolean ownsPost(String postUrlName, String username) {
        return postService.getPostByUrlName(postUrlName).map(target ->
                target.getAuthorDetails().getAuthor().equals(username))
                .orElse(false);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String targetType, Object permission) {
        return false;
    }
}

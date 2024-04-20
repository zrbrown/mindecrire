package net.eightlives.mindecrire.security;

import net.eightlives.mindecrire.config.custom.UserAuthorizationConfig;
import net.eightlives.mindecrire.service.PostService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
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
        String username = ((OAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getAttribute("login");

        if (username == null) {
            return false;
        }

        boolean hasPermission = hasPermission(username, (Permission) permission);

        if (targetDomainObject instanceof String) {
            hasPermission = hasPermission && ownsPost((String) targetDomainObject, username);
        }

        return hasPermission;
    }

    private boolean hasPermission(String username, Permission permission) {
        return userAuthorizationConfig.getUserPermissions()
                .getOrDefault(username, Collections.emptyList())
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

package net.eightlives.mindecrire.config;

import net.eightlives.mindecrire.config.custom.UserAuthorizationConfig;
import net.eightlives.mindecrire.security.UserAuthorizationPermissionEvaluator;
import net.eightlives.mindecrire.service.PostService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class AuthorizationConfig {

    private final UserAuthorizationConfig userAuthorizationConfig;
    private final PostService postService;

    public AuthorizationConfig(UserAuthorizationConfig userAuthorizationConfig, PostService postService) {
        this.userAuthorizationConfig = userAuthorizationConfig;
        this.postService = postService;
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new UserAuthorizationPermissionEvaluator(
                userAuthorizationConfig, postService
        ));
        return expressionHandler;
    }
}

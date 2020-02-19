package net.eightlives.mindy.annotation;

import net.eightlives.friendlyssl.annotation.FriendlySSL;
import net.eightlives.mindy.config.AuthorizationConfig;
import net.eightlives.mindy.config.S3ImageBucketConfig;
import net.eightlives.mindy.config.SecurityConfig;
import net.eightlives.mindy.config.TomcatConfig;
import net.eightlives.mindy.config.custom.BaseConfig;
import net.eightlives.mindy.config.custom.ImageBucketConfig;
import net.eightlives.mindy.config.custom.StaticContentConfig;
import net.eightlives.mindy.config.custom.UserAuthorizationConfig;
import net.eightlives.mindy.controller.*;
import net.eightlives.mindy.endpointextension.RefreshEndpointExtension;
import net.eightlives.mindy.security.UserAuthorizationPermissionEvaluator;
import net.eightlives.mindy.service.AuthorDetailsService;
import net.eightlives.mindy.service.PostService;
import net.eightlives.mindy.service.PostUpdateService;
import net.eightlives.mindy.service.TagService;
import net.eightlives.mindy.theme.CssAttributeProviderFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        AuthorizationConfig.class,
        S3ImageBucketConfig.class,
        SecurityConfig.class,
        TomcatConfig.class,
        BaseConfig.class,
        ImageBucketConfig.class,
        StaticContentConfig.class,
        UserAuthorizationConfig.class,
        BlogController.class,
        CustomErrorController.class,
        ImageController.class,
        RefreshController.class,
        RootController.class,
        StaticContentController.class,
        AuthorDetailsService.class,
        PostService.class,
        PostUpdateService.class,
        TagService.class,
        RefreshEndpointExtension.class,
        UserAuthorizationPermissionEvaluator.class,
        CssAttributeProviderFactory.class
})
@EntityScan(basePackages = {"net.eightlives.mindy.dao.model"})
@EnableJpaRepositories(basePackages = {"net.eightlives.mindy.dao"})
@EnableConfigServer
@FriendlySSL
public @interface MindyApp {
}

package net.eightlives.mindecrire.annotation;

import net.eightlives.friendlyssl.annotation.FriendlySSL;
import net.eightlives.mindecrire.config.AuthorizationConfig;
import net.eightlives.mindecrire.config.S3ImageBucketConfig;
import net.eightlives.mindecrire.config.SecurityConfig;
import net.eightlives.mindecrire.config.TomcatConfig;
import net.eightlives.mindecrire.config.custom.BaseConfig;
import net.eightlives.mindecrire.config.custom.ImageBucketConfig;
import net.eightlives.mindecrire.config.custom.StaticContentConfig;
import net.eightlives.mindecrire.config.custom.UserAuthorizationConfig;
import net.eightlives.mindecrire.controller.*;
import net.eightlives.mindecrire.endpointextension.RefreshEndpointExtension;
import net.eightlives.mindecrire.security.UserAuthorizationPermissionEvaluator;
import net.eightlives.mindecrire.service.AuthorDetailsService;
import net.eightlives.mindecrire.service.PostService;
import net.eightlives.mindecrire.service.PostUpdateService;
import net.eightlives.mindecrire.service.TagService;
import net.eightlives.mindecrire.theme.CssAttributeProviderFactory;
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
@EntityScan(basePackages = {"net.eightlives.mindecrire.dao.model"})
@EnableJpaRepositories(basePackages = {"net.eightlives.mindecrire.dao"})
@EnableConfigServer
@FriendlySSL
public @interface MindecrireApp {
}

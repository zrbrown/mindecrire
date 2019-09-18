package net.eightlives.mindy.annotation;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(basePackages = {
        "net.eightlives.mindy.config",
        "net.eightlives.mindy.controller",
        "net.eightlives.mindy.service"})
@EntityScan(basePackages = {"net.eightlives.mindy.dao.model"})
@EnableJpaRepositories(basePackages = {"net.eightlives.mindy.dao"})
@EnableConfigServer
public @interface MindyApp {
}

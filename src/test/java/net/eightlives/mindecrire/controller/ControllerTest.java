package net.eightlives.mindecrire.controller;

import net.eightlives.mindecrire.annotation.MindecrireApp;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        classes = ControllerTest.TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class ControllerTest {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @SpringBootApplication
    @MindecrireApp
    static class TestApp {
    }

    @Autowired
    MockMvc mvc;

    @MockBean
    S3Client s3Client;

    @BeforeAll
    static void beforeAll() {
        POSTGRES.start();
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("filesystem:" + Path.of("src", "main", "resources", "sql"))
                .createSchemas(true)
                .schemas("application")
                .load()
                .migrate();
    }

    @AfterAll
    static void afterAll() {
        POSTGRES.stop();
    }

    static Authentication getOauthAuthenticationFor(OAuth2User principal) {
        return new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "github");
    }

    static OAuth2User createOAuth2User(String subject, String name, String login) {
        Map<String, Object> authorityAttributes = new HashMap<>();
        authorityAttributes.put("key", "value");

        GrantedAuthority authority = new OAuth2UserAuthority(authorityAttributes);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", subject);
        attributes.put("name", name);
        attributes.put("login", login);

        return new DefaultOAuth2User(List.of(authority), attributes, "sub");
    }

    static OAuth2User createInvalidOAuth2User(String subject, String name, Integer login) {
        Map<String, Object> authorityAttributes = new HashMap<>();
        authorityAttributes.put("key", "value");

        GrantedAuthority authority = new OAuth2UserAuthority(authorityAttributes);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", subject);
        attributes.put("name", name);
        attributes.put("login", login);

        return new DefaultOAuth2User(List.of(authority), attributes, "sub");
    }
}

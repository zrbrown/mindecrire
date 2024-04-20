package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityTest extends ControllerTest {

    @DisplayName("POST /blog/add when OAuth token does not have a login")
    @Test
    void nullLogin() throws Exception {
        mvc.perform(post("/blog/add")
                        .with(authentication(getOauthAuthenticationFor(createOAuth2User("otherzack", null, null))))
                        .with(csrf())
                        .param("postTitle", "New Test Post!")
                        .param("postContent", "Stuff about stuff."))
                .andExpect(status().isForbidden());
    }

    @DisplayName("POST /blog/add when OAuth token does not have a login of type String")
    @Test
    void nonStringLogin() throws Exception {
        mvc.perform(post("/blog/add")
                        .with(authentication(getOauthAuthenticationFor(createInvalidOAuth2User("otherzack", null, 12345))))
                        .with(csrf())
                        .param("postTitle", "New Test Post!")
                        .param("postContent", "Stuff about stuff."))
                .andExpect(status().isForbidden());
    }
}

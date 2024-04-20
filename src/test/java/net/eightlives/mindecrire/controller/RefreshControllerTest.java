package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RefreshControllerTest extends ControllerTest {

    @DisplayName("GET /refresh unauthenticated")
    @Test
    void handleRefreshUnauthenticated() throws Exception {
        mvc.perform(get("/refresh"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/oauth2/authorization/github"));
    }

    @DisplayName("GET /refresh unauthorized")
    @Test
    void handleRefreshUnauthorized() throws Exception {
        mvc.perform(get("/refresh")
                        .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-admin")))))
                .andExpect(status().isForbidden());
    }

    @DisplayName("GET /refresh")
    @Test
    void handleRefresh() throws Exception {
        mvc.perform(get("/refresh")
                        .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "admin")))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/refresh"));
    }
}

package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.endpoint.RefreshEndpoint;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RefreshEndpointExtensionTest extends ControllerTest {

    @MockBean
    RefreshEndpoint refreshEndpoint;

    @DisplayName("GET /actuator/refresh unauthenticated")
    @Test
    void handleRefreshUnauthenticated() throws Exception {
        mvc.perform(post("/actuator/refresh"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/oauth2/authorization/github"));
    }

    @DisplayName("GET /actuator/refresh unauthorized")
    @Test
    void handleRefreshUnauthorized() throws Exception {
        mvc.perform(post("/actuator/refresh")
                        .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-admin")))))
                .andExpect(status().isForbidden());
    }

    @DisplayName("GET /actuator/refresh")
    @Test
    void handleRefresh() throws Exception {
        mvc.perform(post("/actuator/refresh")
                        .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "admin")))))
                .andExpect(status().isOk());

        verify(refreshEndpoint, times(1)).refresh();
    }
}

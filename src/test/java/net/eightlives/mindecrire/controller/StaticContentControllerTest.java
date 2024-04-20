package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StaticContentControllerTest extends ControllerTest {

    @MockBean
    Clock clock;

    @DisplayName("GET /test-content when staticContent.markdownToName does not contain a mapping for requested page")
    @Test
    void staticContentNoPageConfig() throws Exception {
        mvc.perform(get("/test-content"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Page not found"));
    }

    @DisplayName("GET /not-exist when staticContent.markdownToName page does not have a matching .md file")
    @Test
    void staticContentNoMarkdown() throws Exception {
        mvc.perform(get("/not-exist"))
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason("Server misconfigured"));
    }

    @DisplayName("GET /projects and /about successfully")
    @Test
    void staticContent() throws Exception {
        mvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("static_content"))
                .andExpect(model().attribute("title", "My Projects"))
                .andExpect(model().attribute("content", "<h1 class=\"mindecrire-md-heading\">Projects</h1>\n" +
                        "<h2 class=\"mindecrire-md-heading\">Cool Thing</h2>\n" +
                        "<p class=\"mindecrire-md-paragraph\">This is a cool thing I made</p>\n"));

        mvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("static_content"))
                .andExpect(model().attribute("title", "About"))
                .andExpect(model().attribute("content", "<h1 class=\"mindecrire-md-heading\">About</h1>\n" +
                        "<p class=\"mindecrire-md-paragraph\"><em class=\"mindecrire-md-emphasis\">I make things</em></p>\n" +
                        "<p class=\"mindecrire-md-paragraph\"><strong class=\"mindecrire-md-strong-emphasis\">Lots of things</strong></p>\n"));
    }
}

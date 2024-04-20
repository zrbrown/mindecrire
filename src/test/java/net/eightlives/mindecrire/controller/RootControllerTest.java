package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class RootControllerTest extends ControllerTest {

    @DisplayName("GET /")
    @Test
    void handleRequest() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/blog"));
    }
}

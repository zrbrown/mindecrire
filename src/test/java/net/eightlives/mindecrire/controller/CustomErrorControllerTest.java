package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CustomErrorControllerTest extends ControllerTest {

    @DisplayName("GET /error")
    @Test
    void handleError() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("errorMessage", "404 Not Found"));
    }

    @DisplayName("GET /error when error status code is not an integer")
    @Test
    void handleErrorStatusNotInt() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, "Not an integer"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("errorMessage", "An error occurred"));
    }

    @DisplayName("GET /error when error status code is not a valid HTTP status code")
    @Test
    void handleErrorStatusInvalid() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 12345))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("errorMessage", "An error occurred"));
    }

    @DisplayName("GET /error when URI not ending with /actuator/refresh is called with the wrong method (not POST)")
    @Test
    void handleErrorCurrentUriMethodNotAllowed() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 405)
                        .requestAttr("currentUri", "https://localhost/actuator/notrefresh"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("errorMessage", "405 Method Not Allowed"));
    }

    @DisplayName("GET /error when URI ending with /actuator/refresh is called with an error other than 405")
    @Test
    void handleErrorRefreshOtherError() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr("currentUri", "https://localhost/actuator/refresh"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("errorMessage", "404 Not Found"));
    }

    @DisplayName("GET /error when URI ending with /actuator/refresh is called with the wrong method (not POST)")
    @Test
    void handleErrorRefreshMethodNotAllowed() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 405)
                        .requestAttr("currentUri", "https://localhost/actuator/refresh"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/refresh"));
    }
}

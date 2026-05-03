package net.eightlives.mindecrire.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        try {
            if (status instanceof Integer code) {
                Object currentUri = request.getAttribute("currentUri");
                if (currentUri != null && currentUri.toString().endsWith("/actuator/refresh") &&
                        code == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                    return "redirect:/refresh";
                }

                model.addAttribute("errorMessage", status + " " +
                        HttpStatus.valueOf(code).getReasonPhrase());
            } else {
                model.addAttribute("errorMessage", "An error occurred");
            }
        } catch (IllegalArgumentException e) {
            LOG.error("HttpStatus {} does not exist", status, e);
            model.addAttribute("errorMessage", "An error occurred");
        }


        return "error/error";
    }
}

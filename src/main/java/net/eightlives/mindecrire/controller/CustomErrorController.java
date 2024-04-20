package net.eightlives.mindecrire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        try {
            if (status instanceof Integer) {
                Object currentUri = request.getAttribute("currentUri");
                if (currentUri != null && currentUri.toString().endsWith("/actuator/refresh") &&
                        (Integer) status == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                    return "redirect:/refresh";
                }

                model.addAttribute("errorMessage", status.toString() + " " +
                        HttpStatus.valueOf((Integer) status).getReasonPhrase());
            } else {
                model.addAttribute("errorMessage", "An error occurred");
            }
        } catch (IllegalArgumentException e) {
            LOG.error("HttpStatus " + status + " does not exist", e);
            model.addAttribute("errorMessage", "An error occurred");
        }


        return "error/error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

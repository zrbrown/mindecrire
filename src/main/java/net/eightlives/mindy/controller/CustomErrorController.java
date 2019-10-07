package net.eightlives.mindy.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        try {
            if (status instanceof Integer) {
                model.addAttribute("errorMessage", status.toString() + " " +
                        HttpStatus.valueOf((Integer) status).getReasonPhrase());
            } else {
                model.addAttribute("errorMessage", "An error occurred");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "An error occurred");
        }


        return "error/error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

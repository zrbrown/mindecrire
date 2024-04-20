package net.eightlives.mindy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {

    @RequestMapping
    public String request() {
        return "redirect:/blog";
    }
}

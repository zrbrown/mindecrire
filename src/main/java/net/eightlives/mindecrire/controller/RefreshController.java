package net.eightlives.mindecrire.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/refresh")
public class RefreshController {

    @GetMapping
    @PreAuthorize("hasPermission(null, T(net.eightlives.mindecrire.security.Permission).ADMIN)")
    public String handleRefresh() {
        return "admin/refresh";
    }
}

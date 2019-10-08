package net.eightlives.mindy.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/refresh")
public class RefreshController {

    @GetMapping
    @PreAuthorize("hasPermission(null, T(net.eightlives.mindy.security.Permission).ADMIN)")
    public String handleRefresh() {
        return "admin/refresh";
    }
}

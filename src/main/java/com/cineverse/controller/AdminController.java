package com.cineverse.controller;

import com.cineverse.dto.DashboardResponse;
import com.cineverse.dto.LoginRequest;
import com.cineverse.dto.LoginResponse;
import com.cineverse.service.AuthService;
import com.cineverse.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final DashboardService dashboardService;

    public AdminController(AuthService authService, DashboardService dashboardService) {
        this.authService = authService;
        this.dashboardService = dashboardService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.gerar();
    }
}

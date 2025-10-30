package com.is.biblioteca.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth0")
public class Auth0Controller {
    
    @GetMapping("/home")
    public String home() {
        return "auth0/home"; // templates/auth0/home.html
    }
}
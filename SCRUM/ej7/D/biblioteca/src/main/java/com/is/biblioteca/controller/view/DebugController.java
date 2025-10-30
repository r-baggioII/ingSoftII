package com.is.biblioteca.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/debug")
public class DebugController {
    
    @GetMapping("/session")
    public String debugSession() {
        return "debug-session.html";
    }
}

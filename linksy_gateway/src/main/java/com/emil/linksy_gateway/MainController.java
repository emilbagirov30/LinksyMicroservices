package com.emil.linksy_gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {
    @GetMapping("/privacy")
    public String privacyPolicy() {
        return "privacy-policy";
    }
}
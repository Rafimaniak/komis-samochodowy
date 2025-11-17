package pl.komis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Komis Samochodowy API działa! 🚗";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint działa poprawnie!";
    }

    @GetMapping("/status")
    public String status() {
        return "Aplikacja jest uruchomiona - " + new java.util.Date();
    }
}
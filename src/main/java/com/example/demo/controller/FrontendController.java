package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FrontendController {
    @GetMapping(value = "/{path:[^.]*}")
    public String redirect(@PathVariable String path) {
        return "forward:/index.html";
    }
}

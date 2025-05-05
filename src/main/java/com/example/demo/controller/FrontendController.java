package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FrontendController {
    @GetMapping(value = "/{path:[^.]*}")
    public String redirect(@PathVariable String path) {
        return "forward:/index.html";
    }
}

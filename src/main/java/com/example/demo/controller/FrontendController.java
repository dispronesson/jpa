package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
    @RequestMapping(value = "/{path:[^.]*}")
    public String redirect(@PathVariable String path) {
        return "forward:/index.html";
    }
}

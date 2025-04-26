package com.example.demo.controller;

import com.example.demo.aspect.CountAspect;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/count")
@RequiredArgsConstructor
@Tag(name = "Count", description = "Count of site visits")
public class CountController {
    private final CountAspect countAspect;

    @GetMapping
    public String getCount() {
        return "Count of site visits: " + countAspect.getCounter();
    }
}

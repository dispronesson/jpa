package com.example.demo.controller;

import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.service.LogService;
import com.example.demo.util.DateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@Tag(name = "Logs", description = "Interaction with logs")
public class LogController {
    private final LogService logService;

    @GetMapping("/{date}")
    @Operation(summary = "Getting a new log ID")
    public String getNewLogId(
            @PathVariable @Parameter(description = "Log date", example = "2025-04-07")
            String date
    ) throws IOException, InterruptedException {
        if (!DateValidator.isValidDate(date)) {
            throw new InvalidArgumentsException("Invalid date format. "
                    + "Required in the form of 'yyyy-MM-dd'");
        }

        Long logId = logService.getNewLogId(date);

        return "The log file is being generated. ID: " + logId;
    }

    @GetMapping("/status/{logId}")
    @Operation(summary = "Check log file status")
    public String getLogStatus(@PathVariable Long logId) {
        return logService.getLogStatus(logId);
    }

    @GetMapping("/download/{logId}")
    @Operation(summary = "Download log file")
    public ResponseEntity<Resource> getLog(@PathVariable Long logId) {
        String logFilePath = logService.getLog(logId);
        Resource resource = new FileSystemResource(logFilePath);
        return ResponseEntity.ok().contentType(MediaType.valueOf("text/plain; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + logFilePath.substring(5) + "\"").body(resource);
    }
}
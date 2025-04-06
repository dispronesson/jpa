package com.example.demo.controller;

import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.util.DateValidator;
import com.example.demo.util.LogFileService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/logs")
public class LogController {
    private static final String LOG_FILE_PATH = "logs/app.lo";

    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogFile(@PathVariable String date) throws IOException {
        if (!DateValidator.isValidDate(date)) {
            throw new InvalidArgumentsException("Invalid date format. "
                    + "Required in the form of 'yyyy-mm-dd'");
        }

        List<String> logs = LogFileService.getLogsByDate(LOG_FILE_PATH, date);
        if (logs.isEmpty()) {
            throw new NotFoundException("No logs found for the date: " + date);
        }

        Path logFilePath = LogFileService.createLogFile(logs, date);

        Resource resource = new FileSystemResource(logFilePath);

        return ResponseEntity.ok().contentType(MediaType.valueOf("text/plain; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + date + ".log\"").body(resource);
    }
}
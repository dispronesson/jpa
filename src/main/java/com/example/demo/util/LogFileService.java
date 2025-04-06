package com.example.demo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogFileService {
    public static List<String> getLogsByDate(String logFilePath, String date) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(logFilePath))) {
            return stream.filter(line -> line.startsWith(date)).collect(Collectors.toList());
        }
    }

    public static Path createLogFile(List<String> logs, String date) throws IOException {
        Path logFilePath = Paths.get("logs/" + date + ".log");
        Files.write(logFilePath, logs);
        return logFilePath;
    }
}

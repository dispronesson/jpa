package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogAsyncService {
    private static final String LOG_FILE_PATH = "logs/app.log";

    @Async
    public void createLogFile(Long logId, String date, ConcurrentMap<Long, String> logs)
            throws IOException, InterruptedException {
        List<String> log;
        try (Stream<String> stream = Files.lines(Paths.get(LOG_FILE_PATH))) {
            log = stream.filter(line -> line.startsWith(date)).toList();
        }

        String uniqueFileName = "logs/" + date + "-" + logId + ".log";
        Path logFilePath = Paths.get(uniqueFileName);
        Files.write(logFilePath, log);

        Thread.sleep(20000);

        logs.put(logId, logFilePath.toString());
    }
}

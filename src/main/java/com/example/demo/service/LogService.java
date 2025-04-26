package com.example.demo.service;

import com.example.demo.exception.InvalidArgumentsException;
import com.example.demo.exception.NotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final ConcurrentHashMap<Long, String> logs = new ConcurrentHashMap<>();
    private final LogAsyncService logAsyncService;
    private Long logId = 0L;

    public Long getNewLogId(String date) throws IOException, InterruptedException {
        logAsyncService.createLogFile(++logId, date, logs);
        return logId;
    }

    public String getLogStatus(Long logId) {
        if (logId <= 0) {
            throw new InvalidArgumentsException("Invalid log id");
        }

        if (!logs.containsKey(logId)) {
            if (logId > this.logId) {
                throw new NotFoundException("Log file with id " + logId + " not found");
            } else {
                return "Log file with id " + logId + " is not ready";
            }
        } else {
            return "Log file with id " + logId + " is ready to download";
        }
    }

    public String getLog(Long logId) {
        if (logId <= 0) {
            throw new InvalidArgumentsException("Invalid logId");
        }

        if (!logs.containsKey(logId)) {
            throw new NotFoundException("Log file with id " + logId + " not found");
        } else {
            return logs.get(logId);
        }
    }
}

package com.example.sto.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingNotifier implements Notifier {
    private final Logger log = LoggerFactory.getLogger(LoggingNotifier.class);

    @Override
    public void notifyClient(Long clientId, String message) {
        log.info("Notifying client " + clientId + ": " + message);
    }
}

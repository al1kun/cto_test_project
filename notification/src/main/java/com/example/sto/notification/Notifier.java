package com.example.sto.notification;

public interface Notifier {
    void notifyClient(Long clientId, String message);
}

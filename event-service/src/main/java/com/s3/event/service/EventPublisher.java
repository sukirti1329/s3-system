package com.s3.event.service;

public interface EventPublisher<T> {
    void publish(String topic, String key, T event);
}
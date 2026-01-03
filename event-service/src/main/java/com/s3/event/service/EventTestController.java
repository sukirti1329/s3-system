package com.s3.event.service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test/events")
public class EventTestController {

    private final EventProducer producer;

    public EventTestController(EventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public void publish() {
        Map<String, Object> payload = Map.of(
                "objectId", "obj-123",
                "bucketName", "demo-bucket",
                "action", "UPLOAD"
        );

        producer.send("object", "obj-123", payload);
    }
}

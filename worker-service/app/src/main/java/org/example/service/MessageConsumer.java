package org.example.service;


import com.hazelcast.map.IMap;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MessageConsumer {

    private final HazelCastService hazelCastService;

    public MessageConsumer(final HazelCastService hazelCastService) {
        this.hazelCastService = hazelCastService;
    }

    @RabbitListener(queues = "microservice-queue")
    public void receive(String message) {
        System.out.println("Received msg: " + message);
        processFile(message);
    }

    private void processFile(String filename) {

        IMap<String, String> fileMetadataCache = hazelCastService.getMetaDataCache();
        String metadataStr = fileMetadataCache.get(filename);

        if (metadataStr != null) {
            System.out.println("MetaData found: " + metadataStr);
        } else {
            System.out.println("Metadata not found: " + filename);
        }

        File file = new File("/mnt/data/" + filename);
        if (file.exists()) {
            System.out.println("File found! Processing...");
            try {
                System.out.println("File contents: \n" + FileUtils.readFileToString(file, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("File not found!");
        }
    }
}

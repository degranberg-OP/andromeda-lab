package org.example.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.map.IMap;
import org.example.model.FileMetaData;
import org.example.service.HazelCastService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final String EXCHANGE_NAME = "microservice-exchange";
    private static final String ROUTING_KEY = "microservice-routing-key";

    private final RabbitTemplate rabbitTemplate;
    private final HazelCastService hazelCastService;
    private final ObjectMapper objectMapper;

    public ApiController(final RabbitTemplate rabbitTemplate,
                         final HazelCastService hazelCastService,
                         final ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.hazelCastService = hazelCastService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/process_data")
    public ResponseEntity<String> processData(@RequestBody final String data) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, data);
        return ResponseEntity.ok("Message sent to: " + EXCHANGE_NAME);
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        final String uploadDir = "/mnt/data/";
        final File destFile = new File(uploadDir + file.getOriginalFilename());

        new File(uploadDir).mkdirs();

        file.transferTo(destFile);
        System.out.println("File saved at: " + destFile.getAbsolutePath());

        saveMetaData(file);
        return "File uploaded";
    }

    private void saveMetaData(final MultipartFile file) throws JsonProcessingException {
        final FileMetaData fileMetaData = new FileMetaData(
                file.getOriginalFilename(),
                file.getSize(),
                LocalDate.now()
        );

        final IMap<String, String> metaDataCache = hazelCastService.getMetaDataCache();
        final String jsonData = objectMapper.writeValueAsString(fileMetaData);
        metaDataCache.put(file.getOriginalFilename(), jsonData);
        System.out.println("Metadata saved: " + jsonData);

    }
}

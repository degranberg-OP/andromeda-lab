package org.example.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class FileMetaDataTest {

    @Test
    void should_serialize_and_deserialize() throws JsonProcessingException {
        final JsonMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

        final FileMetaData fileMetaData = new FileMetaData(
                "filename.txt",
                10,
                LocalDate.EPOCH
        );

        final String s = objectMapper.writeValueAsString(fileMetaData);
        System.out.println(s);

    }

}
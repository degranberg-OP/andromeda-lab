package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetaData {
    public final String fileName;
    public final long fileSize;
    public final LocalDate uploadDate;

    @JsonCreator
    public FileMetaData(@JsonProperty("fileName") final String fileName,
                        @JsonProperty("fileSize") final long fileSize,
                        @JsonProperty("uploadDate") final LocalDate uploadDate) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final FileMetaData that = (FileMetaData) o;
        return fileSize == that.fileSize && Objects.equals(fileName, that.fileName) && Objects.equals(uploadDate, that.uploadDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileSize, uploadDate);
    }
}

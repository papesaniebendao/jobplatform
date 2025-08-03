package com.mycompany.myapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final FileStorage fileStorage = new FileStorage();

    public FileStorage getFileStorage() {
        return fileStorage;
    }

    public static class FileStorage {
        private String cvDir;

        public String getCvDir() {
            return cvDir;
        }

        public void setCvDir(String cvDir) {
            this.cvDir = cvDir;
        }
    }
}

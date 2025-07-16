package com.globalmedia.videometadataservice.exception;

public class ImportJobNotFoundException extends RuntimeException {
    
    public ImportJobNotFoundException(String message) {
        super(message);
    }
    
    public ImportJobNotFoundException(Long id) {
        super("Import job not found with id: " + id);
    }
}
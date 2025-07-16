package com.globalmedia.videomedatadaservice.exception;

public class VideoNotFoundException extends RuntimeException {
    
    public VideoNotFoundException(String message) {
        super(message);
    }
    
    public VideoNotFoundException(Long id) {
        super("Video not found with id: " + id);
    }
}
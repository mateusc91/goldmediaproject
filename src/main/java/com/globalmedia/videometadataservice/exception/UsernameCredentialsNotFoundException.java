package com.globalmedia.videometadataservice.exception;

public class UsernameCredentialsNotFoundException extends RuntimeException {

    public UsernameCredentialsNotFoundException(String message) {
        super(message);
    }

    public static UsernameCredentialsNotFoundException forUsername(String username) {
        return new UsernameCredentialsNotFoundException("User not found with username: " + username);
    }
}

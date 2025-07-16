package com.globalmedia.videomedatadaservice.exception;

public class UsernameCredentialsNotFoundException extends RuntimeException {

    public UsernameCredentialsNotFoundException(String message) {
        super(message);
    }

    public UsernameCredentialsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameCredentialsNotFoundException() {
        super("User credentials not found");
    }

    public static UsernameCredentialsNotFoundException forUsername(String username) {
        return new UsernameCredentialsNotFoundException("User not found with username: " + username);
    }
}

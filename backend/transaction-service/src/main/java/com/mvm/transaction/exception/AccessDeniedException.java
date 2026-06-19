package com.mvm.transaction.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(Long resourceId, Long userId, String resourceType) {
        super("User " + userId + " does not have access to " + resourceType + " " + resourceId);
    }
}
package com.tweetapp.minitwitterbackend.exception;

public class PersistenceException extends Exception {
    private int errorId;

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public PersistenceException(int errorId) {
        this.errorId = errorId;
    }

    public PersistenceException(String message, int errorId) {
        super(message);
        this.errorId = errorId;
    }

    public PersistenceException(Throwable cause, int errorId) {
        super(cause);
        this.errorId = errorId;
    }

}

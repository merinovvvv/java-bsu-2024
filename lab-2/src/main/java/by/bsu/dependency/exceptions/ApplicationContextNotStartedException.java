package by.bsu.dependency.exceptions;

public class ApplicationContextNotStartedException extends RuntimeException {
    public ApplicationContextNotStartedException() {
        super("Application context has not been started or is empty.");
    }
}

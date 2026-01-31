package jdev.kovalev.exception;

public class LinkDataNotPresentException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "This link is not managed by the service";

    public LinkDataNotPresentException() {
        super(DEFAULT_MESSAGE);
    }
}

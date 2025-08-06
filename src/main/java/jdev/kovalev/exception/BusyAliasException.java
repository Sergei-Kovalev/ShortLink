package jdev.kovalev.exception;

public class BusyAliasException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Alias = %s already exists";

    public BusyAliasException(String alias) {
        super(String.format(DEFAULT_MESSAGE, alias));
    }
}

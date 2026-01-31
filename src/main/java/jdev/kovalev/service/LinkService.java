package jdev.kovalev.service;

public interface LinkService {
    String getShortLink(String fullLink, String alias);

    String getFullLink(String alias);
}

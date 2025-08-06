package jdev.kovalev.service.impl;

import jdev.kovalev.entity.LinkData;
import jdev.kovalev.exception.BusyAliasException;
import jdev.kovalev.exception.LinkDataNotPresentException;
import jdev.kovalev.repository.LinkDataRepository;
import jdev.kovalev.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    @Value("${app.host}")
    private String host;

    @Value("${server.port}")
    private int port;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SHORT_URL_LABEL = "http://%s:%s/%s";

    private final LinkDataRepository repository;

    @Override
    @Transactional
    public String getShortLink(String fullLink, String alias) {
        if (alias == null || alias.isEmpty()) {
            alias = generateAlias();
        }

        Optional<LinkData> byAlias = repository.findByAlias(alias);
        if (byAlias.isPresent()) {
            throw new BusyAliasException(alias);
        } else {
            LinkData linkData = LinkData.builder()
                    .alias(alias)
                    .fullLink(fullLink)
                    .build();
            repository.save(linkData);
            return getLinkFromAlias(alias);
        }
    }

    @Override
    public String getFullLink(String alias) {
        LinkData linkData = repository.findByAlias(alias)
                .orElseThrow(LinkDataNotPresentException::new);
        return linkData.getFullLink();
    }

    private static String generateAlias() {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private String getLinkFromAlias(String alias) {
        return String.format(SHORT_URL_LABEL, host, port, alias);
    }
}

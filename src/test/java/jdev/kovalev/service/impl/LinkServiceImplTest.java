package jdev.kovalev.service.impl;

import jdev.kovalev.entity.LinkData;
import jdev.kovalev.exception.BusyAliasException;
import jdev.kovalev.exception.LinkDataNotPresentException;
import jdev.kovalev.repository.LinkDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceImplTest {

    @Mock
    private LinkDataRepository repository;
    @InjectMocks
    private LinkServiceImpl service;

    private String alias;
    private String fullLink;
    private LinkData linkData;

    @BeforeEach
    void setUp() {
        UUID id = UUID.fromString("d6ba1b09-879f-42f0-b0b9-87a52e03e3b4");
        alias = "alias";
        fullLink = "https://buildin.ai/share/b4946710-db3f-4542-93b6-b9ae6f759446";
        linkData = LinkData.builder()
                .id(id)
                .alias(alias)
                .fullLink(fullLink)
                .build();
    }

    @Nested
    class GetShortLinkTest {
        @Test
        void getShortLink_whenAliasIsNull_shouldReturnShortLink() {
            when(repository.findByAlias(any(String.class)))
                    .thenReturn(Optional.empty());
            when(repository.save(any(LinkData.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String actual = service.getShortLink(fullLink, null);

            assertThat(actual)
                    .isNotNull()
                    .startsWith("http://");

            ArgumentCaptor<LinkData> captor = ArgumentCaptor.forClass(LinkData.class);
            verify(repository).save(captor.capture());

            LinkData savedLinkData = captor.getValue();

            assertThat(savedLinkData.getAlias())
                    .isNotNull()
                    .isNotEmpty();

            assertThat(savedLinkData.getFullLink())
                    .isEqualTo(fullLink);

            assertThat(actual)
                    .contains(savedLinkData.getAlias());
        }

        @Test
        void getShortLink_whenAliasIsNotNull_shouldReturnShortLink() {
            when(repository.findByAlias(any(String.class)))
                    .thenReturn(Optional.empty());
            when(repository.save(any(LinkData.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String actual = service.getShortLink(fullLink, alias);

            assertThat(actual)
                    .isNotNull()
                    .startsWith("http://");

            ArgumentCaptor<LinkData> captor = ArgumentCaptor.forClass(LinkData.class);
            verify(repository).save(captor.capture());

            LinkData savedLinkData = captor.getValue();

            assertThat(savedLinkData.getAlias()).isEqualTo(alias);

            assertThat(savedLinkData.getFullLink())
                    .isEqualTo(fullLink);

            assertThat(actual)
                    .contains(savedLinkData.getAlias());
        }

        @Test
        void getShortLink_whenAliasAlreadyPresentInDb_shouldThrowException() {
            when(repository.findByAlias(any(String.class)))
                    .thenReturn(Optional.of(linkData));

            assertThatThrownBy(() -> service.getShortLink(fullLink, alias))
                    .isInstanceOf(BusyAliasException.class)
                    .hasMessageContaining("Alias = %s already exists", alias);
        }
    }

    @Nested
    class GetFullLinkTests {
        @Test
        void getFullLink_whenPresentInDb_shouldReturnFullLink() {
            when(repository.findByAlias(any(String.class)))
                    .thenReturn(Optional.of(linkData));

            String actual = service.getFullLink(alias);

            assertThat(actual).isEqualTo(fullLink);
        }

        @Test
        void getFullLink_whenNotPresentInDb_shouldThrowException() {
            when(repository.findByAlias(any(String.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getFullLink(alias))
                    .isInstanceOf(LinkDataNotPresentException.class)
                    .hasMessageContaining("This link is not managed by the service");
        }
    }
}
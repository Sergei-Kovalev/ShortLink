package jdev.kovalev.controller;

import jdev.kovalev.exception.BusyAliasException;
import jdev.kovalev.exception.LinkDataNotPresentException;
import jdev.kovalev.exception.handler.ControllersExceptionHandler;
import jdev.kovalev.service.LinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class LinkControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LinkService service;
    @InjectMocks
    private LinkController controller;

    private String alias;
    private String fullLink;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ControllersExceptionHandler())
                .build();

        alias = "alias";
        fullLink = "https://buildin.ai/share/b4946710-db3f-4542-93b6-b9ae6f759446";
    }

    @Nested
    class ShortLinkTests {
        @Test
        void getShortLink_whenAliasIsNull_shouldReturnShortLink() throws Exception {
            String shortLink = "http://localhost:8080/1234567890";

            when(service.getShortLink(fullLink, null))
                    .thenReturn(shortLink);

            mockMvc.perform(post("/")
                                    .param("fullLink", fullLink))
                    .andExpect(status().isOk())
                    .andExpect(content().string(shortLink))
                    .andDo(print());
        }

        @Test
        void getShortLink_whenAliasIsNotNull_shouldReturnShortLink() throws Exception {
            String shortLink = "http://localhost:8080/alias";

            when(service.getShortLink(fullLink, alias))
                    .thenReturn(shortLink);

            mockMvc.perform(post("/")
                                    .param("fullLink", fullLink)
                                    .param(alias, alias))
                    .andExpect(status().isOk())
                    .andExpect(content().string(shortLink))
                    .andDo(print());
        }

        @Test
        void getShortLink_whenAliasIsAlreadyPresent_shouldThrowException() throws Exception {
            when(service.getShortLink(fullLink, "alias"))
                    .thenThrow(new BusyAliasException(alias));

            mockMvc.perform(post("/")
                                    .param("fullLink", fullLink)
                                    .param(alias, alias))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value(String.format("Alias = %s already exists", alias)))
                    .andDo(print());
        }
    }

    @Nested
    class RedirectTests {
        @Test
        void redirect_whenAliasPresentInDb_shouldReturnRedirect() throws Exception {
            when(service.getFullLink(alias))
                    .thenReturn(fullLink);

            mockMvc.perform(get("/" + alias))
                    .andExpect(status().isFound())
                    .andExpect(header().string("Location", fullLink));

            verify(service).getFullLink(alias);
        }

        @Test
        void redirect_whenAliasIsNotPresentInDb_shouldThrowException() throws Exception {
            when(service.getFullLink(alias))
                    .thenThrow(new LinkDataNotPresentException());

            mockMvc.perform(get("/" + alias))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value("This link is not managed by the service"))
                    .andDo(print());

            verify(service).getFullLink(alias);
        }
    }
}
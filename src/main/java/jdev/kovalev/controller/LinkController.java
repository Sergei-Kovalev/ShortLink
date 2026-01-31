package jdev.kovalev.controller;

import jdev.kovalev.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class LinkController {

    private final LinkService service;

    @PostMapping
    public ResponseEntity<String> getShortLink(@RequestParam String fullLink,
                               @RequestParam(required = false) String alias) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getShortLink(fullLink, alias));
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", service.getFullLink(alias))
                .build();
    }
}

package com.github.jvanheesch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestResourceAnnotationBehaviorTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    void givenAnEntityWithAssociationNotExported_whenGetToAssociationResource_then404() {
        Book book = createBookWithAuthorAndPublisher();

        RequestEntity<?> associationResourceRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/books/" + book.getId() + "/author"));
        ResponseEntity<?> associationResourceResponse = restTemplate.exchange(associationResourceRequest, Object.class);

        assertThat(associationResourceResponse.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenARepositoryWithQueryMethodNotExported_whenGetToQueryMethodResource_then404() {
        Book book = createBookWithAuthorAndPublisher();

        RequestEntity<?> queryMethodResourceRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/books/search/findAllByAuthorId?authorId=" + book.getAuthor().getId()));
        ResponseEntity<?> queryMethodResourceResponse = restTemplate.exchange(queryMethodResourceRequest, Object.class);

        assertThat(queryMethodResourceResponse.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenARepositoryWithFindByIdNotExported_whenGetToItemResource_then405InsteadOf404() {
        Book book = createBookWithAuthorAndPublisher();

        RequestEntity<?> itemResourceRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/books/" + book.getId()));
        ResponseEntity<?> itemResourceResponse = restTemplate.exchange(itemResourceRequest, Object.class);

        assertThat(itemResourceResponse.getStatusCode())
                .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void givenARepositoryWithFindByIdNotExported_whenPutToItemResource_then2xxInsteadOf4xx() {
        Book book = createBookWithAuthorAndPublisher();

        RequestEntity<?> itemResourceRequest = new RequestEntity<>(book, HttpMethod.PUT, URI.create("http://localhost:" + port + "/books/" + book.getId()));
        ResponseEntity<?> itemResourceResponse = restTemplate.exchange(itemResourceRequest, Object.class);

        assertThat(itemResourceResponse.getStatusCode())
                .matches(HttpStatus::is2xxSuccessful);
    }

    @Test
    void givenARepositoryWithFindByIdNotExported_whenGetToAssociationResource_then2xxInsteadOf4xx() {
        Book book = createBookWithAuthorAndPublisher();

        RequestEntity<?> associationResourceRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/books/" + book.getId() + "/publisher"));
        ResponseEntity<?> associationResourceResponse = restTemplate.exchange(associationResourceRequest, Object.class);

        assertThat(associationResourceResponse.getStatusCode())
                .matches(HttpStatus::is2xxSuccessful);
    }

    private Book createBookWithAuthorAndPublisher() {
        Author author = new Author();
        author.setName("Joshua Bloch");
        author = authorRepository.save(author);

        Publisher publisher = new Publisher();
        publisher.setName("O'Reilly");
        publisher = publisherRepository.save(publisher);

        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor(author);
        book.setPublisher(publisher);
        book = bookRepository.save(book);

        return book;
    }
}

package com.github.jvanheesch;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface BookRepository extends CrudRepository<Book, Long> {
    @RestResource(exported = false)
    @Override
    Optional<Book> findById(Long id);

    @RestResource(exported = false)
    List<Book> findAllByAuthorId(Long authorId);

    @RestResource
    List<Book> findAllByPublisherId(Long publisherId);
}

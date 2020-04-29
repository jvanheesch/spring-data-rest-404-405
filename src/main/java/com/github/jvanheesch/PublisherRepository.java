package com.github.jvanheesch;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PublisherRepository extends CrudRepository<Publisher, Long> {
}

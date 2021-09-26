package com.olimpiadas.www.repository;

import com.olimpiadas.www.domain.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Job entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobRepository extends ReactiveMongoRepository<Job, String> {
    Flux<Job> findAllBy(Pageable pageable);

    @Query("{}")
    Flux<Job> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<Job> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<Job> findOneWithEagerRelationships(String id);
}

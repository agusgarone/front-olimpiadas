package com.olimpiadas.www.repository;

import com.olimpiadas.www.domain.JobHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the JobHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobHistoryRepository extends ReactiveMongoRepository<JobHistory, String> {
    Flux<JobHistory> findAllBy(Pageable pageable);
}

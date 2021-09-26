package com.olimpiadas.www.repository;

import com.olimpiadas.www.domain.Region;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends ReactiveMongoRepository<Region, String> {}

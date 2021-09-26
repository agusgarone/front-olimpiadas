package com.olimpiadas.www.repository;

import com.olimpiadas.www.domain.Country;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Country entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CountryRepository extends ReactiveMongoRepository<Country, String> {}

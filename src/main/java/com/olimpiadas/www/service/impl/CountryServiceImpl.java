package com.olimpiadas.www.service.impl;

import com.olimpiadas.www.domain.Country;
import com.olimpiadas.www.repository.CountryRepository;
import com.olimpiadas.www.service.CountryService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Country}.
 */
@Service
public class CountryServiceImpl implements CountryService {

    private final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Mono<Country> save(Country country) {
        log.debug("Request to save Country : {}", country);
        return countryRepository.save(country);
    }

    @Override
    public Mono<Country> partialUpdate(Country country) {
        log.debug("Request to partially update Country : {}", country);

        return countryRepository
            .findById(country.getId())
            .map(
                existingCountry -> {
                    if (country.getCountryName() != null) {
                        existingCountry.setCountryName(country.getCountryName());
                    }

                    return existingCountry;
                }
            )
            .flatMap(countryRepository::save);
    }

    @Override
    public Flux<Country> findAll() {
        log.debug("Request to get all Countries");
        return countryRepository.findAll();
    }

    public Mono<Long> countAll() {
        return countryRepository.count();
    }

    @Override
    public Mono<Country> findOne(String id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Country : {}", id);
        return countryRepository.deleteById(id);
    }
}

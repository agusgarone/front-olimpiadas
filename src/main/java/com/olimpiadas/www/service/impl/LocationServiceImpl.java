package com.olimpiadas.www.service.impl;

import com.olimpiadas.www.domain.Location;
import com.olimpiadas.www.repository.LocationRepository;
import com.olimpiadas.www.service.LocationService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Location}.
 */
@Service
public class LocationServiceImpl implements LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Mono<Location> save(Location location) {
        log.debug("Request to save Location : {}", location);
        return locationRepository.save(location);
    }

    @Override
    public Mono<Location> partialUpdate(Location location) {
        log.debug("Request to partially update Location : {}", location);

        return locationRepository
            .findById(location.getId())
            .map(
                existingLocation -> {
                    if (location.getStreetAddress() != null) {
                        existingLocation.setStreetAddress(location.getStreetAddress());
                    }
                    if (location.getPostalCode() != null) {
                        existingLocation.setPostalCode(location.getPostalCode());
                    }
                    if (location.getCity() != null) {
                        existingLocation.setCity(location.getCity());
                    }
                    if (location.getStateProvince() != null) {
                        existingLocation.setStateProvince(location.getStateProvince());
                    }

                    return existingLocation;
                }
            )
            .flatMap(locationRepository::save);
    }

    @Override
    public Flux<Location> findAll() {
        log.debug("Request to get all Locations");
        return locationRepository.findAll();
    }

    public Mono<Long> countAll() {
        return locationRepository.count();
    }

    @Override
    public Mono<Location> findOne(String id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Location : {}", id);
        return locationRepository.deleteById(id);
    }
}

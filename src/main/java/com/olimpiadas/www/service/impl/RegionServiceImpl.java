package com.olimpiadas.www.service.impl;

import com.olimpiadas.www.domain.Region;
import com.olimpiadas.www.repository.RegionRepository;
import com.olimpiadas.www.service.RegionService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Region}.
 */
@Service
public class RegionServiceImpl implements RegionService {

    private final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);

    private final RegionRepository regionRepository;

    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public Mono<Region> save(Region region) {
        log.debug("Request to save Region : {}", region);
        return regionRepository.save(region);
    }

    @Override
    public Mono<Region> partialUpdate(Region region) {
        log.debug("Request to partially update Region : {}", region);

        return regionRepository
            .findById(region.getId())
            .map(
                existingRegion -> {
                    if (region.getRegionName() != null) {
                        existingRegion.setRegionName(region.getRegionName());
                    }

                    return existingRegion;
                }
            )
            .flatMap(regionRepository::save);
    }

    @Override
    public Flux<Region> findAll() {
        log.debug("Request to get all Regions");
        return regionRepository.findAll();
    }

    public Mono<Long> countAll() {
        return regionRepository.count();
    }

    @Override
    public Mono<Region> findOne(String id) {
        log.debug("Request to get Region : {}", id);
        return regionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Region : {}", id);
        return regionRepository.deleteById(id);
    }
}

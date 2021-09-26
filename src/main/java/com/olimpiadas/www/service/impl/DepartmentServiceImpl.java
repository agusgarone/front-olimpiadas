package com.olimpiadas.www.service.impl;

import com.olimpiadas.www.domain.Department;
import com.olimpiadas.www.repository.DepartmentRepository;
import com.olimpiadas.www.service.DepartmentService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Department}.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Mono<Department> save(Department department) {
        log.debug("Request to save Department : {}", department);
        return departmentRepository.save(department);
    }

    @Override
    public Mono<Department> partialUpdate(Department department) {
        log.debug("Request to partially update Department : {}", department);

        return departmentRepository
            .findById(department.getId())
            .map(
                existingDepartment -> {
                    if (department.getDepartmentName() != null) {
                        existingDepartment.setDepartmentName(department.getDepartmentName());
                    }

                    return existingDepartment;
                }
            )
            .flatMap(departmentRepository::save);
    }

    @Override
    public Flux<Department> findAll() {
        log.debug("Request to get all Departments");
        return departmentRepository.findAll();
    }

    public Mono<Long> countAll() {
        return departmentRepository.count();
    }

    @Override
    public Mono<Department> findOne(String id) {
        log.debug("Request to get Department : {}", id);
        return departmentRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Department : {}", id);
        return departmentRepository.deleteById(id);
    }
}

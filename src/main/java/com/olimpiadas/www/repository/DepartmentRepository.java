package com.olimpiadas.www.repository;

import com.olimpiadas.www.domain.Department;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Department entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartmentRepository extends ReactiveMongoRepository<Department, String> {}

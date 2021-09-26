package com.olimpiadas.www.service.impl;

import com.olimpiadas.www.domain.Task;
import com.olimpiadas.www.repository.TaskRepository;
import com.olimpiadas.www.service.TaskService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Task}.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Mono<Task> save(Task task) {
        log.debug("Request to save Task : {}", task);
        return taskRepository.save(task);
    }

    @Override
    public Mono<Task> partialUpdate(Task task) {
        log.debug("Request to partially update Task : {}", task);

        return taskRepository
            .findById(task.getId())
            .map(
                existingTask -> {
                    if (task.getTitle() != null) {
                        existingTask.setTitle(task.getTitle());
                    }
                    if (task.getDescription() != null) {
                        existingTask.setDescription(task.getDescription());
                    }

                    return existingTask;
                }
            )
            .flatMap(taskRepository::save);
    }

    @Override
    public Flux<Task> findAll() {
        log.debug("Request to get all Tasks");
        return taskRepository.findAll();
    }

    public Mono<Long> countAll() {
        return taskRepository.count();
    }

    @Override
    public Mono<Task> findOne(String id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Task : {}", id);
        return taskRepository.deleteById(id);
    }
}

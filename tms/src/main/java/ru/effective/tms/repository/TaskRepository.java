package ru.effective.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.effective.tms.model.entity.Task;

@Repository
public interface TaskRepository
        extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}

package com.besafx.app.service;

import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskCloseRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface TaskCloseRequestService extends PagingAndSortingRepository<TaskCloseRequest, Long>, JpaSpecificationExecutor<TaskCloseRequest> {

    long countByPersonAndTask(Person person, Task task);
}

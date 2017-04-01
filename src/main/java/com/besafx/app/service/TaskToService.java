package com.besafx.app.service;

import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskTo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface TaskToService extends PagingAndSortingRepository<TaskTo, Long>, JpaSpecificationExecutor<TaskTo> {

    List<TaskTo> findByTask(Task task);

    TaskTo findByTaskIdAndPersonId(Long taskId, Long personId);

    TaskTo findByTaskAndPerson(Task task, Person person);
}
